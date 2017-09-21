package checkspec.util;

import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods that are working with calculations. Mainly for internal
 * use within the framework itself.
 *
 * @author Florian Cramer
 * @see Math
 */
@UtilityClass
public final class MathUtils {

	/**
	 * Returns the product of {@code x} and {@code y}. If the calculation would
	 * leed to an over- or underflow {@code Integer#MAX_VALUE} or
	 * {@code Integer#MIN_VALUE} are returned.
	 *
	 * @param x
	 *            the first factor
	 * @param y
	 *            the second factory
	 * @return the product of x and y or {@code Integer#MAX_VALUE} or
	 *         {@code Integer#MIN_VALUE} if and over- or underflow occurres.
	 */
	public static int multiplyWithoutOverflow(int x, int y) {
		try {
			return Math.multiplyExact(x, y);
		} catch (ArithmeticException e) {
			return Math.signum(x) == Math.signum(y) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		}
	}
}
