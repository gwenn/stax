package org.stax;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLStreamReader2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link StaxReader}.
 */
class StaxReaderImpl implements StaxReader, AutoCloseable {
	private static final Logger log = LoggerFactory.getLogger(StaxReaderImpl.class);
	private final XMLStreamReader2 xsr;
	private final Deque<Entry> stack = new ArrayDeque<>();
	private Entry entry;

	StaxReaderImpl(XMLStreamReader2 xsr) {
		this.xsr = Objects.requireNonNull(xsr);
	}

	void parse(StaxHandler handler) throws XMLStreamException {
		entry = new Entry(handler, xsr.getDepth(), null);
		while (xsr.hasNext()) {
			xsr.next();
			if (xsr.isStartElement()) {
				entry.handler.start(this, xsr.getLocalName());
			} else if (xsr.isEndElement()) {
				final int depth = xsr.getDepth();
				if (entry.depth >= depth) {
					final String name = xsr.getLocalName();
					assert entry.depth == depth : "expected depth: " + entry.depth + " but got: " + depth;
					assert entry.name.equals(name) : "expected name: '" + entry.name + "' but got: '" + name + "'";
					if (entry.handler.end()) {
						log.debug("Stop parsing at {} ({})", name, depth);
						stack.clear();
						break;
					} else {
						log.debug("Pop handler at {} ({})", name, depth);
						entry = stack.pop();
					}
				}
			}
		}
		xsr.closeCompletely();
	}

	@Override
	public void push(StaxHandler handler) {
		int depth = xsr.getDepth();
		if (entry != null && entry.depth == depth) {
			throw new IllegalStateException();
		}
		stack.push(entry);
		entry = new Entry(handler, depth, xsr.getLocalName());
	}

	@Override
	public String getElementText() throws XMLStreamException {
		if (xsr.isEmptyElement()) { // TODO Validate
			return null;
		}
		return xsr.getElementText();
	}
	@Override
	public boolean getElementAsBoolean() throws XMLStreamException {
		return xsr.getElementAsBoolean();
	}
	@Override
	public int getElementAsInt() throws XMLStreamException {
		return xsr.getElementAsInt();
	}
	@Override
	public long getElementAsLong() throws XMLStreamException {
		return xsr.getElementAsLong();
	}
	@Override
	public float getElementAsFloat() throws XMLStreamException {
		return xsr.getElementAsFloat();
	}
	@Override
	public double getElementAsDouble() throws XMLStreamException {
		return xsr.getElementAsDouble();
	}
	@Override
	public String getAttributeValue(String localName) {
		return xsr.getAttributeValue(null, localName);
	}
	@Override
	public void skipElement() throws XMLStreamException {
		xsr.skipElement();
	}
	@Override
	public boolean isEmptyElement() throws XMLStreamException {
		return xsr.isEmptyElement();
	}
	@Override
	public void close() throws XMLStreamException {
		xsr.closeCompletely();
	}

	private static class Entry {
		private final StaxHandler handler;
		private final int depth;
		private final String name; // only used for debug

		private Entry(StaxHandler handler, int depth, String name) {
			this.handler = Objects.requireNonNull(handler);
			this.depth = depth;
			this.name = name;
		}
	}
}
