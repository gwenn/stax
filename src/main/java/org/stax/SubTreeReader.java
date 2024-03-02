package org.stax;

import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static javax.xml.stream.XMLStreamConstants.*;

public class SubTreeReader implements AutoCloseable {
    private final XMLStreamReader2 xsr;
    private final int initialDepth;
    private boolean eos = false; // end of stream / tree

    public SubTreeReader(XMLStreamReader2 xsr) throws XMLStreamException {
        int eventType = xsr.getEventType();
        if (eventType != START_ELEMENT && eventType != START_DOCUMENT) {
            throw new XMLStreamException("parser must be on START_ELEMENT to read sub tree", xsr.getLocation());
        }
        this.xsr = xsr;
        initialDepth = xsr.getDepth();
    }

    public SubTreeReader readSubTree() throws XMLStreamException {
        return new SubTreeReader(xsr);
    }

    /**
     * See {@link XMLStreamReader#next()}
     */
    public int next() throws XMLStreamException {
        if (eos) {
            throw new NoSuchElementException();
        }
        int type = xsr.next();
        checkEos(type);
        return type;
    }

    private void checkEos(int type) {
        if (type == END_ELEMENT && xsr.getDepth() == initialDepth) {
            eos = true;
        }
    }

    /**
     * See {@link XMLStreamReader#hasNext()}
     */
    public boolean hasNext() throws XMLStreamException {
        return !eos && xsr.hasNext();
    }

    /**
     * See {@link XMLStreamReader#getElementText()}
     */
    public String getElementText() throws XMLStreamException {
        return xsr.getElementText();
    }
    /**
     * See {@link XMLStreamReader2#getElementAsBoolean()}
     */
    public boolean getElementAsBoolean() throws XMLStreamException {
        return xsr.getElementAsBoolean();
    }
    /**
     * See {@link XMLStreamReader2#getElementAsInt()}
     */
    public int getElementAsInt() throws XMLStreamException {
        return xsr.getElementAsInt();
    }
    /**
     * See {@link XMLStreamReader2#getElementAsLong()}
     */
    public long getElementAsLong() throws XMLStreamException {
        return xsr.getElementAsLong();
    }
    /**
     * See {@link XMLStreamReader2#getElementAsFloat()}
     */
    public float getElementAsFloat() throws XMLStreamException {
        return xsr.getElementAsFloat();
    }
    /**
     * See {@link XMLStreamReader2#getElementAsDouble()}
     */
    public double getElementAsDouble() throws XMLStreamException {
        return xsr.getElementAsDouble();
    }
    /**
     * See {@link XMLStreamReader2#getElementAsDecimal()}
     */
    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        return xsr.getElementAsDecimal();
    }
    /**
     * See {@link XMLStreamReader#getAttributeValue(String, String)}
     */
    public String getAttributeValue(String localName) {
        return xsr.getAttributeValue(null, localName);
    }
    /**
     * See {@link XMLStreamReader2#skipElement()}
     */
    public void skipElement() throws XMLStreamException {
        xsr.skipElement();
        checkEos(xsr.getEventType());
    }
    /**
     * See {@link XMLStreamReader2#isEmptyElement()}
     */
    public boolean isEmptyElement() throws XMLStreamException {
        return xsr.isEmptyElement();
    }
    @Override
    public void close() throws XMLStreamException {
        if (initialDepth == 0) {
            xsr.closeCompletely();
        } else {
            while (hasNext()) {
                next();
            }
        }
    }
}
