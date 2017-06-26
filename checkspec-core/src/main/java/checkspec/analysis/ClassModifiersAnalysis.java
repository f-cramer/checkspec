package checkspec.analysis;

import java.util.List;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.spec.ClassSpecification;
import checkspec.spec.ModifiersSpecification;
import checkspec.spring.ResolvableType;

public class ClassModifiersAnalysis extends AbstractModifiersAnalysis implements AnalysisForClass<List<ReportProblem>> {

	@Override
	public List<ReportProblem> analyze(ResolvableType actual, ClassSpecification spec) {
		ModifiersSpecification modifiersSpec = spec.getModifiers();
		return analyze(actual.getRawClass().getModifiers(), modifiersSpec, !modifiersSpec.isInterface() || actual.getRawClass().isInterface());
	}

	@Override
	public void add(ClassReport report, List<ReportProblem> returnType) {
		report.addProblems(returnType);
	}
}
