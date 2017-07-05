package checkspec.util;

import java.lang.reflect.Modifier;

import checkspec.spring.ResolvableType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;


@UtilityClass
public class ModifierUtils {

	public static String toString(@NonNull ResolvableType type) {
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
