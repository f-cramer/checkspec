package checkspec.util;

import java.lang.reflect.Field;
import java.util.Objects;

import checkspec.type.Visibility;

public class FieldUtils {

	public static String createString(Field field) {
		return toString(field);
	}

	public static String toString(Field field) {
		Visibility visibility = getVisibility(field);
		return String.format("%s %s", visibility, field.getName());
	}

	public static Visibility getVisibility(Field field) {
		Objects.requireNonNull(field);
		return MemberUtils.getVisibility(field.getModifiers());
	}
}
