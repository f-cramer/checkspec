package checkspec.examples.example1;

@SuppressWarnings("unused")
public abstract class AlternateCalculatorImpl {

	private int abc;

	public abstract int add(int a, int b, Integer c);

	public int multiply(int a, int b) {
		return a * b;
	}

	public Integer divide(int a, int b) {
		return null;
	}

	public int subtract(int x, int b) {
		return x + b;
	}
}
