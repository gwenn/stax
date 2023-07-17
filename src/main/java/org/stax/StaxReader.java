package org.stax;

import java.math.BigDecimal;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLStreamReader2;

/**
 * {@link XMLStreamReader2} + stack of {@link StaxHandler}
 */
public interface StaxReader {
	/**
	 * @param xsr     {@code (XMLStreamReader2) XMLInputFactory.newInstance().createXMLStreamReader(xml)}
	 * @param handler root handler
	 */
	static void parse(XMLStreamReader2 xsr, StaxHandler handler) throws XMLStreamException {
		try (StaxReaderImpl sr = new StaxReaderImpl(xsr)) {
			sr.parse(handler);
		}
	}

	/**
	 * Use {@code handler} to process descendants.
	 * {@code handler} will be automatically popped when the current XML element ends.
	 */
	void push(StaxHandler handler);

	/**
	 * See {@link XMLStreamReader#getElementText()}
	 */
	String getElementText() throws XMLStreamException;
	/**
	 * See {@link XMLStreamReader2#getElementAsBoolean()}
	 */
	boolean getElementAsBoolean() throws XMLStreamException;
	/**
	 * See {@link XMLStreamReader2#getElementAsInt()}
	 */
	int getElementAsInt() throws XMLStreamException;
	/**
	 * See {@link XMLStreamReader2#getElementAsLong()}
	 */
	long getElementAsLong() throws XMLStreamException;
	/**
	 * See {@link XMLStreamReader2#getElementAsFloat()}
	 */
	float getElementAsFloat() throws XMLStreamException;
	/**
	 * See {@link XMLStreamReader2#getElementAsDouble()}
	 */
	double getElementAsDouble() throws XMLStreamException;
	/**
	 * See {@link XMLStreamReader2#getElementAsDecimal()}
	 */
	BigDecimal getElementAsDecimal() throws XMLStreamException;

	/**
	 * See {@link XMLStreamReader#getAttributeValue(String, String)}
	 */
	String getAttributeValue(/*String namespaceURI, */String localName);

	/**
	 * See {@link XMLStreamReader2#skipElement()}
	 */
	void skipElement() throws XMLStreamException;
	/**
	 * See {@link XMLStreamReader2#isEmptyElement()}
	 */
	boolean isEmptyElement() throws XMLStreamException;
}
