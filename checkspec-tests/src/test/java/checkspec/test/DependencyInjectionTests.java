package checkspec.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import checkspec.api.Inject;
import checkspec.examples.example1.Calculator;

public class DependencyInjectionTests {

	@Inject
	private Calculator calc;

	@Test
	public void addTests() {
		assertEquals(3, calc.add(1, 2));
	}
}
