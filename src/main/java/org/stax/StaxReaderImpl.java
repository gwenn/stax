package org.stax;

import java.math.BigDecimal;
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
	private final Deque<Entry<?>> stack = new ArrayDeque<>();
	@SuppressWarnings("rawtypes")
	private Entry entry;

	StaxReaderImpl(XMLStreamReader2 xsr) {
		this.xsr = Objects.requireNonNull(xsr);
	}

	<S> void parse(StaxHandler<S> handler, S state) throws XMLStreamException {
		entry = new Entry<>(handler, xsr.getDepth(), null, state);
		while (xsr.hasNext()) {
			xsr.next();
			if (xsr.isStartElement()) {
				//noinspection unchecked
				entry.handler.start(entry.state, this, xsr.getLocalName());
			} else if (xsr.isEndElement()) {
				final int depth = xsr.getDepth();
				if (entry.depth >= depth) {
					final String name = xsr.getLocalName();
					assert entry.depth == depth : "expected depth: " + entry.depth + " but got: " + depth;
					assert entry.name.equals(name) : "expected name: '" + entry.name + "' but got: '" + name + "'";
					//noinspection unchecked
					if (entry.handler.end(entry.state)) {
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
	public <S> void push(StaxHandler<S> handler, S state) {
		int depth = xsr.getDepth();
		if (entry != null && entry.depth == depth) {
			throw new IllegalStateException();
		}
		stack.push(entry);
		entry = new Entry<>(handler, depth, xsr.getLocalName(), state);
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
	public BigDecimal getElementAsDecimal() throws XMLStreamException {
		return xsr.getElementAsDecimal();
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

	private static class Entry<S> {
		private final StaxHandler<S> handler;
		private final int depth;
		private final String name; // only used for debug
		private final S state;

		private Entry(StaxHandler<S> handler, int depth, String name, S state) {
			this.handler = Objects.requireNonNull(handler);
			this.depth = depth;
			this.name = name;
			this.state = state;
		}
	}
}
