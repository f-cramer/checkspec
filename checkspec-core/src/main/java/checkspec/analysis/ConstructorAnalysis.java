package checkspec.analysis;

import java.lang.reflect.Constructor;
import java.util.Collection;

import checkspec.report.ClassReport;
import checkspec.report.ConstructorReport;
import checkspec.report.ParametersReport;
import checkspec.spec.ClassSpecification;
import checkspec.spec.ConstructorSpecification;
import checkspec.spec.ParametersSpecification;

public class ConstructorAnalysis extends ExecutableAnalysis<Constructor<?>, ConstructorSpecification, ConstructorReport> {

	@Override
	protected ConstructorSpecification[] getMemberSpecifications(ClassSpecification spec) {
		return spec.getConstructorSpecifications();
	}

	@Override
	protected Constructor<?>[] getMembers(Class<?> clazz) {
		return clazz.getDeclaredConstructors();
	}

	@Override
	protected ConstructorReport checkMember(Constructor<?> constructor, ConstructorSpecification spec) {
		ParametersReport parametersReport = PARAMETERS_ANALYSIS.analyse(constructor.getParameters(), spec.getParameters());
		ConstructorReport report = new ConstructorReport(spec, constructor, parametersReport);

		VISIBILITY_ANALYSIS.analyse(constructor, spec).ifPresent(report::addProblem);
		report.addProblems(MODIFIERS_ANALYSIS.analyse(constructor, spec));

		return report;
	}

	@Override
	protected ConstructorReport createEmptyReport(ConstructorSpecification specification) {
		return new ConstructorReport(specification);
	}

	@Override
	protected ParametersSpecification getParametersSpecification(ConstructorSpecification specification) {
		return specification.getParameters();
	}

	@Override
	public void add(ClassReport report, Collection<? extends ConstructorReport> returnType) {
		report.addConstructorReports(returnType);
	}
}
