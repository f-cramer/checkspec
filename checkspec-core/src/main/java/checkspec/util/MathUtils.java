package checkspec.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class MathUtils {

	public static int multiplyWithoutOverflow(int x, int y) {
		try {
			return Math.multiplyExact(x, y);
		} catch (ArithmeticException e) {
			return Math.signum(x) == Math.signum(y) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		}
	}
}
