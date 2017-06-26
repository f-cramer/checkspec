package checkspec.analysis;

import static checkspec.util.ClassUtils.getName;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;

import checkspec.report.ClassReport;
import checkspec.report.MethodReport;
import checkspec.report.ParametersReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.spec.ClassSpecification;
import checkspec.spec.MethodSpecification;
import checkspec.spec.ParametersSpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import lombok.Getter;

@Getter
public class MethodAnalysis extends ExecutableAnalysis<Method, MethodSpecification, MethodReport> {

	private static final String NAME = "should have name \"%s\"";
	private static final String COMPATIBLE_TYPE = "returns compatible type \"%s\"";
	private static final String INCOMPATIBLE_TYPE = "returns incompatible type \"%s\"";

	private Comparator<MethodReport> comparator = Comparator.comparing(MethodReport::getSpec);

	@Override
	protected MethodSpecification[] getMemberSpecifications(ClassSpecification spec) {
		return spec.getMethodSpecifications();
	}

	@Override
	protected Method[] getMembers(Class<?> clazz) {
		return clazz.getDeclaredMethods();
	}

	@Override
	protected MethodReport checkMember(Method method, MethodSpecification spec) {
		ParametersReport parametersReport = PARAMETERS_ANALYSIS.analyse(method.getParameters(), spec.getParameters());
		MethodReport report = new MethodReport(spec, method, parametersReport);

		VISIBILITY_ANALYSIS.analyse(method, spec).ifPresent(report::addProblem);
		report.addProblems(MODIFIERS_ANALYSIS.analyse(method, spec));

		String methodName = method.getName();
		String specName = spec.getName();

		if (!methodName.equals(specName)) {
			int score = calculateDistance(methodName, specName);
			report.addProblem(new ReportProblem(score, String.format(NAME, specName), Type.ERROR));
		}

		ResolvableType specReturnType = spec.getReturnType();
		ResolvableType methodReturnType = ResolvableType.forMethodReturnType(method);
		if (methodReturnType.getRawClass() != specReturnType.getRawClass()) {
			boolean compatible = ClassUtils.isAssignable(methodReturnType, specReturnType);
			String format = compatible ? COMPATIBLE_TYPE : INCOMPATIBLE_TYPE;
			Type type = compatible ? Type.WARNING : Type.ERROR;
			report.addProblem(new ReportProblem(1, String.format(format, getName(methodReturnType)), type));
		}

		return report;
	}

	@Override
	protected MethodReport createEmptyReport(MethodSpecification specification) {
		return new MethodReport(specification);
	}

	@Override
	protected ParametersSpecification getParametersSpecification(MethodSpecification specification) {
		return specification.getParameters();
	}

	@Override
	public void add(ClassReport report, Collection<? extends MethodReport> returnType) {
		report.addMethodReports(returnType);
	}
}
