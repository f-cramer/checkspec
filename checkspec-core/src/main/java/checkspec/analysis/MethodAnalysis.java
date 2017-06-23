package checkspec.analysis;

import static checkspec.util.ClassUtils.getName;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import checkspec.report.ClassReport;
import checkspec.report.MethodReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.spec.ClassSpecification;
import checkspec.spec.MethodSpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import checkspec.util.MathUtils;
import lombok.Getter;

@Getter
public class MethodAnalysis extends MemberAnalysis<Method, MethodSpecification, MethodReport> {

	private static final String NAME = "should have name \"%s\"";
	private static final String COMPATIBLE_TYPE = "returns compatible type \"%s\"";
	private static final String INCOMPATIBLE_TYPE = "returns incompatible type \"%s\"";
	private static final String PARAMETER_COMPATIBLE_TYPE = "parameter %d has compatible type \"%s\"";
	private static final String PARAMETER_INCOMPATIBLE_TYPE = "parameter %d has incompatible type \"%s\"";
	private static final String PARAMETER_COUNT = "parameter count should be %s but is %s";
	
	private Comparator<MethodReport> comparator = Comparator.comparing(MethodReport::getSpec);
	
	@Override
	protected MethodSpecification[] getMemberSpecifications(ClassSpecification spec) {
		return spec.getMethodSpecifications();
	}

	@Override
	protected Function<MethodSpecification, Stream<Pair<Method, MethodSpecification>>> getMapperFunction(Class<?> clazz) {
		return methodSpec -> Arrays.stream(clazz.getDeclaredMethods()).parallel()
				.map(method -> Pair.of(method, methodSpec));
	}

	@Override
	protected int getDistance(Method actual, MethodSpecification spec) {
		if (actual.equals(spec.getRawElement())) {
			return 0;
		}

		int nameSimilarity = NAME_SIMILARITY.apply(actual.getName(), spec.getName()) * 2 + 1;
		int heuristic = 0;
		heuristic += VISIBILITY_ANALYSIS.analyse(actual, spec).map(ReportProblem::getScore).orElse(0);
		heuristic += MODIFIERS_ANALYSIS.analyse(actual, spec).parallelStream()
				.mapToLong(ReportProblem::getScore)
				.sum();
		heuristic += checkMethodParameters(actual, spec).parallelStream()
				.mapToLong(ReportProblem::getScore)
				.sum();

		return MathUtils.multiplyWithoutOverflow(nameSimilarity, heuristic + 1);
	}

	@Override
	protected Method[] getMembers(Class<?> clazz) {
		return clazz.getDeclaredMethods();
	}

	@Override
	protected MethodReport checkMember(Method method, MethodSpecification spec) {
		ResolvableType specReturnType = spec.getReturnType();
		ResolvableType methodReturnType = ResolvableType.forMethodReturnType(method);
		String methodReturnTypeName = getName(methodReturnType);

		MethodReport report = new MethodReport(spec, method);

		VISIBILITY_ANALYSIS.analyse(method, spec).ifPresent(report::addProblem);
		report.addProblems(MODIFIERS_ANALYSIS.analyse(method, spec));

		if (!method.getName().equals(spec.getName())) {
			report.addProblem(new ReportProblem(1, String.format(NAME, spec.getName()), Type.ERROR));
		}

		if (methodReturnType.getRawClass() != specReturnType.getRawClass()) {
			boolean compatible = ClassUtils.isAssignable(methodReturnType, specReturnType);
			String format = compatible ? COMPATIBLE_TYPE : INCOMPATIBLE_TYPE;
			Type type = compatible ? Type.WARNING : Type.ERROR;
			report.addProblem(new ReportProblem(1, String.format(format, methodReturnTypeName), type));
		}

		return report;
	}

	@Override
	protected MethodReport createEmptyReport(MethodSpecification specification) {
		return new MethodReport(specification);
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
					String format = compatible ? PARAMETER_COMPATIBLE_TYPE : PARAMETER_INCOMPATIBLE_TYPE;
					Type type = compatible ? Type.WARNING : Type.ERROR;
					problems.add(new ReportProblem(1, String.format(format, i + 1, getName(actualType)), type));
				}
			}
		} else {
			int score = Math.abs(actualLength - specLength);
			String message = String.format(PARAMETER_COUNT, specLength, actualLength);
			problems.add(new ReportProblem(score, message, Type.WARNING));
		}

		return problems;
	}

	@Override
	public void add(ClassReport report, Collection<? extends MethodReport> returnType) {
		report.addMethodReports(returnType);
	}
}
