package org.stax;

import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

public interface Stax {
    /**
     * Copy event(s) from reader to writer
     */
    static void copy(XMLStreamReader2 xsr, XMLStreamWriter2 xsw, Copier copier) throws XMLStreamException {
        try {
            copier.copy(xsr, xsw);
            while (xsr.hasNext()) {
                xsr.next();
                copier.copy(xsr, xsw);
            }
        } finally {
            xsr.closeCompletely();
            xsw.closeCompletely();
        }
    }
    /**
     * Skip all {@link XMLStreamReader#isWhiteSpace()}
     */
    static void compact(XMLStreamReader2 xsr, XMLStreamWriter2 xsw) throws XMLStreamException {
        copy(xsr, xsw, (r, w) -> {
            if (!r.isWhiteSpace()) {
                xsw.copyEventFromReader(xsr, false);
            }
        });
    }
    static String compact(String xml) throws XMLStreamException {
        StringWriter writer = new StringWriter();
        compact((XMLStreamReader2) XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml))
                , (XMLStreamWriter2) XMLOutputFactory.newInstance().createXMLStreamWriter(writer));
        return writer.toString();
    }

    /**
     * See {@link #copy(XMLStreamReader2, XMLStreamWriter2, Copier)}
     */
    @FunctionalInterface
    interface Copier {
        /**
         * See {@link XMLStreamWriter2#copyEventFromReader(XMLStreamReader2, boolean)}
         */
        void copy(XMLStreamReader2 xsr, XMLStreamWriter2 xsw) throws XMLStreamException;
    }
}
