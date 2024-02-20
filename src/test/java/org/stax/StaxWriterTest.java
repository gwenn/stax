package org.stax;

import org.codehaus.stax2.XMLStreamWriter2;
import org.junit.Test;

import javax.xml.stream.XMLOutputFactory;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

public class StaxWriterTest {
    @Test
    public void write() throws Exception {
        final Food food = StaxReaderImplTest.load();
        try (StringWriter out = new StringWriter();
             StaxWriter sw = new StaxWriter((XMLStreamWriter2) XMLOutputFactory.newInstance().createXMLStreamWriter(out))
        ) {
            sw.writeStartDocument();
            sw.subTree("deliciousFoods", () -> {
                sw.subTree("animals", () -> {
                    for (Animal a : food.animals) {
                        sw.subTree("animal", () -> {
                            sw.writeAttribute("name", a.name);
                            for (Meat m : a.meats) {
                                sw.subTree("meat", () -> sw.writeElement("name", m.name));
                            }
                        });
                    }
                });
                sw.flush();
                sw.subTree("vegetables", () -> {
                    for (Vegetable v : food.vegetables) {
                        sw.subTree("vegetable", () -> {
                            sw.writeElement("name", v.name);
                            sw.subTree("preparations", () -> {
                                for (String p : v.preparations) {
                                    sw.writeElement("preparation", p);
                                }
                            });
                        });
                    }
                });
            });
            sw.close();
            assertTrue(out.getBuffer().length() > 0);
        }
    }
}
