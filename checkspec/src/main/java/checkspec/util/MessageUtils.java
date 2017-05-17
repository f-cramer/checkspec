package checkspec.util;

public class MessageUtils {

	private static final String MISSING = "%s - missing";
	private static final String BEST_FITTING = "%s - best fitting for \"%s\"";

	public static String missing(String expected) {
		return String.format(MISSING, expected);
	}

	public static String bestFitting(String actual, String expected) {
		return String.format(BEST_FITTING, actual, expected);
	}
}
