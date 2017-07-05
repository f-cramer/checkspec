package checkspec.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class FieldUtils {

	private static final String TO_STRING_FORMAT = "%s %s %s";

	public static String toString(@NonNull Field field) {
		String modifiers = Modifier.toString(field.getModifiers());
		String typeName = getTypeName(field);
		return String.format(TO_STRING_FORMAT, modifiers, typeName, field.getName()).trim();
	}

	public static ResolvableType getType(@NonNull Field field) {
		return ResolvableType.forField(field);
	}

	public static String getTypeName(@NonNull Field field) {
		return ClassUtils.getName(ResolvableType.forField(field));
	}

	public static Visibility getVisibility(@NonNull Field field) {
		return MemberUtils.getVisibility(field.getModifiers());
	}
}
