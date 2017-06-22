package checkspec.analysis;

import java.util.Optional;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.spec.ClassSpecification;
import checkspec.spring.ResolvableType;

public class ClassVisibilityAnalysis extends AbstractVisibilityAnalysis implements AnalysisForClass<Optional<ReportProblem>> {

	@Override
	public Optional<ReportProblem> analyse(ResolvableType actual, ClassSpecification specification) {
		return analyseVisibility(actual.getRawClass().getModifiers(), specification.getVisibility());
	}

	@Override
	public void add(ClassReport report, Optional<ReportProblem> returnType) {
		returnType.ifPresent(report::addProblem);
	}
}
