package checkspec.analysis;

import static checkspec.util.ClassUtils.getName;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import checkspec.report.ClassReport;
import checkspec.report.ConstructorReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.spec.ClassSpecification;
import checkspec.spec.ConstructorSpecification;
import checkspec.spec.MethodParameterSpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import checkspec.util.ConstructorUtils;
import checkspec.util.MethodUtils;
import lombok.Getter;

@Getter
public class ConstructorAnalysis extends MemberAnalysis<Constructor<?>, ConstructorSpecification, ConstructorReport> {
	
	private static final String COMPATIBLE_TYPE = "parameter %d has compatible type \"%s\"";
	private static final String INCOMPATIBLE_TYPE = "parameter %d has incompatible type \"%s\"";
	private static final String PARAMETER_COUNT = "parameter count should be %s but is %s";

	private Comparator<ConstructorReport> comparator = Comparator.comparing(ConstructorReport::getSpec);
	
	@Override
	protected ConstructorSpecification[] getMemberSpecifications(ClassSpecification spec) {
		return spec.getConstructorSpecifications();
	}

	@Override
	protected Function<ConstructorSpecification, Stream<Pair<Constructor<?>, ConstructorSpecification>>> getMapperFunction(Class<?> clazz) {
		return constructorSpec -> Arrays.stream(clazz.getDeclaredConstructors()).parallel()
				.map(constructor -> Pair.of(constructor, constructorSpec));
	}

	@Override
	protected int getDistance(Constructor<?> constructor, ConstructorSpecification spec) {
		ResolvableType[] parameters = ConstructorUtils.getParametersAsResolvableType(constructor);
		ResolvableType[] specParameters = Arrays.stream(spec.getParameters())
				.map(MethodParameterSpecification::getType)
				.toArray(ResolvableType[]::new);

		return MethodUtils.calculateParameterDistance(parameters, specParameters);
	}

	@Override
	protected Constructor<?>[] getMembers(Class<?> clazz) {
		return clazz.getDeclaredConstructors();
	}

	@Override
	protected ConstructorReport checkMember(Constructor<?> constructor, ConstructorSpecification spec) {
		List<ReportProblem> problems = new ArrayList<>();

		VISIBILITY_ANALYSIS.analyse(constructor, spec).ifPresent(problems::add);
		problems.addAll(MODIFIERS_ANALYSIS.analyse(constructor, spec));
		problems.addAll(checkConstructorParameters(constructor, spec));

		ConstructorReport report = new ConstructorReport(spec, constructor);
		return report;
	}

	@Override
	protected ConstructorReport createEmptyReport(ConstructorSpecification specification) {
		return new ConstructorReport(specification);
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
					String format = compatible ? COMPATIBLE_TYPE : INCOMPATIBLE_TYPE;
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
	public void add(ClassReport report, Collection<? extends ConstructorReport> returnType) {
		report.addConstructorReports(returnType);
	}
}
