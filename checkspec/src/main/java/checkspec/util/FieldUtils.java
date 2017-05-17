package checkspec.util;

import java.lang.reflect.Field;
import java.util.Objects;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;

public class FieldUtils {

	public static String createString(Field field) {
		return toString(field);
	}

	public static String toString(Field field) {
		Visibility visibility = getVisibility(field);
		return String.format("%s %s %s", visibility, field.getName()).trim();
	}
	
	public static ResolvableType getType(Field field) {
		return ResolvableType.forField(field);
	}

	public static String getTypeName(Field field) {
		return ClassUtils.getName(ResolvableType.forField(field));
	}

	public static Visibility getVisibility(Field field) {
		Objects.requireNonNull(field);
		return MemberUtils.getVisibility(field.getModifiers());
	}
}
