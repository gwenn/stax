package org.stax;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.stax.StaxHandler.require;
import static org.stax.StaxHandler.stateful;

/**
 * Adapted from <a href="http://blog.palominolabs.com/2013/03/06/parsing-xml-with-java-and-staxmate/">Practical XML Parsing With Java and StaxMate</a>
 * (<a href="https://github.com/palominolabs/staxmate-example">StAX</a>)
 */
public class StaxReaderImplTest {
	@Test
	public void parse() throws Exception {
		load();
	}

	static Food load() throws IOException, XMLStreamException {
		try (InputStream is = StaxReaderImplTest.class.getResourceAsStream("/sample.xml")) {
			Food food = new Food(); // ~ created even if there is no <deliciousFoods/>
			StaxReader.parse(new StreamSource(is), require("deliciousFoods", (r, name) -> handleRootChildElement(food, r, name)));
			assertEquals(3, food.animals.size());
			assertEquals(3, food.vegetables.size());
			return food;
		}
	}

	static void handleRootChildElement(Food food, StaxReader sr, String name) {
		if ("animals".equals(name)) {
			sr.push(stateful((r, n) -> {
				sr.require("animal");
				return new Animal(sr.getAttributeValue("name"));
			}, StaxReaderImplTest::extractAnimal, food.animals::add));
		} else if ("vegetables".equals(name)) {
			sr.push(stateful((r, n) -> {
				sr.require("vegetable");
				return new Vegetable();
			}, StaxReaderImplTest::extractVegetable, food.vegetables::add));
		}
	}
	private static void extractVegetable(Vegetable vegetable, StaxReader sr, String name) throws XMLStreamException {
		if ("name".equals(name)) {
			vegetable.name = sr.getElementText();
		} else if ("preparations".equals(name)) {
			sr.push((r, n) -> {
				sr.require("preparation");
				vegetable.preparations.add(r.getElementText());
			});
		}
	}
	private static void extractAnimal(Animal animal, StaxReader sr, String name) throws XMLStreamException {
		sr.require("meat");
		sr.push((r, n) -> {
			sr.require("name");
			animal.meats.add(new Meat(r.getElementText()));
		});
	}
}