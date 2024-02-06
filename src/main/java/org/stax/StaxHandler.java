package org.stax;

import javax.xml.stream.XMLStreamException;

/**
 * Like SAX event {@link org.xml.sax.ContentHandler handler} but for StAX.
 */
@FunctionalInterface
public interface StaxHandler {
	/**
	 * @param expected element(s) name
	 * @param child handler for element descendants
	 * @return simple handler
	 */
	static StaxHandler pushOrSkip(String expected, StaxHandler child) {
		return (sr, name) -> {
			if (expected.equals(name)) {
				sr.push(child);
			} else {
				sr.skipElement();
			}
		};
	}

	/**
	 * Receive notification of the start of an element.
	 *
	 * @param name XML element name
	 */
	void start(StaxReader sr, String name) throws XMLStreamException;
	/**
	 * Receive notification of the end of the element associated to this current handler (when it was {@link StaxReader#push pushed}).
	 *
	 * @return {@code true} to stop parsing
	 */
	default boolean end() {
		return false;
	}
}
