package checkspec;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Function;

public class StaticChecker {

	public static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static boolean checkMethods(Class<?> clazz, Class<?> interf) {
		return Arrays.stream(interf.getDeclaredMethods()).allMatch(method -> checkMethod(clazz, method));
	}

	public static boolean checkMethod(Class<?> clazz, Method method) {
		return true;
	}

	public static boolean checkModifiers(Class<?> clazz, Class<?> interf) {
		int classMods = clazz.getModifiers();
		int interfaceMods = interf.getModifiers();

		if (isNotEqual(classMods, interfaceMods, Modifier::isPrivate)) {
			System.out.printf("%s and %s are not equal in terms of private%n", clazz.getName(), interf.getName());
			return false;
		}

		if (isNotEqual(classMods, interfaceMods, Modifier::isProtected)) {
			System.out.printf("%s and %s are not equal in terms of protected%n", clazz.getName(), interf.getName());
			return false;
		}

		if (isNotEqual(classMods, interfaceMods, Modifier::isPublic)) {
			System.out.printf("%s and %s are not equal in terms of public%n", clazz.getName(), interf.getName());
			return false;
		}

		if (isNotEqual(classMods, interfaceMods, Modifier::isStatic)) {
			System.out.printf("%s and %s are not equal in terms of static%n", clazz.getName(), interf.getName());
			return false;
		}

		return true;
	}

	private static boolean isNotEqual(int classMods, int interfMods, Function<Integer, Boolean> function) {
		return function.apply(classMods) != function.apply(interfMods);
	}
}
