package checkspec.util;

import java.util.Locale;

enum ClassType {

	ANNOTATION, INTERFACE, ENUM, CLASS;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.US);
	}
}
