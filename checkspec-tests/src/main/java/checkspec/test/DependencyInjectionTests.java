package checkspec.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import checkspec.annotation.Inject;

public class DependencyInjectionTests {

	@Inject
	private Calc calc;
	
	@Test
	public void addTests() {
		assertEquals(3, calc.add(1, 2));
	}
}
