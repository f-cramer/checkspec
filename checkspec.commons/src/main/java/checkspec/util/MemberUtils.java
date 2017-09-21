package checkspec.util;

import java.lang.reflect.Modifier;

import checkspec.api.Visibility;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods that are working on instances of
 * {@link java.lang.reflect.Member Member}. Mainly for internal use within the
 * framework itself.
 *
 * @author Florian Cramer
 * @see java.lang.reflect.Member Member
 */
@UtilityClass
public final class MemberUtils {

	/**
	 * Returns the visibility for the given modifiers using the methods in
	 * {@link Modifier}.
	 *
	 * @param modifiers
	 *            the modifiers
	 * @return the visibility
	 */
	public static Visibility getVisibility(int modifiers) {
		if (Modifier.isPrivate(modifiers)) {
			return Visibility.PRIVATE;
		} else if (Modifier.isPublic(modifiers)) {
			return Visibility.PUBLIC;
		} else if (Modifier.isProtected(modifiers)) {
			return Visibility.PROTECTED;
		} else {
			return Visibility.PACKAGE;
		}
	}
}
