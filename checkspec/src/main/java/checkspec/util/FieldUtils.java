package checkspec.util;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;

public class FieldUtils {

	public static String createString(@Nonnull Field field) {
		return toString(field);
	}

	public static String toString(@Nonnull Field field) {
		Visibility visibility = getVisibility(field);
		String typeName = getTypeName(field);
		return String.format("%s %s %s", visibility, typeName, field.getName()).trim();
	}

	public static ResolvableType getType(@Nonnull Field field) {
		return ResolvableType.forField(field);
	}

	public static String getTypeName(@Nonnull Field field) {
		return ClassUtils.getName(ResolvableType.forField(field));
	}

	public static Visibility getVisibility(@Nonnull Field field) {
		return MemberUtils.getVisibility(field.getModifiers());
	}
}
