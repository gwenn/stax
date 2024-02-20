package org.stax;

import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.typed.TypedXMLStreamWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.math.BigDecimal;

/**
 * Convenient wrapper around a {@link XMLStreamWriter2}.
 * {@link XMLStreamWriter2#writeStartElement(String)} / {@link XMLStreamWriter2#writeEndElement()} are not exposed.
 * {@link #subTree(String, Callback)} method must be used instead.
 * Or for textual element, {@link #writeElement(String, String)} should be used.
 */
public class StaxWriter implements AutoCloseable {
    private final XMLStreamWriter2 xsw;

    public StaxWriter(XMLStreamWriter2 xsw) {
        this.xsw = xsw;
    }

    /**
     * {@link XMLStreamWriter#writeStartDocument()}
     */
    public void writeStartDocument() throws XMLStreamException {
        xsw.writeStartDocument();
    }
    /**
     * {@link XMLStreamWriter2#writeStartDocument(String, String, boolean)}
     */
    public void writeStartDocument(String version, String encoding, boolean standAlone) throws XMLStreamException {
        xsw.writeStartDocument(version, encoding, standAlone);
    }

    private void writeStartElement(String localName) throws XMLStreamException {
        xsw.writeStartElement(localName);
    }

    /**
     * {@link XMLStreamWriter#writeAttribute(String, String)}
     */
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        xsw.writeAttribute(localName, value);
    }

    /**
     * {@link TypedXMLStreamWriter#writeIntAttribute(String, String, String, int)}
     */
    public void writeAttribute(String localName, int value) throws XMLStreamException {
        xsw.writeIntAttribute(null, null, localName, value);
    }

    /**
     * Auto-wrap {@link XMLStreamWriter#writeCharacters(String)}
     */
    public void writeElement(String localName, String text) throws XMLStreamException {
        if (text == null) {
            xsw.writeEmptyElement(localName);
        } else {
            xsw.writeStartElement(localName);
            xsw.writeCharacters(text);
            xsw.writeEndElement();
        }
    }

    /**
     * Auto-wrap {@link TypedXMLStreamWriter#writeBoolean(boolean)}
     */
    public void writeElement(String localName, boolean value) throws XMLStreamException {
        xsw.writeStartElement(localName);
        xsw.writeBoolean(value);
        xsw.writeEndElement();
    }

    /**
     * Auto-wrap {@link TypedXMLStreamWriter#writeInt(int)}
     */
    public void writeElement(String localName, int value) throws XMLStreamException {
        xsw.writeStartElement(localName);
        xsw.writeInt(value);
        xsw.writeEndElement();
    }

    /**
     * Auto-wrap {@link TypedXMLStreamWriter#writeLong(long)}
     */
    public void writeElement(String localName, long value) throws XMLStreamException {
        xsw.writeStartElement(localName);
        xsw.writeLong(value);
        xsw.writeEndElement();
    }

    /**
     * Auto-wrap {@link TypedXMLStreamWriter#writeFloat(float)}
     */
    public void writeElement(String localName, float value) throws XMLStreamException {
        xsw.writeStartElement(localName);
        xsw.writeFloat(value);
        xsw.writeEndElement();
    }

    /**
     * Auto-wrap {@link TypedXMLStreamWriter#writeDouble(double)}
     */
    public void writeElement(String localName, double value) throws XMLStreamException {
        xsw.writeStartElement(localName);
        xsw.writeDouble(value);
        xsw.writeEndElement();
    }

    /**
     * Auto-wrap {@link TypedXMLStreamWriter#writeDecimal(BigDecimal)}
     */
    public void writeElement(String localName, BigDecimal value) throws XMLStreamException {
        if (value == null) {
            xsw.writeEmptyElement(localName);
        } else {
            xsw.writeStartElement(localName);
            xsw.writeDecimal(value);
            xsw.writeEndElement();
        }
    }

    /**
     * {@link XMLStreamWriter#writeEmptyElement(String)}
     */
    public void writeEmptyElement(String localName) throws XMLStreamException {
        xsw.writeEmptyElement(localName);
    }

    private void writeEndElement() throws XMLStreamException {
        xsw.writeEndElement();
    }

    /**
     * <pre>{@code
     * writeStartElement(localName);
     * callback.write();
     * writeEndElement();
     * }</pre>
     */
    public void subTree(String localName, Callback callback) throws XMLStreamException {
        xsw.writeStartElement(localName);
        callback.write();
        xsw.writeEndElement();
    }

    /**
     * {@link XMLStreamWriter2#copyEventFromReader(XMLStreamReader2, boolean)}
     */
    public void copyEventFromReader(XMLStreamReader2 r, boolean preserveEventData) throws XMLStreamException {
        xsw.copyEventFromReader(r, preserveEventData);
    }

    /**
     * {@link XMLStreamWriter#writeComment(String)}
     */
    public void writeComment(String data) throws XMLStreamException {
        xsw.writeComment(data);
    }

    /**
     * {@link XMLStreamWriter#flush()}
     */
    public void flush() throws XMLStreamException {
        xsw.flush();
    }

    /**
     * {@link XMLStreamWriter2#closeCompletely()}
     */
    @Override
    public void close() throws XMLStreamException {
        xsw.closeCompletely();
    }

    /**
     * {@link #subTree(String, Callback)}
     */
    public interface Callback {
        void write() throws XMLStreamException;
    }
}
