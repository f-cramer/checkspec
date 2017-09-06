package checkspec.analysis;

import java.util.Optional;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.type.MatchableType;

public class ClassNameAnalysis implements ClassAnalysis<Optional<ReportProblem>> {

	private static final String SHOULD = "should have name %s";

	@Override
	public Optional<ReportProblem> analyze(MatchableType actual, ClassSpecification specification, MultiValuedMap<Class<?>, Class<?>> payload) {
		String specificationName = specification.getName();
		String implementationName = actual.getRawClass().getName();

		if (specificationName.equals(implementationName)) {
			return Optional.empty();
		} else {
			return Optional.of(new ReportProblem(25, String.format(SHOULD, specificationName), ReportProblemType.ERROR));
		}
	}

	@Override
	public void add(ClassReport report, Optional<ReportProblem> returnType) {
		returnType.ifPresent(report::addProblem);
	}
}
