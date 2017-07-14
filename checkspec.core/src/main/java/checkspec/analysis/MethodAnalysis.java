package checkspec.analysis;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import checkspec.report.ClassReport;
import checkspec.report.MethodReport;
import checkspec.report.ParametersReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.specification.MethodSpecification;
import checkspec.spring.ResolvableType;
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
	protected MethodReport checkMember(Method method, MethodSpecification spec, Map<ResolvableType, ClassReport> oldReports) {
		ParametersReport parametersReport = PARAMETERS_ANALYSIS.analyze(method.getParameters(), spec.getParameters(), oldReports);
		MethodReport report = new MethodReport(spec, method, parametersReport);

		VISIBILITY_ANALYSIS.analyze(method, spec).ifPresent(report::addProblem);
		report.addProblems(MODIFIERS_ANALYSIS.analyze(method, spec));

		String methodName = method.getName();
		String specName = spec.getName();

		if (!methodName.equals(specName)) {
			int score = calculateDistance(methodName, specName);
			report.addProblem(new ReportProblem(score, String.format(NAME, specName), ReportProblemType.ERROR));
		}

		ResolvableType specReturnType = spec.getReturnType();
		ResolvableType methodReturnType = ResolvableType.forMethodReturnType(method);
		AnalysisUtils.compareTypes(specReturnType, methodReturnType, oldReports, (s, a) -> String.format(COMPATIBLE_TYPE, s, a), (a, s) -> String.format(INCOMPATIBLE_TYPE, a, s))
				.ifPresent(report::addProblem);
//		if (methodReturnType.getRawClass() != specReturnType.getRawClass()) {
//			boolean compatible = ClassUtils.isAssignable(methodReturnType, specReturnType);
//			String format = compatible ? COMPATIBLE_TYPE : INCOMPATIBLE_TYPE;
//			ReportProblemType type = compatible ? ReportProblemType.WARNING : ReportProblemType.ERROR;
//			report.addProblem(new ReportProblem(1, String.format(format, getName(methodReturnType)), type));
//		}

		return report;
	}

	@Override
	protected MethodReport createEmptyReport(MethodSpecification specification) {
		return new MethodReport(specification);
	}

	@Override
	public void add(ClassReport report, Collection<? extends MethodReport> returnType) {
		report.addMethodReports(returnType);
	}
}
