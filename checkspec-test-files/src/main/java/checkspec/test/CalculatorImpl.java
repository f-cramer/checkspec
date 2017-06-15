package checkspec.test;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class CalculatorImpl extends ArrayList<Integer> {

	private static final long serialVersionUID = -3637983294990560884L;

	private int abc;

	public int add(int a, int b) {
		return a + b;
	}

	public int subtract(int a, int b) {
		return a - b;
	}

	public int multiply(int a, int b) {
		return a * b;
	}

	public int divide(int a, int b) {
		return a / b;
	}
}
