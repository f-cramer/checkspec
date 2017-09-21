package checkspec.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods for creating messages. Mainly for internal use within
 * the framework itself.
 *
 * @author Florian Cramer
 */
@UtilityClass
public final class MessageUtils {

	private static final String MISSING = "%s - missing";
	private static final String BEST_FITTING = "%s - best fitting for \"%s\"";

	/**
	 * Creates a <i>missing</i> message.
	 *
	 * @param expected
	 *            the expected value that is missing
	 * @return the message
	 */
	public static String missing(@NonNull String expected) {
		return String.format(MISSING, expected);
	}

	/**
	 * Creates a <i>best fitting</i> message.
	 *
	 * @param actual
	 *            the actual value
	 * @param expected
	 *            the value as it should be
	 * @return the message
	 */
	public static String bestFitting(@NonNull String actual, @NonNull String expected) {
		return String.format(BEST_FITTING, actual, expected);
	}
}
