package org.stax;

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
		try (InputStream is = this.getClass().getResourceAsStream("/sample.xml")) {
			XMLStreamReader2 xsr = (XMLStreamReader2) XMLInputFactory.newInstance().createXMLStreamReader(is);
			Food food = new Food();
			StaxReader.parse(xsr, (r, name) -> handleRootChildElement(food, r, name));
			assertEquals(3, food.animals.size());
			assertEquals(3, food.vegetables.size());
		}
	}
	private void handleRootChildElement(Food food, StaxReader sr, String name) {
		if ("animals".equals(name)) {
			sr.push((r, n) -> handleAnimals(food.animals, r));
		} else if ("vegetables".equals(name)) {
			sr.push((r, n) -> handleVegetables(food.vegetables, r, n));
		}
	}
	private void handleVegetables(List<Vegetable> vegetables, StaxReader sr, String name) throws XMLStreamException {
		if ("vegetable".equals(name)) {
			Vegetable vegetable = new Vegetable();
			sr.push((r, n) -> extractVegetable(vegetable, r, n));
			vegetables.add(vegetable);
		} else {
			sr.skipElement();
		}
	}
	private void extractVegetable(Vegetable vegetable, StaxReader sr, String name) throws XMLStreamException {
		if ("name".equals(name)) {
			vegetable.name = sr.getElementText();
		} else if ("preparations".equals(name)) {
			sr.push((r, n) -> {
				sr.require("preparation");
				vegetable.preparations.add(r.getElementText());
			});
		}
	}

	private void handleAnimals(List<Animal> animals, StaxReader sr) throws XMLStreamException {
		sr.require("animal");
		Animal animal = new Animal(sr.getAttributeValue("name"));
		sr.push((r, n) -> extractAnimal(animal, r));
		animals.add(animal);
	}
	private void extractAnimal(Animal animal, StaxReader sr) throws XMLStreamException {
		sr.require("meat");
		sr.push((r, n) -> {
			sr.require("name");
			animal.meats.add(new Meat(r.getElementText()));
		});
	}
}