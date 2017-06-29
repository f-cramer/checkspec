package checkspec.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.function.Function;
import java.util.stream.Stream;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassUtils {

	private static String TO_STRING_FORMAT = "%s %s %s";

	private static volatile ClassLoader SYSTEM_CLASS_LOADER;
	private static final Object SYSTEM_CLASS_LOAD_SYNC = new Object();

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
			} catch (ClassNotFoundException | NoClassDefFoundError e) {
				return Stream.empty();
			}
		};
	}

	public Function<String, Stream<Class<?>>> systemClassStreamSupplier() {
		return classStreamSupplier(getSystemClassLoader());
	}

	public static <T> Function<Class<T>, Stream<T>> instantiate() {
		return instantiate(null);
	}

	public static <T> Function<Class<T>, Stream<T>> instantiate(String errorFormat) {
		return clazz -> {
			try {
				return Stream.of(clazz.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				if (errorFormat != null) {
					System.err.printf(errorFormat, getName(clazz));
				}
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

	public static boolean isSuperType(@NonNull final Class<?> type, @NonNull final Class<?> superType) {
		return org.apache.commons.lang3.ClassUtils.isAssignable(type, superType);
	}

	public static ClassLoader getSystemClassLoader() {
		if (SYSTEM_CLASS_LOADER == null) {
			synchronized (SYSTEM_CLASS_LOAD_SYNC) {
				if (SYSTEM_CLASS_LOADER == null) {
					PrivilegedAction<ClassLoader> action = () -> ClassLoader.getSystemClassLoader();
					SYSTEM_CLASS_LOADER = AccessController.doPrivileged(action);
				}
			}
		}

		return SYSTEM_CLASS_LOADER;
	}

	public static boolean equal(ResolvableType t1, ResolvableType t2) {
		if (t1.equals(t2)) {
			return true;
		}

		return equal(t1.getRawClass(), t2.getRawClass());
	}

	public static boolean equal(Class<?> t1, Class<?> t2) {
		if (t1 == t2) {
			return true;
		}

		if (getName(t1).equals(getName(t2))) {
			return true;
		}

		return false;
	}
}
