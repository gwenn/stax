package org.stax;

import org.codehaus.stax2.XMLStreamWriter2;

import javax.xml.stream.XMLStreamException;
import java.math.BigDecimal;

public class StaxWriter implements AutoCloseable {
    private final XMLStreamWriter2 xsw;

    public StaxWriter(XMLStreamWriter2 xsw) {
        this.xsw = xsw;
    }

    public void writeStartDocument()
            throws XMLStreamException {
        xsw.writeStartDocument();
    }
    public void writeStartDocument(String version, String encoding,
                                   boolean standAlone)
            throws XMLStreamException {
        xsw.writeStartDocument(version, encoding, standAlone);
    }
/*
    public AutoCloseElement autoCloseElement(String localName)
            throws XMLStreamException {
        xsw.writeStartElement(localName);
        return xsw::writeEndElement;
    }
*/
    public void writeStartElement(String localName)
            throws XMLStreamException {
        xsw.writeStartElement(localName);
    }

    public void writeAttribute(String localName,
                               String value)
            throws XMLStreamException {
        xsw.writeAttribute(localName, value);
    }

    public void writeElement(String localName, String text) throws XMLStreamException {
        if (text == null) {
            xsw.writeEmptyElement(localName);
        } else {
            xsw.writeStartElement(localName);
            xsw.writeCharacters(text);
            xsw.writeEndElement();
        }
    }

    public void writeElement(String localName, boolean value) throws XMLStreamException {
        xsw.writeStartElement(localName);
        xsw.writeBoolean(value);
        xsw.writeEndElement();
    }

    public void writeElement(String localName, int value) throws XMLStreamException {
        xsw.writeStartElement(localName);
        xsw.writeInt(value);
        xsw.writeEndElement();
    }

    public void writeElement(String localName, long value) throws XMLStreamException {
        xsw.writeStartElement(localName);
        xsw.writeLong(value);
        xsw.writeEndElement();
    }

    public void writeElement(String localName, float value) throws XMLStreamException {
        xsw.writeStartElement(localName);
        xsw.writeFloat(value);
        xsw.writeEndElement();
    }

    public void writeElement(String localName, double value) throws XMLStreamException {
        xsw.writeStartElement(localName);
        xsw.writeDouble(value);
        xsw.writeEndElement();
    }

    public void writeElement(String localName, BigDecimal value) throws XMLStreamException {
        if (value == null) {
            xsw.writeEmptyElement(localName);
        } else {
            xsw.writeStartElement(localName);
            xsw.writeDecimal(value);
            xsw.writeEndElement();
        }
    }

    public void writeEndElement()
            throws XMLStreamException {
        xsw.writeEndElement();
    }

    public void subTree(String localName, Callback callback) throws XMLStreamException {
        xsw.writeStartElement(localName);
        callback.x();
        xsw.writeEndElement();
    }

    @Override
    public void close() throws XMLStreamException {
        xsw.closeCompletely();
    }

    public interface Callback {
        void x() throws XMLStreamException;
    }
/*
    public interface AutoCloseElement extends AutoCloseable {
        @Override
        void close() throws XMLStreamException;
    }
*/
}
