package checkspec.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MessageUtils {

	private static final String MISSING = "%s - missing";
	private static final String BEST_FITTING = "%s - best fitting for \"%s\"";

	public static String missing(@NonNull String expected) {
		return String.format(MISSING, expected);
	}

	public static String bestFitting(@NonNull String actual, @NonNull String expected) {
		return String.format(BEST_FITTING, actual, expected);
	}
}
