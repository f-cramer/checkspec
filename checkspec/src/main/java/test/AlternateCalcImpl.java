package test;

public abstract class AlternateCalcImpl {

	public abstract int add(int a, int b);

	int subtract(int a, int b) {
		return a;
	}

	public double multiply(int a, int b) {
		return a;
	}

	public Object divide(int a, int b) {
		return null;
	}
}
