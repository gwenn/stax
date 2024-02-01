package org.stax;

import javax.xml.stream.XMLStreamException;

/**
 * Like SAX event {@link org.xml.sax.ContentHandler handler} but for StAX.
 */
@FunctionalInterface
public interface StaxHandler<S> {
	/**
	 * Receive notification of the start of an element.
	 *
	 * @param name XML element name
	 * @param state associated state
	 */
	void start(StaxReader sr, String name, S state) throws XMLStreamException;
	/**
	 * Receive notification of the end of the element associated to this current handler (when it was {@link StaxReader#push pushed}).
	 *
	 * @param state associated state
	 * @return {@code true} to stop parsing
	 */
	default boolean end(S state) {
		return false;
	}
}
