package org.stax;

import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.typed.TypedXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.util.Arrays;

import static java.lang.Math.min;

/**
 * Convenient wrapper around a {@link XMLStreamWriter2}.
 * {@link XMLStreamWriter2#writeStartElement(String)} / {@link XMLStreamWriter2#writeEndElement()} are not exposed.
 * {@link #subTree(String, Callback)} method must be used instead.
 * Or for textual element, {@link #writeElement(String, String)} should be used.
 */
public class StaxWriter implements AutoCloseable {
    private static final char[] SPACES = new char[64];
    static {
        Arrays.fill(SPACES, ' ');
    }
    private final XMLStreamWriter2 xsw;
    private boolean pretty;
    private int depth;
    private boolean startElement;

    /**
     * @param result like {@code new StreamResult(Paths.get(...).toFile()}
     */
    public static StaxWriter from(Result result) throws XMLStreamException {
        return new StaxWriter((XMLStreamWriter2) XMLOutputFactory.newInstance().createXMLStreamWriter(result));
    }

    public StaxWriter(XMLStreamWriter2 xsw) {
        this.xsw = xsw;
    }

    /**
     * {@link XMLStreamWriter#writeStartDocument()}
     */
    public void writeStartDocument() throws XMLStreamException {
        xsw.writeStartDocument();
        if (pretty) {
            xsw.writeSpace("\n");
        }
    }
    /**
     * {@link XMLStreamWriter2#writeStartDocument(String, String, boolean)}
     */
    public void writeStartDocument(String version, String encoding, boolean standAlone) throws XMLStreamException {
        xsw.writeStartDocument(version, encoding, standAlone);
        if (pretty) {
            xsw.writeSpace("\n");
        }
    }

    private void writeStartElement(String localName) throws XMLStreamException {
        if (pretty && depth > 0) {
            indent();
        }
        startElement = true;
        ++depth;
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
            writeEmptyElement(localName);
        } else {
            writeStartElement(localName);
            xsw.writeCharacters(text);
            writeEndElement();
        }
    }

    /**
     * Auto-wrap {@link TypedXMLStreamWriter#writeBoolean(boolean)}
     */
    public void writeElement(String localName, boolean value) throws XMLStreamException {
        writeStartElement(localName);
        xsw.writeBoolean(value);
        writeEndElement();
    }

    /**
     * Auto-wrap {@link TypedXMLStreamWriter#writeInt(int)}
     */
    public void writeElement(String localName, int value) throws XMLStreamException {
        writeStartElement(localName);
        xsw.writeInt(value);
        writeEndElement();
    }

    /**
     * Auto-wrap {@link TypedXMLStreamWriter#writeLong(long)}
     */
    public void writeElement(String localName, long value) throws XMLStreamException {
        writeStartElement(localName);
        xsw.writeLong(value);
        writeEndElement();
    }

    /**
     * Auto-wrap {@link TypedXMLStreamWriter#writeFloat(float)}
     */
    public void writeElement(String localName, float value) throws XMLStreamException {
        writeStartElement(localName);
        xsw.writeFloat(value);
        writeEndElement();
    }

    /**
     * Auto-wrap {@link TypedXMLStreamWriter#writeDouble(double)}
     */
    public void writeElement(String localName, double value) throws XMLStreamException {
        writeStartElement(localName);
        xsw.writeDouble(value);
        writeEndElement();
    }

    /**
     * Auto-wrap {@link TypedXMLStreamWriter#writeDecimal(BigDecimal)}
     */
    public void writeElement(String localName, BigDecimal value) throws XMLStreamException {
        if (value == null) {
            writeEmptyElement(localName);
        } else {
            writeStartElement(localName);
            xsw.writeDecimal(value);
            writeEndElement();
        }
    }

    /**
     * {@link XMLStreamWriter#writeEmptyElement(String)}
     */
    public void writeEmptyElement(String localName) throws XMLStreamException {
        if (pretty) {
            indent();
        }
        xsw.writeEmptyElement(localName);
    }

    private void writeEndElement() throws XMLStreamException {
        --depth;
        if (startElement) { // simple element (no child)
            startElement = false;
        } else if (pretty) {
            indent();
        }
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
        writeStartElement(localName);
        callback.write();
        writeEndElement();
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

    public void pretty(boolean pretty) {
        this.pretty = pretty;
    }
    private void indent() throws XMLStreamException {
        xsw.writeSpace("\n");
        xsw.writeRaw(SPACES, 0, min(depth*2, SPACES.length));
    }
}
