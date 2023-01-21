package org.stax;

import java.util.ArrayList;
import java.util.List;

final class Animal {
	final String name;
	final List<Meat> meats = new ArrayList<>();

	Animal(String name) {
		this.name = name;
	}
}
