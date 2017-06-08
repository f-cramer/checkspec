package checkspec.util;

import java.lang.reflect.Field;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;
import lombok.NonNull;

public class FieldUtils {

	public static String createString(@NonNull Field field) {
		return toString(field);
	}

	public static String toString(@NonNull Field field) {
		Visibility visibility = getVisibility(field);
		String typeName = getTypeName(field);
		return String.format("%s %s %s", visibility, typeName, field.getName()).trim();
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
