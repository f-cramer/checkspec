package checkspec.util;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

public final class ReflectionsUtils {

	private static final String JAVA_CLASS_PATH = "java.class.path";
	private static final String PATH_SEPARATOR = "path.separator";

	private static volatile URL[] URLS;
	private static final Object URLS_SYNC = new Object();

	public static Reflections createDefaultReflections() {
		return createReflections(null);
	}

	public static Reflections createReflections(URL[] urls) {
		ConfigurationBuilder configuration = new ConfigurationBuilder()
				.forPackages("")
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
							.flatMap(ReflectionsUtils::getUrlAsStream)
							.toArray(URL[]::new);
				}
			}
		}
		return URLS;
	}
	
	public static Stream<URL> getUrlAsStream(String path) {
		try {
			return Stream.of(new File(path).toURI().toURL());
		} catch (Exception e) {
			return Stream.empty();
		}
	}
}
