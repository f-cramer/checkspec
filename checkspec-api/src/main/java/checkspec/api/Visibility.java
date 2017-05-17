package checkspec.api;

import java.util.Locale;

public enum Visibility {

	INSIGNIFICANT, PACKAGE {
		@Override
		public String toString() {
			return "";
		}
	},
	PRIVATE, PROTECTED, PUBLIC;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.US);
	}
}