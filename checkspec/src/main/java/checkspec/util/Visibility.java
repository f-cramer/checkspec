package checkspec.util;

import java.util.Locale;

public enum Visibility {

	DEFAULT, PRIVATE, PROTECTED, PUBLIC;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.US);
	}
}
