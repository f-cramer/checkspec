package checkspec.util;

public class MathUtils {

	public static int multiplyWithoutOverflow(int x, int y) {
		try {
			return Math.multiplyExact(x, y);
		} catch (ArithmeticException e) {
			return Math.signum(x) == Math.signum(y) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		}
	}
}
