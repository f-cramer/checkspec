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



import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Stream;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import checkspec.api.Spec;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods that work on {@link Reflections} and classpath. Mainly
 * for internal use within the framework itself.
 *
 * @author Florian Cramer
 *
 */
@UtilityClass
public final class ReflectionsUtils {

	private static final String JAVA_CLASS_PATH = "java.class.path";
	private static final String PATH_SEPARATOR = "path.separator";
	private static final String VALUE = "value";

	private static volatile URL[] URLS;
	private static final Object URLS_SYNC = new Object();

	/**
	 * Creates a new default instance of {@link Reflections}.
	 *
	 * @return default instance of {@link Reflections}
	 */
	public static Reflections createDefaultReflections() {
		return createReflections(null);
	}

	/**
	 * Creates an instance of {@link Reflections} using all urls from the
	 * classpath.
	 *
	 * @return instance of {@link Reflections}
	 */
	public static Reflections createReflectionsFromClasspath() {
		return createReflections(getUrlsFromClasspath());
	}

	/**
	 * Creates an instance of {@link Reflections} using the given {@code urls}.
	 *
	 * @param urls
	 *            the urls
	 * @return instance of {@link Reflections}
	 */
	public static Reflections createReflections(URL[] urls) {
		ConfigurationBuilder configuration = new ConfigurationBuilder()
				.forPackages("")
				.setExpandSuperTypes(true)
				.setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner());

		if (urls != null) {
			configuration.setUrls(urls);
		}

		int availableProcessors = Runtime.getRuntime().availableProcessors();
		ExecutorService threadPool = Executors.newFixedThreadPool(availableProcessors, new DaemonThreadFactory());
		configuration.setExecutorService(threadPool);

		return new Reflections(configuration);
	}

	/**
	 * Returns all urls found in the current classpath.
	 *
	 * @return all urls found in current classpath
	 */
	public static URL[] getUrlsFromClasspath() {
		if (URLS == null) {
			synchronized (URLS_SYNC) {
				if (URLS == null) {
					URLS = Arrays.stream(System.getProperty(JAVA_CLASS_PATH).split(System.getProperty(PATH_SEPARATOR)))
							.flatMap(ReflectionsUtils::getAsUrlStream)
							.toArray(URL[]::new);
				}
			}
		}
		return Arrays.copyOf(URLS, URLS.length);
	}

	/**
	 * Creates a url from the given file path and maps it into a stream. If an
	 * exception occurres or the file does not exist an empty stream is
	 * returned.
	 *
	 * @param path
	 *            the file path
	 * @return a stream containing a url representing the given path or an empty
	 *         stream if no file could be found on the given path.
	 */
	public static Stream<URL> getAsUrlStream(String path) {
		File file = new File(path);
		if (file.exists()) {
			file = file.getAbsoluteFile();
			try {
				return Stream.of(file.getCanonicalFile().toURI().toURL());
			} catch (Exception expected) {
			}
		}

		return Stream.empty();
	}

	/**
	 * Returns an array of classes that are annotated with a non-disabled
	 * {@link Spec @Spec}.
	 *
	 * @param urls
	 *            the classpath urls to search
	 * @param classLoader
	 *            the classloader for this urls
	 * @return classes in {@code urls} annotated with {@link Spec @Spec}
	 */
	public static Class<?>[] findClassAnnotatedWithEnabledSpec(URL[] urls, ClassLoader classLoader) {
		// checkspec maven plugin does not seem to like this being inlined
		return createReflections(urls).getAllTypes().parallelStream()
				.flatMap(ClassUtils.classStreamSupplier(classLoader))
				.filter(ReflectionsUtils::hasSpecAnnotation)
				.toArray(i -> new Class<?>[i]); // checkstyle does not like this
												// being a method reference
	}

	private static boolean hasSpecAnnotation(Class<?> clazz) {
		Function<Annotation, String> annotationName = ((Function<Annotation, Class<?>>) Annotation::annotationType).andThen(Class::getName);

		return Arrays.stream(clazz.getAnnotations()).parallel()
				.filter(StreamUtils.equalsPredicate(Spec.class.getName(), annotationName))
				.anyMatch(ReflectionsUtils::hasValueSetToTrue);
	}

	private static boolean hasValueSetToTrue(Annotation annotation) {
		Method valueMethod;
		try {
			valueMethod = annotation.annotationType().getMethod(VALUE);
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}

		if (valueMethod != null && valueMethod.getReturnType() == boolean.class) {
			boolean accessible = valueMethod.isAccessible();
			valueMethod.setAccessible(true);
			try {
				return (Boolean) valueMethod.invoke(annotation);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException expected) {
			}
			valueMethod.setAccessible(accessible);
		}

		return false;
	}
}
