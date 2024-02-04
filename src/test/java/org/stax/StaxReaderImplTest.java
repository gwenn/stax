package org.stax;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLStreamReader2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Adapted from <a href="http://blog.palominolabs.com/2013/03/06/parsing-xml-with-java-and-staxmate/">Practical XML Parsing With Java and StaxMate</a>
 * (<a href="https://github.com/palominolabs/staxmate-example">StAX</a>)
 */
public class StaxReaderImplTest {
	@Test
	public void parse() throws Exception {
		load();
	}

	Food load() throws IOException, XMLStreamException {
		try (InputStream is = this.getClass().getResourceAsStream("/sample.xml")) {
			XMLStreamReader2 xsr = (XMLStreamReader2) XMLInputFactory.newInstance().createXMLStreamReader(is);
			Food food = new Food();
			StaxReader.parse(xsr, this::handleRootChildElement, food);
			assertEquals(3, food.animals.size());
			assertEquals(3, food.vegetables.size());
			return food;
		}
	}

	private void handleRootChildElement(Food food, StaxReader sr, String name) {
		if ("animals".equals(name)) {
			sr.push(this::handleAnimals, food.animals);
		} else if ("vegetables".equals(name)) {
			sr.push(this::handleVegetables, food.vegetables);
		}
	}
	private void handleVegetables(List<Vegetable> vegetables, StaxReader sr, String name) throws XMLStreamException {
		if ("vegetable".equals(name)) {
			Vegetable vegetable = new Vegetable();
			sr.push(this::extractVegetable, vegetable);
			vegetables.add(vegetable);
		} else {
			sr.skipElement();
		}
	}
	private void extractVegetable(Vegetable vegetable, StaxReader sr, String name) throws XMLStreamException {
		if ("name".equals(name)) {
			vegetable.name = sr.getElementText();
		} else if ("preparations".equals(name)) {
			sr.push((p, r, n) -> {
				sr.require("preparation");
				p.add(r.getElementText());
			}, vegetable.preparations);
		}
	}

	private void handleAnimals(List<Animal> animals, StaxReader sr, String name) throws XMLStreamException {
		sr.require("animal");
		Animal animal = new Animal(sr.getAttributeValue("name"));
		sr.push(this::extractAnimal, animal);
		animals.add(animal);
	}
	private void extractAnimal(Animal animal, StaxReader sr, String name) throws XMLStreamException {
		sr.require("meat");
		sr.push((m, r, n) -> {
			assert "name".equals(n) : name;
			m.add(new Meat(r.getElementText()));
		}, animal.meats);
	}
}