package checkspec;

public class ClassUtils {

	public static String getName(Class<?> clazz) {
		String pkg = org.apache.commons.lang3.ClassUtils.getPackageName(clazz);
		if (!pkg.isEmpty()) {
			pkg = pkg + ".";
		}

		return pkg + org.apache.commons.lang3.ClassUtils.getShortCanonicalName(clazz);
	}
}
