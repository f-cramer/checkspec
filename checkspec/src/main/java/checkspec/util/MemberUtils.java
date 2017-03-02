package checkspec.util;

import java.lang.reflect.Modifier;

public class MemberUtils {
	
	public static Visibility getVisibility(int modifiers) {
		if (Modifier.isPrivate(modifiers)) {
			return Visibility.PRIVATE;
		} else if (Modifier.isProtected(modifiers)) {
			return Visibility.PROTECTED;
		} else if (Modifier.isPublic(modifiers)) {
			return Visibility.PUBLIC;
		} else {
			return Visibility.DEFAULT;
		}
	}
}
