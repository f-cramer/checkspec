package checkspec.util;

/*-
 * #%L
 * CheckSpec Commons
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static checkspec.util.SecurityUtils.*;

import java.net.URL;
import java.util.function.Function;
import java.util.stream.Stream;

import checkspec.api.Visibility;
import checkspec.type.MatchableType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods that are working on instances of {@link Class} and / or
 * {@link MatchableType}. Mainly for internal use within the framework itself.
 *
 * @author Florian Cramer
 * @see Class
 * @see MatchableType
 */
@UtilityClass
public final class ClassUtils {

	private static String TO_STRING_FORMAT = "%s %s %s";

	private static ClassLoader BASE_CLASS_LOADER;
	private static volatile ClassLoader SYSTEM_CLASS_LOADER;
	private static final Object SYSTEM_CLASS_LOAD_SYNC = new Object();

	private static final ClassLoader BOOT_CLASS_LOADER;

	static {
		ClassLoader loader = ClassUtils.getBaseClassLoader();
		while (loader.getParent() != null) {
			loader = loader.getParent();
		}

		BOOT_CLASS_LOADER = loader;
	}

	/**
	 * Returns a string representation of the given {@link MatchableType}. This
	 * looks exactly like the type header you would write to define the given
	 * type excluding any {@code extends} and / or {@code implements} statement.
	 * E.g. "public final class String" for an instance of {@link MatchableType}
	 * that was created from {@code java.lang.String}.
	 * <p>
	 * The modifiers of the given type are sorted using the canonical order
	 * found in {@link java.lang.reflect.Modifier#toString(int)}, the class name
	 * is given in the form that is given by {@link Class#getName()}.
	 *
	 * @param type
	 *            the non-null type
	 * @return the string representation
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}
	 */
	public static String createString(@NonNull MatchableType type) {
		String modifiers = ModifierUtils.createString(type);
		String classType = getType(type);
		String name = getName(type);
		return String.format(TO_STRING_FORMAT, modifiers, classType, name).replaceAll("\\s+", " ");
	}

	/**
	 * Returns a string representation of the given {@link Class}. This looks
	 * exactly like the type header you would write to define the given type
	 * excluding any {@code extends} and / or {@code implements} statement. E.g.
	 * "public final class String" for {@code java.lang.String}.
	 * <p>
	 * The modifiers of the given type are sorted using the canonical order
	 * found in {@link java.lang.reflect.Modifier#toString(int)}, the class name
	 * is given in the form that is given by {@link Class#getName()}.
	 *
	 * @param type
	 *            the non-null type
	 * @return the string representation
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}
	 */
	public static String toString(@NonNull Class<?> type) {
		return createString(MatchableType.forClass(type));
	}

	private static String getType(MatchableType type) {
		Class<?> clazz = type.getRawClass();
		if (clazz.isEnum()) {
			return "enum";
		} else if (clazz.isAnnotation()) {
			return "@interface";
		} else if (clazz.isInterface()) {
			return "interface";
		} else {
			return "class";
		}
	}

	/**
	 * Returns the fully-qualified name of the given type including all type
	 * parameters. Returns "&lt;component-type&gt;[]" for an array type.
	 *
	 * @param type
	 *            the non-null type
	 * @return the name
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}
	 */
	public static String getName(@NonNull MatchableType type) {
		return type.toString();
	}

	/**
	 * Returns the fully-qualified name of the given type including all type
	 * parameters. Returns "&lt;component-type&gt;[]" for an array type.
	 *
	 * @param clazz
	 *            the non-null type
	 * @return the name
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}
	 */
	public static String getName(@NonNull Class<?> clazz) {
		return getName(MatchableType.forClass(clazz));
	}

	/**
	 * Returns the runtime class descriptor for the class that is associated
	 * with the given fully-qualified name. If such class could not be found
	 * {@code null} is returned.
	 *
	 * @param className
	 *            the class name
	 * @return the class associated with the given class name if one could be
	 *         found, null otherwise
	 * @throws NullPointerException
	 *             if {@code className} is {@code null}
	 * @see #getClassAsStream(String)
	 * @see Class#forName(String)
	 */
	public static Class<?> getClass(@NonNull String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Returns a {@code Stream} containing the runtime class descriptor for the
	 * class that is associcated with the given fully-qualified name. Returns an
	 * empty stream if such class could not be found.
	 *
	 * @param className
	 *            the class name
	 * @return a {@code Stream} containing the class associated with the given
	 *         class name if one could be found, an empty {@code Stream}
	 *         otherwise.
	 * @throws NullPointerException
	 *             if {@code className} is {@code null}
	 * @see #getClass(String)
	 * @see Class#forName(String)
	 */
	public static Stream<Class<?>> getClassAsStream(@NonNull String className) {
		try {
			return Stream.of(Class.forName(className));
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			return Stream.empty();
		}
	}

	/**
	 * Determines and returns the package name of the given type. E.g.
	 * "java.lang" for a {@link MatchableType} that was created from an instance
	 * of {@code java.lang.String}.
	 *
	 * @param type
	 *            the type
	 * @return the package name, or and empty string if the class was defined in
	 *         the default package
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}
	 * @see #getPackage(Class)
	 * @see #getPackage(String)
	 */
	public static String getPackage(@NonNull MatchableType type) {
		return org.apache.commons.lang3.ClassUtils.getPackageName(type.getRawClass());
	}

	/**
	 * Determines and returns the package name of the given type. E.g.
	 * "java.lang" for {@code java.lang.String}.
	 *
	 * @param type
	 *            the type
	 * @return the package name, or and empty string if the class was defined in
	 *         the default package
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}
	 * @see #getPackage(Class)
	 * @see #getPackage(String)
	 */
	public static String getPackage(@NonNull Class<?> type) {
		return org.apache.commons.lang3.ClassUtils.getPackageName(type);
	}

	/**
	 * Determines and returns the package name of the given type. E.g.
	 * "java.lang" for "java.lang.String".
	 *
	 * @param className
	 *            the class name
	 * @return the package name, or and empty string if the class was defined in
	 *         the default package
	 * @throws NullPointerException
	 *             if {@code class name} is {@code null}
	 * @see #getPackage(Class)
	 * @see #getPackage(String)
	 */
	public static String getPackage(@NonNull String className) {
		return org.apache.commons.lang3.ClassUtils.getPackageName(className);
	}

	/**
	 * Creates a lambda function that loads a class by its name from the given
	 * {@link ClassLoader}. If no such class could be found, i.e. if
	 * {@link ClassLoader#loadClass(String) loadClass(String)} throws a
	 * {@link ClassNotFoundException}, {@code null} is returned.
	 *
	 * @param loader
	 *            the class loader
	 * @return a lambda function that loads a class from the given class loader
	 * @throws NullPointerException
	 *             if {@code loader} is {@code null}
	 * @see #getClass(String)
	 * @see ClassLoader#loadClass(String)
	 */
	public Function<String, Class<?>> classSupplier(@NonNull ClassLoader loader) {
		return name -> {
			try {
				return loader.loadClass(name);
			} catch (ClassNotFoundException e) {
				return null;
			}
		};
	}

	/**
	 * Creates a lambda function that loads a class by its name from the given
	 * {@link ClassLoader} and wraps it in a {@link Stream}. If no such class
	 * could be found, i.e. if {@link ClassLoader#loadClass(String)
	 * loadClass(String)} throws a {@link ClassNotFoundException}, an empty
	 * stream is returned.
	 *
	 * @param loader
	 *            the class loader
	 * @return a lambda function that loads a class from the given class loader
	 *         and wraps it in an instance of {@link Stream}
	 * @throws NullPointerException
	 *             if {@code loader} is {@code null}
	 * @see #getClassAsStream(String)
	 * @see ClassLoader#loadClass(String)
	 */
	public Function<String, Stream<Class<?>>> classStreamSupplier(@NonNull ClassLoader loader) {
		return name -> {
			try {
				return Stream.of(loader.loadClass(name));
			} catch (ClassNotFoundException | NoClassDefFoundError e) {
				return Stream.empty();
			}
		};
	}

	/**
	 * Creates a lambda function that loads a class by its name from the system
	 * class loader and wraps it in a {@link Stream}. If no such class could be
	 * found, i.e. if {@link ClassLoader#loadClass(String) loadClass(String)}
	 * throws a {@link ClassNotFoundException}, an empty stream is returned.
	 *
	 * @return a lambda function that loads a class from the given class loader
	 *         and wraps it in an instance of {@link Stream}
	 * @see #getClassAsStream(String)
	 * @see #getBaseClassLoader()
	 * @see ClassLoader#loadClass(String)
	 */
	public Function<String, Stream<Class<?>>> systemClassStreamSupplier() {
		return classStreamSupplier(getBaseClassLoader());
	}

	/**
	 * Returns a lambda function that creates and instance for a given class and
	 * wrappes it in a {@link Stream}. If the given class could not be
	 * instantiated, i.e. if throws an {@link InstantiationException}, an empty
	 * stream is returned.
	 *
	 * @param <T>
	 *            the class type
	 * @return a lambda function that instantiates a given class and wraps it in
	 *         an instance of {@link Stream}
	 * @see #instantiate(String)
	 */
	public static <T> Function<Class<? extends T>, Stream<? extends T>> instantiate() {
		return instantiate(null);
	}

	/**
	 * Returns a lambda function that creates and instance for a given class and
	 * wrappes it in a {@link Stream}. If the given class could not be
	 * instantiated, i.e. if throws an {@link InstantiationException}, the given
	 * error message is printed to {@link System#err} and an empty stream is
	 * returned.
	 *
	 * @param <T>
	 *            the class type
	 * @param errorFormat
	 *            the error format
	 * @return a lambda function that instantiates a given class and wraps it in
	 *         an instance of {@link Stream}
	 * @throws NullPointerException
	 *             if {@code errorFormat} is {@code null}
	 * @see #instantiate()
	 */
	public static <T> Function<Class<? extends T>, Stream<? extends T>> instantiate(String errorFormat) {
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

	/**
	 * Returns the {@link Visibility} of the given type, e.g.
	 * {@link Visibility#PUBLIC PUBLIC} for an instance
	 * of @{@link MatchableType} created from {@code checkspec.util.ClassUtils}.
	 *
	 * @param type
	 *            the type
	 * @return the visibility
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}
	 */
	public static Visibility getVisibility(@NonNull MatchableType type) {
		return MemberUtils.getVisibility(type.getRawClass().getModifiers());
	}

	/**
	 * Checks if a type {@code superType} is a super type of {@code type}. This
	 * is {@code true} any of the following checks are {@code true}
	 * <ul>
	 * <li>{@code type} and {@code superType} describe the same class or the
	 * same interface.
	 * <li>{@code type} and {@code superType} describe different classes and any
	 * of the following checks are {@code true}
	 * <ul>
	 * <li>it is the direct super class of {@code type}</li>
	 * <li>it is a super type of the direct super class {@code superClass} of
	 * {@code type}, such that {@code isSuperType(superClass, superType)} is
	 * {@code true}</li>
	 * </ul>
	 * </li>
	 * <li>{@code type} describes a class and {@code superType} an interface and
	 * any of the following checks are {@code true}
	 * <ul>
	 * <li>it is implemented by {@code type}</li>
	 * <li>it is a super type of the direct super class {@code superClass} of
	 * {@code type}, such that {@code isSuperType(superClass, superType)} is
	 * {@code true}</li>
	 * <li>it is a super type of any of the interfaces {@code interfs} that are
	 * directly implemented by {@code type}, such that
	 * {@code isSuperType(interf, superType)} is {@code true} for any interface
	 * {@code interf} in {@code interfs}
	 * </ul>
	 * </li>
	 * <li>{@code type} and {@code superType} describe different interfaces and
	 * any of the following checks are {@code true}
	 * <ul>
	 * <li>{@code type} directly extends {@code superType}</li>
	 * <li>any of the interfaces {@code interfs} that are directly extended by
	 * {@code type} is a super type of {@code superType}, such that
	 * {@code isSuperType(interf, superType)} is {@code true} for any interface
	 * {@code interf} in {@code interfs}</li>
	 * </ul>
	 * </li>
	 * </ul>
	 *
	 * @param type
	 *            the type
	 * @param superType
	 *            the possible super type
	 * @return whether or not {@code superType} is a super type of {@code type}
	 * @throws NullPointerException
	 *             if {@code type} or {@code superType} are {@code null}
	 */
	public static boolean isSuperType(@NonNull final Class<?> type, @NonNull final Class<?> superType) {
		return org.apache.commons.lang3.ClassUtils.isAssignable(type, superType);
	}

	/**
	 * Returns the base class loader for this framework. By default this is the
	 * system class loader that is loaded via
	 * {@link ClassLoader#getSystemClassLoader()}.
	 *
	 * The base class loader can be changed using the
	 * {@link #setBaseClassLoader(ClassLoader)} method.
	 *
	 * @return the base class loader
	 * @see #setBaseClassLoader(ClassLoader)
	 */
	public static ClassLoader getBaseClassLoader() {
		if (BASE_CLASS_LOADER == null) {
			return getSystemClassLoader();
		} else {
			return BASE_CLASS_LOADER;
		}
	}

	/**
	 * Sets the base class loader for this framework. If {@code classLoader} is
	 * {@code null} the base class loader is reset to the system class loader.
	 *
	 * @param classLoader
	 *            the base class loader
	 * @see #getBaseClassLoader()
	 */
	public static void setBaseClassLoader(ClassLoader classLoader) {
		BASE_CLASS_LOADER = classLoader;
	}

	/**
	 * Utility method for lazy initialization of the
	 * {@link #SYSTEM_CLASS_LOADER} constant.
	 *
	 * @return the system class loader
	 */
	private static ClassLoader getSystemClassLoader() {
		if (SYSTEM_CLASS_LOADER == null) {
			synchronized (SYSTEM_CLASS_LOAD_SYNC) {
				if (SYSTEM_CLASS_LOADER == null) {
					SYSTEM_CLASS_LOADER = doPrivileged(ClassLoader::getSystemClassLoader);
				}
			}
		}

		return SYSTEM_CLASS_LOADER;
	}

	/**
	 * Checks whether the given types refer to the same class or the same
	 * interface.
	 *
	 * @param t1
	 *            the first type
	 * @param t2
	 *            the second type
	 * @return whether or not the given types refer to the same class or the
	 *         same
	 * @throws NullPointerException
	 *             if {@code t1} or {@code t2} are {@code null} interface
	 */
	public static boolean equal(Class<?> t1, Class<?> t2) {
		if (t1 == null || t2 == null) {
			return t1 == t2;
		}

		// should be false if same class is loaded by two different class
		// loaders
		if (t1 == t2) {
			return true;
		}

		if (getName(t1).equals(getName(t2))) {
			return true;
		}

		return false;
	}

	/**
	 * Returns the location of the class file the given class was loaded from.
	 *
	 * @param clazz
	 *            the class
	 * @return the location of the class file
	 * @throws NullPointerException
	 *             if {@code clazz} is {@code null}
	 */
	public static URL getLocation(@NonNull Class<?> clazz) {
		String name = clazz.getName().replace('.', '/') + ".class";

		ClassLoader loader = clazz.getClassLoader();
		if (loader == null) {
			loader = clazz.getClassLoader();
		}

		if (loader != null) {
			URL resource = loader.getResource(name);
			if (resource != null) {
				return resource;
			}
		}

		return BOOT_CLASS_LOADER.getResource(name);
	}
}
