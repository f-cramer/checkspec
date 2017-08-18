package checkspec.analysis;

import java.util.Optional;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.specification.ClassSpecification;
import checkspec.type.MatchableType;

public class ClassVisibilityAnalysis extends AbstractVisibilityAnalysis implements ClassAnalysis<Optional<ReportProblem>> {

	@Override
	public Optional<ReportProblem> analyze(MatchableType actual, ClassSpecification specification, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		return analyseVisibility(actual.getRawClass().getModifiers(), specification.getVisibility());
	}

	@Override
	public void add(ClassReport report, Optional<ReportProblem> returnType) {
		returnType.ifPresent(report::addProblem);
	}
}
