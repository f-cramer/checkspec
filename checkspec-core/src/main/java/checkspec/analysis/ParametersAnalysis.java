package checkspec.analysis;

import static checkspec.util.ClassUtils.getName;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import checkspec.report.ParametersReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.spec.ParameterSpecification;
import checkspec.spec.ParametersSpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;

public class ParametersAnalysis implements Analysis<Parameter[], ParametersSpecification, ParametersReport> {

	private static final String COMPATIBLE_TYPE = "parameter %d has compatible type \"%s\"";
	private static final String INCOMPATIBLE_TYPE = "parameter %d has incompatible type \"%s\"";
	private static final String PARAMETER_COUNT = "parameter count should be %s but is %s";

	@Override
	public ParametersReport analyse(Parameter[] actual, ParametersSpecification specification) {
		ParametersReport report = new ParametersReport(specification, actual);

		int memberParameterCount = actual.length;
		int specParameterCount = specification.getCount();

		if (memberParameterCount == specParameterCount) {
			for (int i = 0; i < memberParameterCount; i++) {
				ParameterSpecification spec = specification.get(i);
				Parameter parameter = actual[i];

				ResolvableType specType = spec.getType();
				ResolvableType actualType = getType(parameter);

				if (actualType.getRawClass() != specType.getRawClass()) {
					boolean compatible = ClassUtils.isAssignable(specType, actualType);
					String format = compatible ? COMPATIBLE_TYPE : INCOMPATIBLE_TYPE;
					Type type = compatible ? Type.WARNING : Type.ERROR;
					report.addProblem(new ReportProblem(1, String.format(format, i + 1, getName(actualType)), type));
				}
			}
		} else {
			int score = Math.abs(memberParameterCount - specParameterCount);
			String message = String.format(PARAMETER_COUNT, specParameterCount, memberParameterCount);
			report.addProblem(new ReportProblem(score, message, Type.WARNING));
		}

		return report;
	}

	private static ResolvableType getType(Parameter parameter) {
		Executable executable = parameter.getDeclaringExecutable();
		OptionalInt optionalIndex = findIndex(parameter);

		if (optionalIndex.isPresent()) {
			int index = optionalIndex.getAsInt();

			if (executable instanceof Constructor<?>) {
				return ResolvableType.forConstructorParameter((Constructor<?>) executable, index);
			} else if (executable instanceof Method) {
				return ResolvableType.forMethodParameter((Method) executable, index);
			}
		}

		return ResolvableType.forType(parameter.getParameterizedType());
	}

	private static OptionalInt findIndex(Parameter parameter) {
		Executable executable = parameter.getDeclaringExecutable();
		return IntStream.range(0, executable.getParameterCount())
				.filter(index -> executable.getParameters()[index] == parameter)
				.findFirst();
	}
}
