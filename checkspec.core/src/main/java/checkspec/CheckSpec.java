package checkspec;

/*-
 * #%L
 * CheckSpec Core
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

import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import checkspec.analysis.ClassAnalysis;
import checkspec.report.ClassReport;
import checkspec.report.SpecReport;
import checkspec.specification.ClassSpecification;
import checkspec.type.MatchableType;
import checkspec.util.ClassUtils;
import checkspec.util.ReflectionsUtils;
import checkspec.util.StreamUtils;
import checkspec.util.TypeDiscovery;
import checkspec.util.UrlUtils;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Main entry point into the framework.
 *
 * @author Florian Cramer
 *
 */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CheckSpec {

	private static final String JAR_SUFFIX = ".jar";

	private static volatile CheckSpec DEFAULT_INSTANCE;
	private static final Object DEFAULT_SYNC = new Object();

	private static volatile CheckSpec LIBRARY_LESS_INSTANCE;
	private static final Object LIBRARY_LESS_SYNC = new Object();

	/**
	 * Returns the lazyly created default instance of this class. It is created
	 * using {@link ReflectionsUtils#createDefaultReflections()
	 * createDefaultReflections()} and {@link ClassUtils#getBaseClassLoader()
	 * getBaseClassLoader()}.
	 *
	 * @return default {@code CheckSpec} instance
	 */
	public static CheckSpec getDefaultInstance() {
		if (DEFAULT_INSTANCE == null) {
			synchronized (DEFAULT_SYNC) {
				if (DEFAULT_INSTANCE == null) {
					Reflections reflections = ReflectionsUtils.createDefaultReflections();
					ClassLoader classLoader = ClassUtils.getBaseClassLoader();
					DEFAULT_INSTANCE = new CheckSpec(reflections, classLoader);
				}
			}
		}
		return DEFAULT_INSTANCE;
	}

	/**
	 * Returns an instance of this class that is created by using only the
	 * locally created classes excluding any dependencies in .jar-files. It is
	 * created using {@link ReflectionsUtils#createDefaultReflections()
	 * createDefaultReflections()}.
	 *
	 * @return {@code CheckSpec} instance excluding .jar-files
	 */
	public static CheckSpec getInstanceForClasspathWithoutJars() {
		if (LIBRARY_LESS_INSTANCE == null) {
			synchronized (LIBRARY_LESS_SYNC) {
				if (LIBRARY_LESS_INSTANCE == null) {
					URL[] urls = Arrays.stream(ReflectionsUtils.getUrlsFromClasspath()).parallel()
							.filter(url -> !url.getPath().endsWith(JAR_SUFFIX))
							.toArray(URL[]::new);

					Reflections reflections = ReflectionsUtils.createReflections(urls);
					ClassLoader classLoader = doPrivileged(() -> new URLClassLoader(urls, ClassUtils.getBaseClassLoader()));
					LIBRARY_LESS_INSTANCE = new CheckSpec(reflections, classLoader);
				}
			}
		}
		return LIBRARY_LESS_INSTANCE;
	}

	/**
	 * Returns an instance of {@link CheckSpec} that is created using the given
	 * {@link URL}s as classpath. This method uses
	 * {@link ReflectionsUtils#createReflections(URL[])
	 * createReflections(URL[])} to create an instance of {@link Reflections}.
	 *
	 * @param urls
	 *            the classpath, null not permitted
	 * @return an instance created using the given urls as classpath
	 */
	public static CheckSpec getInstanceForClasspath(@NonNull URL[] urls) {
		Reflections reflections = ReflectionsUtils.createReflections(urls);
		ClassLoader classLoader = createUrlClassLoader(urls);
		return new CheckSpec(reflections, classLoader);
	}

	/**
	 * Returns an instance of {@link CheckSpec} that is created using the given
	 * {@link URL}s as different classpaths. This method uses
	 * {@link ReflectionsUtils#createReflections(URL[])
	 * createReflections(URL[])} to create instances of {@link Reflections}.
	 *
	 * @param urlsList
	 *            the classpaths, null not permitted
	 * @return an insatnce created using the given urls as classpaths
	 */
	public static CheckSpec getInstanceForClasspaths(@NonNull List<URL[]> urlsList) {
		List<Pair<ClassLoader, Reflections>> list = urlsList.stream()
				.map(urls -> Pair.of(createUrlClassLoader(urls), ReflectionsUtils.createReflections(urls)))
				.collect(Collectors.toList());
		return new CheckSpec(list);
	}

	private static ClassLoader createUrlClassLoader(URL[] urls) {
		return doPrivileged(() -> new URLClassLoader(urls, ClassUtils.getBaseClassLoader()));
	}

	private static final String ERROR_FORMAT = "Analysis \"%s\" does not provide a default constructor and thus will not be used%n";
	private static final ClassAnalysis<?>[] ANALYSES;

	static {
		List<Class<?>> analyses = TypeDiscovery.getSubTypesOf(ClassAnalysis.class).stream()
				.filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
				.collect(Collectors.toList());

		List<Class<?>> mostConcreteAnalyses = new ArrayList<>();

		// filter for most concrete analyses only
		// any class that is subclassed is not used
		for (Class<?> analysis : analyses) {
			List<Class<?>> moreAbstractSuperClasses = mostConcreteAnalyses.parallelStream()
					.filter(potentialSuperClass -> ClassUtils.isSuperType(analysis, potentialSuperClass))
					.collect(Collectors.toList());

			boolean hasSubClassesAdded = mostConcreteAnalyses.parallelStream()
					.anyMatch(potentialSubClass -> ClassUtils.isSuperType(potentialSubClass, analysis));

			if (!hasSubClassesAdded) {
				mostConcreteAnalyses.add(analysis);
			}

			mostConcreteAnalyses.removeAll(moreAbstractSuperClasses);
		}

		ANALYSES = mostConcreteAnalyses.stream()
				.filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
				.map(clazz -> (Class<ClassAnalysis<?>>) clazz)
				.flatMap(ClassUtils.instantiate(ERROR_FORMAT))
				.sorted(Comparator.comparingInt(ClassAnalysis::getPriority))
				.toArray(length -> new ClassAnalysis<?>[length]);
	}

	private final List<Pair<ClassLoader, Reflections>> classLoaderReflectionsPairs;

	private CheckSpec(Reflections reflections, ClassLoader classLoader) {
		this(Collections.singletonList(Pair.of(classLoader, reflections)));
	}

	/**
	 * Creates a list of {@link SpecReport}s for the given specifications
	 * {@code specs} each of which is populated with a {@link ClassReport} for
	 * any class that in any way matches the current {@code spec}.
	 * <p>
	 * Behaves the same as a call to {@code checkSpec(specs, "")}.
	 *
	 * @param specs
	 *            the non-null list of specifications the returned list of
	 *            {@link SpecReport}s should be based on
	 * @return a list of {@link SpecReport} that is populated with a
	 *         {@link ClassReport} for any class that in any way matches
	 *         {@code spec}
	 */
	public List<SpecReport> checkSpec(@NonNull Collection<ClassSpecification> specs) {
		return checkSpec(specs, "");
	}

	/**
	 * Creates a list of {@link SpecReport}s for the given specifications
	 * {@code specs} each of which is populated with a {@link ClassReport} for
	 * any class in the same base package as {@code basePackage} that in any way
	 * matches the current {@code spec}.
	 *
	 * @param specs
	 *            the non-null list of specifications the returned list of
	 *            {@link SpecReport}s should be baseed on
	 * @param basePackage
	 *            the base package in which all implementations contained in the
	 *            returned {@link SpecReport}s should be inside of.
	 * @return a list of {@link SpecReport} that is populated with a
	 *         {@link ClassReport} for any class that in any way matches
	 *         {@code spec}
	 */
	public List<SpecReport> checkSpec(@NonNull Collection<ClassSpecification> specs, @NonNull Class<?> basePackage) {
		return checkSpec(specs, ClassUtils.getPackage(basePackage));
	}

	/**
	 * Creates a list of {@link SpecReport}s for the given specifications
	 * {@code specs} each of which is populated with a {@link ClassReport} for
	 * any class in the same base package as {@code basePackage} that in any way
	 * matches the current {@code spec}.
	 *
	 * @param specs
	 *            the non-null list of specifications the returned list of
	 *            {@link SpecReport}s should be baseed on
	 * @param basePackageName
	 *            the base package in which all implementations contained in the
	 *            returned {@link SpecReport}s should be inside of.
	 * @return a list of {@link SpecReport} that is populated with a
	 *         {@link ClassReport} for any class that in any way matches
	 *         {@code spec}
	 */
	public List<SpecReport> checkSpec(@NonNull Collection<ClassSpecification> specs, @NonNull String basePackageName) {
		List<Class<?>> possibleClasses = getPossibleClasses(specs, basePackageName);

		List<SpecReport> reports = Collections.emptyList();

		int maxIterations = 10;
		for (int iteration = 0; iteration <= maxIterations; iteration++) {
			List<SpecReport> oldReports = reports;
			MultiValuedMap<Class<?>, Class<?>> bestMatches = convert(oldReports);
			reports = performChecks(specs, possibleClasses, bestMatches);

			if (Objects.equals(oldReports, reports)) {
				break;
			}
		}

		return reports.parallelStream()
				.map(CheckSpec::filterImproperClassReports)
				.collect(Collectors.toList());
	}

	private static List<SpecReport> performChecks(Collection<ClassSpecification> specs, Collection<Class<?>> possibleClasses, MultiValuedMap<Class<?>, Class<?>> bestMatches) {
		return specs.stream()
				.map(spec -> performSingleCheck(spec, possibleClasses, bestMatches))
				.collect(Collectors.toList());
	}

	private static SpecReport performSingleCheck(ClassSpecification spec, Collection<Class<?>> possibleClasses, MultiValuedMap<Class<?>, Class<?>> bestMatches) {
		List<ClassReport> reports = possibleClasses.parallelStream()
				.map(e -> checkImplements(e, spec, bestMatches))
				.collect(Collectors.toList());
		return new SpecReport(spec, reports);
	}

	private static ClassReport checkImplements(Class<?> clazz, ClassSpecification spec, MultiValuedMap<Class<?>, Class<?>> oldMappings) {
		ClassReport report = new ClassReport(spec, clazz);
		MatchableType type = MatchableType.forClass(clazz);

		for (final ClassAnalysis<?> analysis : ANALYSES) {
			performAnalysis(analysis, type, spec, oldMappings, report);
		}

		return report;
	}

	private static <ReturnType> void performAnalysis(ClassAnalysis<ReturnType> analysis, MatchableType clazz, ClassSpecification spec, MultiValuedMap<Class<?>, Class<?>> reports,
			ClassReport report) {
		ReturnType returnValue = analysis.analyze(clazz, spec, reports);
		analysis.add(report, returnValue);
	}

	private static MultiValuedMap<Class<?>, Class<?>> convert(List<SpecReport> reports) {
		HashSetValuedHashMap<Class<?>, Class<?>> map = new HashSetValuedHashMap<>();
		reports.forEach(report -> map.putAll(report.getSpecification().getRawElement().getRawClass(), getImplementationClasses(report)));
		return map;
	}

	private static List<Class<?>> getImplementationClasses(SpecReport report) {
		return report.getClassReports().parallelStream()
				.map(ClassReport::getImplementation)
				.map(MatchableType::getRawClass)
				.collect(Collectors.toList());
	}

	private List<Class<?>> getPossibleClasses(Collection<ClassSpecification> specs, String basePackageName) {
		List<URL> specLocations = specs.parallelStream()
				.map(spec -> ClassUtils.getLocation(spec.getRawElement().getRawClass()))
				.collect(Collectors.toList());
		String basePackage = basePackageName.toLowerCase();
		return classLoaderReflectionsPairs.stream()
				.flatMap(pair -> getPossibleClasses(pair.getLeft(), pair.getRight(), specLocations, basePackage))
				.collect(Collectors.toList());
	}

	private static Stream<Class<?>> getPossibleClasses(ClassLoader classLoader, Reflections reflections, Collection<URL> specLocations, String basePackage) {
		Collection<String> classNames = reflections.getStore().get(SubTypesScanner.class.getSimpleName()).values();

		return classNames.parallelStream()
				.filter(e -> ClassUtils.getPackage(e).toLowerCase().startsWith(basePackage))
				.flatMap(ClassUtils.classStreamSupplier(classLoader))
				.filter(StreamUtils.inPredicate(specLocations, ClassUtils::getLocation).negate())
				.filter(clazz -> loadedFromValidLocation(reflections, clazz))
				.distinct();
	}

	private static boolean loadedFromValidLocation(Reflections reflections, Class<?> clazz) {
		Set<URL> urls = reflections.getConfiguration().getUrls();
		URL location = ClassUtils.getLocation(clazz);

		return urls.parallelStream().anyMatch(url -> UrlUtils.isParent(location, url));
	}

	private static SpecReport filterImproperClassReports(SpecReport report) {
		List<ClassReport> classReports = report.getClassReports().parallelStream()
				.filter(ClassReport::isAnyImplemenationMatching)
				.sorted()
				.collect(Collectors.toList());
		return new SpecReport(report.getSpecification(), classReports);
	}
}
