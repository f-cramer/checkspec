package checkspec.analysis;

import java.util.Map;
import java.util.Optional;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.specification.ClassSpecification;
import checkspec.type.ResolvableType;

public class ClassVisibilityAnalysis extends AbstractVisibilityAnalysis implements AnalysisForClass<Optional<ReportProblem>> {

	@Override
	public Optional<ReportProblem> analyze(ResolvableType actual, ClassSpecification specification, Map<ClassSpecification, ClassReport> oldReports) {
		return analyseVisibility(actual.getRawClass().getModifiers(), specification.getVisibility());
	}

	@Override
	public void add(ClassReport report, Optional<ReportProblem> returnType) {
		returnType.ifPresent(report::addProblem);
	}
}
