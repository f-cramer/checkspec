package checkspec.analysis;

import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.specification.ClassSpecification;
import checkspec.specification.ModifiersSpecification;
import checkspec.type.MatchableType;

public class ClassModifiersAnalysis extends AbstractModifiersAnalysis implements ClassAnalysis<List<ReportProblem>> {

	@Override
	public List<ReportProblem> analyze(MatchableType actual, ClassSpecification spec, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		ModifiersSpecification modifiersSpec = spec.getModifiers();
		return analyzeModifiers(actual.getRawClass().getModifiers(), modifiersSpec, !modifiersSpec.isInterface() || actual.getRawClass().isInterface());
	}

	@Override
	public void add(ClassReport report, List<ReportProblem> returnType) {
		report.addProblems(returnType);
	}
}
