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
			StaxReader.parse(xsr, (r, name, f) -> handleRootChildElement(f, r, name), food);
			assertEquals(3, food.animals.size());
			assertEquals(3, food.vegetables.size());
		}
	}
	private void handleRootChildElement(Food food, StaxReader sr, String name) {
		if ("animals".equals(name)) {
			sr.push((r, n, a) -> handleAnimals(a, r, n), food.animals);
		} else if ("vegetables".equals(name)) {
			sr.push((r, n, v) -> handleVegetables(v, r, n), food.vegetables);
		}
	}
	private void handleVegetables(List<Vegetable> vegetables, StaxReader sr, String name) throws XMLStreamException {
		if ("vegetable".equals(name)) {
			Vegetable vegetable = new Vegetable();
			sr.push((r, n, v) -> extractVegetable(v, r, n), vegetable);
			vegetables.add(vegetable);
		} else {
			sr.skipElement();
		}
	}
	private void extractVegetable(Vegetable vegetable, StaxReader sr, String name) throws XMLStreamException {
		if ("name".equals(name)) {
			vegetable.name = sr.getElementText();
		} else if ("preparations".equals(name)) {
			sr.push((r, n, p) -> {
				assert "preparation".equals(n) : name;
				p.add(r.getElementText());
			}, vegetable.preparations);
		}
	}

	private void handleAnimals(List<Animal> animals, StaxReader sr, String name) {
		assert "animal".equals(name) : name;
		Animal animal = new Animal(sr.getAttributeValue("name"));
		sr.push((r, n, a) -> extractAnimal(a, r, n), animal);
		animals.add(animal);
	}
	private void extractAnimal(Animal animal, StaxReader sr, String name) {
		assert "meat".equals(name) : name;
		sr.push((r, n, m) -> {
			assert "name".equals(n) : name;
			m.add(new Meat(r.getElementText()));
		}, animal.meats);
	}
}