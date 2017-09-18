package checkspec.analysis;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.report.MethodReport;
import checkspec.report.ParametersReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.specification.MethodSpecification;
import checkspec.type.MatchableType;
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
	protected MethodReport checkMember(Method method, MethodSpecification spec, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		ParametersReport parametersReport = PARAMETERS_ANALYSIS.analyze(method.getParameters(), spec.getParameters(), oldReports);
		MethodReport report = new MethodReport(spec, method, parametersReport);

		VISIBILITY_ANALYSIS.analyze(method, spec).ifPresent(report::addProblem);
		report.addProblems(MODIFIERS_ANALYSIS.analyze(method, spec));
		report.addProblems(EXCEPTION_ANALYSIS.analyze(method, spec, oldReports));

		String methodName = method.getName();
		String specName = spec.getName();

		if (!methodName.equals(specName)) {
			int score = calculateDistance(methodName, specName);
			report.addProblem(new ReportProblem(score, String.format(NAME, specName), ReportProblemType.ERROR));
		}

		MatchableType specReturnType = spec.getReturnType();
		MatchableType methodReturnType = MatchableType.forMethodReturnType(method);
		AnalysisUtils.compareTypes(specReturnType, methodReturnType, oldReports, (s, a) -> String.format(COMPATIBLE_TYPE, s, a), (a, s) -> String.format(INCOMPATIBLE_TYPE, a, s))
				.ifPresent(report::addProblem);

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
