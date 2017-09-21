package checkspec.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import checkspec.api.Visibility;
import checkspec.type.MatchableType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods that are working on instances of {@link Field}. Mainly
 * for internal use within the framework itself.
 *
 * @author Florian Cramer
 * @see Field
 */
@UtilityClass
public final class FieldUtils {

	private static final String TO_STRING_FORMAT = "%s %s %s";

	/**
	 * Returns a string representation of the given {@link Field}. This looks
	 * exactly like the string you would write to define the given field. E.g.
	 * "public static final String TO_STRING_FORMAT" for the only field in this
	 * class.
	 * <p>
	 * The modifiers of the given type are sorted using the canonical order
	 * found in {@link java.lang.reflect.Modifier#toString(int)}.
	 *
	 * @param field
	 *            the non-null field
	 * @return the string representation
	 * @throws NullPointerException
	 *             if {@code field} is {@code null}
	 */
	public static String createString(@NonNull Field field) {
		String modifiers = Modifier.toString(field.getModifiers());
		String typeName = getTypeName(field);
		return String.format(TO_STRING_FORMAT, modifiers, typeName, field.getName()).trim();
	}

	/**
	 * Returns a {@link MatchableType} for the given field.
	 *
	 * @param field
	 *            the non-null field
	 * @return the matchable type
	 * @throws NullPointerException
	 *             if {@code field} is {@code null}
	 * @see MatchableType#forFieldType(Field)
	 */
	public static MatchableType getType(@NonNull Field field) {
		return MatchableType.forFieldType(field);
	}

	/**
	 * Returns a string representation for the type of the given field.
	 *
	 * @param field
	 *            the non-null field
	 * @return the string representation
	 * @throws NullPointerException
	 *             if {@code field} is {@code null}
	 * @see ClassUtils#getName(MatchableType)
	 */
	public static String getTypeName(@NonNull Field field) {
		return ClassUtils.getName(MatchableType.forFieldType(field));
	}

	/**
	 * Returns the visibility of the given field.
	 *
	 * @param field
	 *            the non-null field
	 * @return the visibility
	 * @see MemberUtils#getVisibility(int)
	 */
	public static Visibility getVisibility(@NonNull Field field) {
		return MemberUtils.getVisibility(field.getModifiers());
	}
}
