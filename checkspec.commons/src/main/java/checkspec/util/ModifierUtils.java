package checkspec.util;

import java.lang.reflect.Modifier;

import checkspec.type.MatchableType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods for working with modifiers. Mainly for internal use
 * within the framework itself.
 *
 * @author Florian Cramer
 * @see Class#getModifiers()
 * @see java.lang.reflect.Member#getModifiers() Member.getModifier()
 */
@UtilityClass
public class ModifierUtils {

	/**
	 * Creates a string representation of the modifiers of the given type. The
	 * modifiers are sorted using the canonical order found in
	 * {@link java.lang.reflect.Modifier#toString(int)}. Visibility modifiers
	 * are excluded.
	 *
	 * @param type
	 *            the type
	 * @return the string representation of the types modifiers
	 */
	public static String createString(@NonNull MatchableType type) {
		int mod = type.getRawClass().getModifiers();
		boolean isEnum = type.getRawClass().isEnum();
		boolean isInterface = Modifier.isInterface(mod);

		StringBuilder sb = new StringBuilder();

		if (Modifier.isPublic(mod)) {
			sb.append("public ");
		}
		if (Modifier.isProtected(mod)) {
			sb.append("protected ");
		}
		if (Modifier.isPrivate(mod)) {
			sb.append("private ");
		}

		/* Canonical order */
		if (Modifier.isAbstract(mod)) {
			if (!isInterface) {
				if (!isEnum) {
					sb.append("abstract ");
				}
			}
		}
		if (Modifier.isStatic(mod)) {
			if (!isInterface) {
				if (!isEnum) {
					sb.append("static ");
				}
			}
		}
		if (Modifier.isFinal(mod)) {
			if (!isEnum) {
				sb.append("final ");
			}
		}

		return sb.toString().trim();
	}
}
