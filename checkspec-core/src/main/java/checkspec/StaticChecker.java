package checkspec;

import static checkspec.util.ClassUtils.getName;
import static checkspec.util.MemberUtils.getVisibility;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.NATIVE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.element.Modifier.STRICTFP;
import static javax.lang.model.element.Modifier.SYNCHRONIZED;
import static javax.lang.model.element.Modifier.TRANSIENT;
import static javax.lang.model.element.Modifier.VOLATILE;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.SimilarityScore;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.reflections.Reflections;

import checkspec.analysis.AnalysisForClass;
import checkspec.api.Visibility;
import checkspec.report.ClassReport;
import checkspec.report.ConstructorReport;
import checkspec.report.FieldReport;
import checkspec.report.MethodReport;
import checkspec.report.Report;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.report.SpecReport;
import checkspec.spec.ClassSpecification;
import checkspec.spec.ConstructorSpecification;
import checkspec.spec.FieldSpecification;
import checkspec.spec.InterfaceSpecification;
import checkspec.spec.MethodParameterSpecification;
import checkspec.spec.MethodSpecification;
import checkspec.spec.ModifiersSpecification;
import checkspec.spec.ModifiersSpecification.State;
import checkspec.spec.Specification;
import checkspec.spec.VisibilitySpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import checkspec.util.ConstructorUtils;
import checkspec.util.FieldUtils;
import checkspec.util.MathUtils;
import checkspec.util.MethodUtils;
import checkspec.util.ReflectionsUtils;
import javassist.util.proxy.ProxyFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class StaticChecker {

	private static final Objenesis OBJENESIS = new ObjenesisStd();
	private static final SimilarityScore<Integer> NAME_SIMILARITY = LevenshteinDistance.getDefaultInstance();

	private static final String ERROR_FORMAT = "Analysis \"%s\" does not provide a default constructor and thus will not be used%n";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ClassReport checkImplements(Class<?> clazz, ClassSpecification spec) {
		ClassReport report = new ClassReport(spec, clazz);
		ResolvableType type = ResolvableType.forClass(clazz);

		Reflections reflections = ReflectionsUtils.createDefaultReflections();

		Set<Class<? extends AnalysisForClass>> analysisClasses = reflections.getSubTypesOf(AnalysisForClass.class);
		for (Class<? extends AnalysisForClass> analysisClass : analysisClasses) {
			try {
				if (!Modifier.isAbstract(analysisClass.getModifiers())) {
					AnalysisForClass analysis = analysisClass.newInstance();
					performAnalysis(analysis, type, spec, report);
				}
			} catch (InstantiationException | IllegalAccessException e) {
				System.err.printf(ERROR_FORMAT, ClassUtils.getName(analysisClass));
			}
		}
		
//		checkVisibility(clazz, spec).ifPresent(report::addProblem);
//		report.addProblems(checkModifiers(clazz, spec));
//		checkSuperClass(clazz, spec).ifPresent(report::addProblem);
//		report.addProblems(checkInterfaces(clazz, spec));
//		report.addFieldReports(checkFields(clazz, spec));
//		report.addConstructorReports(checkConstructors(clazz, spec));
//		report.addMethodReports(checkMethods(clazz, spec));

		return report;
	}
	
	private static <ReturnType> void performAnalysis(AnalysisForClass<ReturnType> analysis, ResolvableType clazz, ClassSpecification spec, ClassReport report) {
		ReturnType returnValue = analysis.analyse(clazz, spec);
		analysis.add(report, returnValue);
	}

	public static List<FieldReport> checkFields(Class<?> clazz, ClassSpecification spec) {
		// FieldSpecification[] fieldSpecifications =
		// spec.getFieldSpecifications();
		// List<Pair<Field, FieldSpecification>> pairs =
		// Arrays.stream(fieldSpecifications).parallel()
		// .flatMap(mapFields(clazz))
		// .sorted(Comparator.comparingInt(pair ->
		// calculateDistance(pair.getLeft(), pair.getRight())))
		// .collect(Collectors.toList());
		//
		// List<Field> unusedFields = new
		// ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
		// List<FieldSpecification> notFoundSpecs = new
		// ArrayList<>(Arrays.asList(fieldSpecifications));
		//
		// Iterator<Pair<Field, FieldSpecification>> iterator =
		// pairs.iterator();
		// while (iterator.hasNext()) {
		// Pair<Field, FieldSpecification> pair = iterator.next();
		// Field field = pair.getLeft();
		// FieldSpecification fieldSpec = pair.getRight();
		//
		// if (unusedFields.contains(field) &&
		// notFoundSpecs.contains(fieldSpec)) {
		// unusedFields.remove(field);
		// notFoundSpecs.remove(fieldSpec);
		// } else {
		// iterator.remove();
		// }
		// }
		//
		// Stream<FieldReport> foundFields = pairs.parallelStream()
		// .map(pair -> checkField(pair.getLeft(), pair.getRight()));
		// Stream<FieldReport> notFoundFields = notFoundSpecs.parallelStream()
		// .map(FieldReport::new);
		//
		// return Stream.concat(foundFields, notFoundFields)
		// .sorted(Comparator.comparing(FieldReport::getSpec))
		// .collect(Collectors.toList());

		return check(clazz, spec, ClassSpecification::getFieldSpecifications, StaticChecker::mapFields, StaticChecker::calculateDistance, Class::getDeclaredFields, StaticChecker::checkField,
				FieldReport::new, Comparator.comparing(FieldReport::getSpec));
	}

	private static Function<FieldSpecification, Stream<Pair<Field, FieldSpecification>>> mapFields(Class<?> clazz) {
		return fieldSpec -> Arrays.stream(clazz.getDeclaredFields()).parallel()
				.map(field -> Pair.of(field, fieldSpec));
	}

	private static FieldReport checkField(Field field, FieldSpecification spec) {
		List<ReportProblem> problems = new ArrayList<>();

		String fieldName = field.getName();
		String specName = spec.getName();

		checkVisibility(field, spec).ifPresent(problems::add);

		if (!fieldName.equals(specName)) {
			String format = "should have name \"%s\"";
			problems.add(new ReportProblem(1, String.format(format, specName), Type.WARNING));
		}

		ResolvableType fieldType = FieldUtils.getType(field);
		ResolvableType specType = spec.getType();

		if (fieldType.getRawClass() != specType.getRawClass()) {
			String fieldTypeName = getName(fieldType);
			String specTypeName = getName(specType);

			boolean compatible = ClassUtils.isAssignable(fieldType, specType);
			String format = "has " + (compatible ? "" : "in") + "compatible type \"%s\" rather than \"%s\"";
			String message = String.format(format, fieldTypeName, specTypeName);
			problems.add(new ReportProblem(1, message, compatible ? Type.WARNING : Type.ERROR));
		}

		FieldReport report = new FieldReport(spec, field);
		report.addProblems(problems);
		return report;
	}

	public static List<ConstructorReport> checkConstructors(Class<?> clazz, ClassSpecification spec) {
		// ConstructorSpecification[] constructorSpecifications =
		// spec.getConstructorSpecifications();
		// List<Pair<Constructor<?>, ConstructorSpecification>> pairs =
		// Arrays.stream(constructorSpecifications).parallel()
		// .flatMap(mapConstructors(clazz))
		// .sorted(Comparator.comparingInt(pair ->
		// calculateDistance(pair.getLeft(), pair.getRight())))
		// .collect(Collectors.toList());
		//
		// List<Constructor<?>> unusedConstructors = new
		// ArrayList<>(Arrays.asList(clazz.getDeclaredConstructors()));
		// List<ConstructorSpecification> notFoundSpecs = new
		// ArrayList<>(Arrays.asList(constructorSpecifications));
		//
		// Iterator<Pair<Constructor<?>, ConstructorSpecification>> iterator =
		// pairs.iterator();
		// while (iterator.hasNext()) {
		// Pair<Constructor<?>, ConstructorSpecification> pair =
		// iterator.next();
		// Constructor<?> constructor = pair.getLeft();
		// ConstructorSpecification constructorSpec = pair.getRight();
		// if (unusedConstructors.contains(constructor) &&
		// notFoundSpecs.contains(constructorSpec)) {
		// unusedConstructors.remove(constructor);
		// notFoundSpecs.remove(constructorSpec);
		// } else {
		// iterator.remove();
		// }
		// }
		//
		// Stream<ConstructorReport> foundConstructors = pairs.parallelStream()
		// .map(pair -> checkConstructor(pair.getLeft(), pair.getRight()));
		// Stream<ConstructorReport> notFoundConstructors =
		// notFoundSpecs.parallelStream()
		// .map(ConstructorReport::new);
		//
		// return Stream.concat(foundConstructors, notFoundConstructors)
		// .sorted(Comparator.comparing(ConstructorReport::getSpec))
		// .collect(Collectors.toList());

		return check(clazz, spec, ClassSpecification::getConstructorSpecifications, StaticChecker::mapConstructors, StaticChecker::calculateDistance, Class::getDeclaredConstructors,
				StaticChecker::checkConstructor, ConstructorReport::new, Comparator.comparing(ConstructorReport::getSpec));
	}

	private static Function<ConstructorSpecification, Stream<Pair<Constructor<?>, ConstructorSpecification>>> mapConstructors(Class<?> clazz) {
		return constructorSpec -> Arrays.stream(clazz.getDeclaredConstructors()).parallel()
				.map(constructor -> Pair.of(constructor, constructorSpec));
	}

	private static ConstructorReport checkConstructor(Constructor<?> constructor, ConstructorSpecification spec) {
		List<ReportProblem> problems = new ArrayList<>();

		checkVisibility(constructor, spec).ifPresent(problems::add);
		problems.addAll(checkModifiers(constructor, spec));
		problems.addAll(checkConstructorParameters(constructor, spec));

		ConstructorReport report = new ConstructorReport(spec, constructor);
		return report;
	}

	private static List<ReportProblem> checkConstructorParameters(Constructor<?> actual, ConstructorSpecification spec) {
		List<ReportProblem> problems = new ArrayList<>();

		int actualLength = actual.getParameterCount();
		int specLength = spec.getParameters().length;

		if (actualLength == specLength) {
			for (int i = 0; i < actualLength; i++) {
				ResolvableType specType = ResolvableType.forConstructorParameter(spec.getRawElement(), i);
				ResolvableType actualType = ResolvableType.forConstructorParameter(actual, i);

				if (actualType.getRawClass() != specType.getRawClass()) {
					boolean compatible = ClassUtils.isAssignable(specType, actualType);
					String format = "parameter %d has " + (compatible ? "" : "in") + "compatible type \"%s\"";
					Type type = compatible ? Type.WARNING : Type.ERROR;
					problems.add(new ReportProblem(1, String.format(format, i + 1, getName(actualType)), type));
				}
			}
		} else {
			int score = Math.abs(actualLength - specLength);
			String message = String.format("parameter count should be %s but is %s", specLength, actualLength);
			problems.add(new ReportProblem(score, message, Type.WARNING));
		}

		return problems;
	}

	public static List<MethodReport> checkMethods(Class<?> clazz, ClassSpecification spec) {
		// MethodSpecification[] methodSpecifications =
		// spec.getMethodSpecifications();
		// List<Pair<Method, MethodSpecification>> pairs =
		// Arrays.stream(methodSpecifications).parallel()
		// .flatMap(mapMethods(clazz))
		// .sorted(Comparator.comparingInt(pair ->
		// calculateDistance(pair.getLeft(), pair.getRight())))
		// .collect(Collectors.toList());
		//
		// List<Method> unusedMethods = new
		// ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));
		// List<MethodSpecification> notFoundSpecs = new
		// ArrayList<>(Arrays.asList(methodSpecifications));
		//
		// Iterator<Pair<Method, MethodSpecification>> iterator =
		// pairs.iterator();
		// while (iterator.hasNext()) {
		// Pair<Method, MethodSpecification> pair = iterator.next();
		// Method method = pair.getLeft();
		// MethodSpecification methodSpec = pair.getRight();
		// if (unusedMethods.contains(method) &&
		// notFoundSpecs.contains(methodSpec)) {
		// unusedMethods.remove(method);
		// notFoundSpecs.remove(methodSpec);
		// } else {
		// iterator.remove();
		// }
		// }
		//
		// Stream<MethodReport> foundMethods = pairs.parallelStream()
		// .map(pair -> checkMethod(pair.getLeft(), pair.getRight()));
		// Stream<MethodReport> notFoundMethods = notFoundSpecs.parallelStream()
		// .map(MethodReport::new);
		//
		// return Stream.concat(foundMethods, notFoundMethods)
		// .sorted(Comparator.comparing(MethodReport::getSpec))
		// .collect(Collectors.toList());

		return check(clazz, spec, ClassSpecification::getMethodSpecifications, StaticChecker::mapMethods, StaticChecker::calculateDistance, Class::getDeclaredMethods, StaticChecker::checkMethod,
				MethodReport::new, Comparator.comparing(MethodReport::getSpec));
	}

	private static Function<MethodSpecification, Stream<Pair<Method, MethodSpecification>>> mapMethods(Class<?> clazz) {
		return methodSpec -> Arrays.stream(clazz.getDeclaredMethods()).parallel()
				.map(method -> Pair.of(method, methodSpec));
	}

	private static <MemberType extends Member, SpecificationType extends Specification<MemberType>, ReportType extends Report<SpecificationType, MemberType>> List<ReportType> check(Class<?> clazz,
			ClassSpecification spec, Function<ClassSpecification, SpecificationType[]> specSupplier,
			Function<Class<?>, Function<SpecificationType, Stream<Pair<MemberType, SpecificationType>>>> mapperFunction, ToIntBiFunction<MemberType, SpecificationType> distanceMeasure,
			Function<Class<?>, MemberType[]> memberSupplier, BiFunction<MemberType, SpecificationType, ReportType> memberChecker, Function<SpecificationType, ReportType> emptyReportGenerator,
			Comparator<ReportType> comparator) {
		SpecificationType[] specifications = specSupplier.apply(spec);

		List<Pair<MemberType, SpecificationType>> pairs = Arrays.stream(specifications).parallel()
				.flatMap(mapperFunction.apply(clazz))
				.sorted(Comparator.comparingInt(pair -> distanceMeasure.applyAsInt(pair.getLeft(), pair.getRight())))
				.collect(Collectors.toList());

		List<MemberType> unusedMembers = new ArrayList<>(Arrays.asList(memberSupplier.apply(clazz)));
		List<SpecificationType> notFoundSpecs = new ArrayList<>(Arrays.asList(specifications));

		Iterator<Pair<MemberType, SpecificationType>> iterator = pairs.iterator();
		while (iterator.hasNext()) {
			Pair<MemberType, SpecificationType> pair = iterator.next();
			MemberType member = pair.getLeft();
			SpecificationType memberSpec = pair.getRight();
			if (unusedMembers.contains(member) && notFoundSpecs.contains(memberSpec)) {
				unusedMembers.remove(member);
				notFoundSpecs.remove(memberSpec);
			} else {
				iterator.remove();
			}
		}

		Stream<ReportType> foundMethods = pairs.parallelStream()
				.map(pair -> memberChecker.apply(pair.getLeft(), pair.getRight()));
		Stream<ReportType> notFoundMethods = notFoundSpecs.parallelStream()
				.map(emptyReportGenerator);

		return Stream.concat(foundMethods, notFoundMethods)
				.sorted(comparator)
				.collect(Collectors.toList());
	}

	private static MethodReport checkMethod(Method method, MethodSpecification spec) {
		ResolvableType specReturnType = spec.getReturnType();
		ResolvableType methodReturnType = ResolvableType.forMethodReturnType(method);
		String methodReturnTypeName = getName(methodReturnType);

		MethodReport report = new MethodReport(spec, method);

		checkVisibility(method, spec).ifPresent(report::addProblem);
		report.addProblems(checkModifiers(method, spec));

		if (!method.getName().equals(spec.getName())) {
			String format = "should be called \"%s\"";
			report.addProblem(new ReportProblem(1, String.format(format, spec.getName()), Type.ERROR));
		}

		if (methodReturnType.getRawClass() != specReturnType.getRawClass()) {
			boolean compatible = ClassUtils.isAssignable(methodReturnType, specReturnType);
			String format = "returns " + (compatible ? "" : "in") + "compatible type \"%s\"";
			Type type = compatible ? Type.WARNING : Type.ERROR;
			report.addProblem(new ReportProblem(1, String.format(format, methodReturnTypeName), type));
		}

		return report;
	}

	private static List<ReportProblem> checkMethodParameters(Method actual, MethodSpecification spec) {
		List<ReportProblem> problems = new ArrayList<>();

		int actualLength = actual.getParameterCount();
		int specLength = spec.getParameters().length;

		if (actualLength == specLength) {
			for (int i = 0; i < actualLength; i++) {
				ResolvableType specType = ResolvableType.forMethodParameter(spec.getRawElement(), i);
				ResolvableType actualType = ResolvableType.forMethodParameter(actual, i);

				if (actualType.getRawClass() != specType.getRawClass()) {
					boolean compatible = ClassUtils.isAssignable(specType, actualType);
					String format = "parameter %d has " + (compatible ? "" : "in") + "compatible type \"%s\"";
					Type type = compatible ? Type.WARNING : Type.ERROR;
					problems.add(new ReportProblem(1, String.format(format, i + 1, getName(actualType)), type));
				}
			}
		} else {
			int score = Math.abs(actualLength - specLength);
			String message = String.format("parameter count should be %s but is %s", specLength, actualLength);
			problems.add(new ReportProblem(score, message, Type.WARNING));
		}

		return problems;
	}

	public static Optional<ReportProblem> checkSuperClass(Class<?> actual, ClassSpecification spec) {
		ResolvableType rawSpecSuperClass = spec.getSuperClassSpecification().getRawElement();
		if (actual.getSuperclass() != rawSpecSuperClass.getRawClass()) {
			String format;
			if (rawSpecSuperClass.getRawClass().getName().equals("java.lang.Object")) {
				format = "should not declare any super class";
			} else {
				format = "should declare \"%s\" as its super class";
			}
			return Optional.of(new ReportProblem(1, String.format(format, getName(rawSpecSuperClass)), Type.ERROR));
		}

		return Optional.empty();
	}

	public static List<ReportProblem> checkInterfaces(Class<?> actual, ClassSpecification spec) {
		List<ReportProblem> problems = new ArrayList<>();

		List<ResolvableType> notFoundInterfaces = Arrays.stream(actual.getInterfaces())
				.map(ResolvableType::forClass)
				// needs to be mutable
				.collect(Collectors.toCollection(() -> new ArrayList<>()));

		List<InterfaceSpecification> specifications = new ArrayList<>(Arrays.asList(spec.getInterfaceSpecifications()));

		for (InterfaceSpecification specification : specifications) {
			Optional<ResolvableType> interf = notFoundInterfaces.parallelStream()
					.filter(i -> ClassUtils.equal(specification.getRawElement(), i))
					.findAny();

			if (interf.isPresent()) {
				notFoundInterfaces.remove(interf.get());
			} else {
				String format = "should implement interface \"%s\"";
				problems.add(new ReportProblem(1, String.format(format, ClassUtils.getName(interf.get())), Type.ERROR));
			}
		}

		String format = "should not implements interface \"%s\"";
		for (ResolvableType notFoundInterface : notFoundInterfaces) {
			problems.add(new ReportProblem(1, String.format(format, ClassUtils.getName(notFoundInterface)), Type.ERROR));
		}

		return problems;
	}

	public static Optional<ReportProblem> checkVisibility(Class<?> actual, ClassSpecification spec) {
		return checkVisibility(actual.getModifiers(), spec.getVisibility());
	}

	public static Optional<ReportProblem> checkVisibility(Member actual, Specification<?> spec) {
		return checkVisibility(actual.getModifiers(), spec.getVisibility());
	}

	public static Optional<ReportProblem> checkVisibility(int actualModifiers, VisibilitySpecification spec) {
		Visibility actualVisibility = getVisibility(actualModifiers);
		ReportProblem problem = null;

		if (!spec.matches(actualVisibility)) {
			Visibility[] visibilities = spec.getVisibilities();
			if (visibilities.length == 1 && visibilities[0] == Visibility.PACKAGE) {
				problem = new ReportProblem(1, "should not have any visibility modifier", Type.ERROR);
			} else if (visibilities.length == 1) {
				problem = new ReportProblem(1, String.format("should have visibility \"%s\"", visibilities[0]), Type.ERROR);
			} else {
				String visibilityString = Arrays.stream(visibilities).map(Visibility::toString).collect(Collectors.joining(", "));
				problem = new ReportProblem(1, String.format("should have any of the following visibilities: \"%s\"", visibilityString), Type.ERROR);
			}
		}

		return Optional.ofNullable(problem);
	}

	public static List<ReportProblem> checkModifiers(Class<?> actual, ClassSpecification spec) {
		ModifiersSpecification modifiersSpec = spec.getModifiers();
		return checkModifiers(actual.getModifiers(), modifiersSpec, !modifiersSpec.isInterface() || actual.isInterface());
	}

	public static List<ReportProblem> checkModifiers(Member actual, Specification<? extends Member> spec) {
		boolean checkAbstract = !spec.getRawElement().getDeclaringClass().isInterface() || actual.getDeclaringClass().isInterface();
		return checkModifiers(actual.getModifiers(), spec.getModifiers(), checkAbstract);
	}

	private static List<ReportProblem> checkModifiers(int actual, ModifiersSpecification spec, boolean checkAbstract) {
		List<Optional<ReportProblem>> problems = new ArrayList<>();

		if (checkAbstract) {
			problems.add(checkModifier(Modifier.isAbstract(actual), spec.isAbstract(), ABSTRACT));
		}
		problems.add(checkModifier(Modifier.isFinal(actual), spec.isFinal(), FINAL));
		problems.add(checkModifier(Modifier.isNative(actual), spec.isNative(), NATIVE));
		problems.add(checkModifier(Modifier.isStatic(actual), spec.isStatic(), STATIC));
		problems.add(checkModifier(Modifier.isStrict(actual), spec.isStrict(), STRICTFP));
		problems.add(checkModifier(Modifier.isSynchronized(actual), spec.isSynchronized(), SYNCHRONIZED));
		problems.add(checkModifier(Modifier.isTransient(actual), spec.isTransient(), TRANSIENT));
		problems.add(checkModifier(Modifier.isVolatile(actual), spec.isVolatile(), VOLATILE));

		return problems.parallelStream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
	}

	/**
	 * Creates and returns a {@link ReportProblem} if the actual state of the
	 * given modifiers does not match the given modifier specification state.
	 *
	 * @param actual
	 *            the actual modifier state - {@code true} if the modifier is
	 *            set, {@code false} otherwise
	 * @param spec
	 *            the modifier specification
	 * @param modifier
	 *            the modifier itself
	 * @return an empty optional if the actual modifier matches the given
	 *         specification, an optional with value that contains a
	 *         {@link ReportProblem} with a matching problem description
	 */
	private static Optional<ReportProblem> checkModifier(boolean actual, State spec, javax.lang.model.element.Modifier modifier) {
		ReportProblem problem = null;

		if (spec == State.TRUE && !spec.matches(actual)) {
			String format = "should have modifier \"%s\"";
			problem = new ReportProblem(1, String.format(format, modifier), Type.WARNING);
		}

		if (spec == State.FALSE && !spec.matches(actual)) {
			String format = "should not have modifier \"%s\"";
			problem = new ReportProblem(1, String.format(format, modifier), Type.WARNING);
		}

		return Optional.ofNullable(problem);
	}

	private static int calculateDistance(Field field, FieldSpecification spec) {
		int nameDistance = NAME_SIMILARITY.apply(field.getName(), spec.getName());

		ResolvableType fieldType = FieldUtils.getType(field);
		int typeDistance = field.getType() == spec.getType().getRawClass() ? 0 : ClassUtils.isAssignable(spec.getType(), fieldType) ? 5 : 10;

		return MathUtils.multiplyWithoutOverflow(nameDistance, typeDistance);
	}

	private static int calculateDistance(Constructor<?> constructor, ConstructorSpecification spec) {
		ResolvableType[] parameters = ConstructorUtils.getParametersAsResolvableType(constructor);
		ResolvableType[] specParameters = Arrays.stream(spec.getParameters())
				.map(MethodParameterSpecification::getType)
				.toArray(ResolvableType[]::new);

		return MethodUtils.calculateParameterDistance(parameters, specParameters);
	}

	private static int calculateDistance(Method actual, MethodSpecification spec) {
		if (actual.equals(spec.getRawElement())) {
			return 0;
		}

		int nameSimilarity = NAME_SIMILARITY.apply(actual.getName(), spec.getName()) * 2 + 1;
		int heuristic = 0;
		heuristic += checkVisibility(actual, spec).map(ReportProblem::getScore).orElse(0);
		heuristic += checkModifiers(actual, spec).parallelStream()
				.mapToLong(ReportProblem::getScore)
				.sum();
		heuristic += checkMethodParameters(actual, spec).parallelStream()
				.mapToLong(ReportProblem::getScore)
				.sum();

		return MathUtils.multiplyWithoutOverflow(nameSimilarity, heuristic + 1);
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> clazz, MethodInvocationHandler handler) {
		if (clazz.isInterface()) {
			return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, handler);
		} else {
			ProxyFactory factory = new ProxyFactory();
			factory.setSuperclass(clazz);
			factory.setFilter(e -> !MethodUtils.isAbstract(e));
			Class<?> proxyClass = factory.createClass();
			T proxy = (T) OBJENESIS.newInstance(proxyClass);
			((javassist.util.proxy.Proxy) proxy).setHandler(handler);
			return proxy;
		}
	}

	public static MethodInvocationHandler createInvocationHandler(Class<?> spec, SpecReport report) {
		final List<ClassReport> classReports = report.getClassReports();
		if (classReports.isEmpty()) {
			String format = "no implementation of \"%s\" could be found";
			String classString = ClassUtils.toString(spec);
			throw new UnsupportedOperationException(String.format(format, classString));
		}

		String specName = ClassUtils.getName(spec);

		try {
			ClassReport classReport = classReports.get(0);
			Class<?> implementingClass = classReport.getImplementation().getRawClass();
			String implementationName = ClassUtils.getName(implementingClass);
			Object implementation = implementingClass.newInstance();

			Map<Method, MethodReport> methodReports = classReport.getMethodReports().parallelStream()
					.collect(Collectors.toMap(e -> e.getSpec().getRawElement(), Function.identity()));

			return (proxy, method, args) -> {
				MethodReport actualMethod = methodReports.get(method);

				if (actualMethod == null || actualMethod.getImplementation() == null) {
					String methodName = method.getName();
					String parameterList = MethodUtils.getParameterList(method);
					String format = "no implementation of %s#%s(%s) could be found in %s";
					throw new UnsupportedOperationException(String.format(format, specName, methodName, parameterList, implementationName));
				} else {
					return actualMethod.getImplementation().invoke(implementation, args);
				}
			};
		} catch (InstantiationException | IllegalAccessException e1) {
			throw new IllegalArgumentException();
		}
	}
}
