package checkspec.util;

import java.lang.reflect.Modifier;

import checkspec.api.Visibility;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MemberUtils {

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
