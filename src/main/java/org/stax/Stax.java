package org.stax;

import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.io.Stax2ByteArraySource;
import org.codehaus.stax2.io.Stax2FileSource;
import org.codehaus.stax2.io.Stax2StringSource;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import java.io.File;

public interface Stax {
    static Source from(String text) {
        return new Stax2StringSource(text);
    }
    static Source from(byte[] buf) {
        return new Stax2ByteArraySource(buf, 0, buf.length);
    }
    static Source from(File f) {
        return new Stax2FileSource(f);
    }

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
