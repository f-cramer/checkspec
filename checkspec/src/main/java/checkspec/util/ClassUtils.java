package checkspec.util;

import java.util.stream.Stream;

import checkspec.spring.ResolvableType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassUtils {
	
	public static String toString(Class<?> clazz) {
		Visibility visibility = getVisibility(clazz);
		ClassType type = getType(clazz);
		String name = getName(clazz);
		return String.format("%s %s %s", visibility, type, name);
	}
	
	public static String getName(ResolvableType type) {
		return getName(type.getRawClass());
	}

	public static String getName(Class<?> clazz) {
		String pkg = org.apache.commons.lang3.ClassUtils.getPackageName(clazz);
		if (!pkg.isEmpty()) {
			pkg = pkg + ".";
		}

		return pkg + org.apache.commons.lang3.ClassUtils.getShortCanonicalName(clazz);
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

	public static String getPackage(Class<?> clazz) {
		return org.apache.commons.lang3.ClassUtils.getPackageName(clazz);
	}

	public static String getPackage(String className) {
		return org.apache.commons.lang3.ClassUtils.getPackageName(className);
	}

	public static ClassType getType(Class<?> clazz) {
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

	public static Visibility getVisibility(Class<?> clazz) {
		return MemberUtils.getVisibility(clazz.getModifiers());
	}
	

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
