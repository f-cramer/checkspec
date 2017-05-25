package checkspec.util;

import java.util.function.Function;
import java.util.stream.Stream;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassUtils {

	private static String TO_STRING_FORMAT = "%s %s %s";

	public static String toString(ResolvableType type) {
		Visibility visibility = getVisibility(type);
		ClassType classType = getType(type);
		String name = getName(type);
		return String.format(TO_STRING_FORMAT, visibility, classType, name);
	}

	 public static String toString(Class<?> clazz) {
		 return toString(ResolvableType.forClass(clazz));
	 }

	public static String getName(ResolvableType type) {
		if (type.isArray()) {
			return getName(type.getComponentType()) + "[]";
		}
		return type.toString();
	}

	public static String getName(Class<?> clazz) {
		return getName(ResolvableType.forClass(clazz));
	}

	public static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static Stream<Class<?>> getClassAsStream(String className) {
		try {
			return Stream.of(Class.forName(className));
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			return Stream.empty();
		}
	}
	
	public Function<String, Class<?>> classSupplier(ClassLoader loader) {
		return name -> {
			try {
				return loader.loadClass(name);
			} catch (ClassNotFoundException e) {
				return null;
			}
		};
	}

	public Function<String, Stream<Class<?>>> classStreamSupplier(ClassLoader loader) {
		return name -> {
			try {
				return Stream.of(loader.loadClass(name));
			} catch (ClassNotFoundException e) {
				return Stream.empty();
			}
		};
	}
	
	public static String getPackage(ResolvableType type) {
		return org.apache.commons.lang3.ClassUtils.getPackageName(type.getRawClass());
	}

	public static String getPackage(Class<?> clazz) {
		return org.apache.commons.lang3.ClassUtils.getPackageName(clazz);
	}

	public static String getPackage(String className) {
		return org.apache.commons.lang3.ClassUtils.getPackageName(className);
	}

	public static ClassType getType(ResolvableType type) {
		Class<?> clazz = type.getRawClass();
		if (clazz.isEnum()) {
			return ClassType.ENUM;
		} else if (clazz.isAnnotation()) {
			return ClassType.ANNOTATION;
		} else if (clazz.isInterface()) {
			return ClassType.INTERFACE;
		} else {
			return ClassType.CLASS;
		}
	}

	// public static ClassType getType(Class<?> clazz) {
	// if (clazz.isEnum()) {
	// return ClassType.ENUM;
	// } else if (clazz.isAnnotation()) {
	// return ClassType.ANNOTATION;
	// } else if (clazz.isInterface()) {
	// return ClassType.INTERFACE;
	// } else {
	// return ClassType.CLASS;
	// }
	// }

	public static Visibility getVisibility(ResolvableType type) {
		return MemberUtils.getVisibility(type.getRawClass().getModifiers());
	}

	// public static Visibility getVisibility(Class<?> clazz) {
	// return MemberUtils.getVisibility(clazz.getModifiers());
	// }

	public static boolean isAssignable(ResolvableType cls, final ResolvableType toClass) {
		if (toClass == null) {
			return false;
		}
		// have to check for null, as isAssignableFrom doesn't
		if (cls == null) {
			return !toClass.getRawClass().isPrimitive();
		}

		if (toClass.isAssignableFrom(cls)) {
			return true;
		}

		return org.apache.commons.lang3.ClassUtils.isAssignable(cls.getRawClass(), toClass.getRawClass());
	}
}
