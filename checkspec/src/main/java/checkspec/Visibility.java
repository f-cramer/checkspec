package checkspec;

import java.util.Locale;

enum Visibility {

	DEFAULT, PRIVATE, PROTECTED, PUBLIC;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.US);
	}
}
