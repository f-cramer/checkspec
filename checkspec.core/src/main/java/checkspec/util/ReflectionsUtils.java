package checkspec.util;

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

@UtilityClass

public final class ReflectionsUtils {

	private static final String JAVA_CLASS_PATH = "java.class.path";
	private static final String PATH_SEPARATOR = "path.separator";
	private static final String VALUE = "value";

	private static volatile URL[] URLS;
	private static final Object URLS_SYNC = new Object();

	public static Reflections createDefaultReflections() {
		return createReflections(null);
	}

	public static Reflections createReflectionsFromClasspath() {
		return createReflections(getUrlsFromClasspath());
	}

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

	public static Class<?>[] findClassAnnotatedWithEnabledSpec(URL[] urls, ClassLoader classLoader) {
		// checkspec maven plugin does not seem to like this being inlined
		return createReflections(urls).getAllTypes().parallelStream()
				.flatMap(ClassUtils.classStreamSupplier(classLoader))
				.filter(ReflectionsUtils::hasSpecAnnotation)
				.toArray(i -> new Class<?>[i]); // checkstyle does not like this being a method reference
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
