package checkspec.analysis;

import java.util.List;
import java.util.Map;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.specification.ClassSpecification;
import checkspec.specification.ModifiersSpecification;
import checkspec.type.ResolvableType;

public class ClassModifiersAnalysis extends AbstractModifiersAnalysis implements AnalysisForClass<List<ReportProblem>> {

	@Override
	public List<ReportProblem> analyze(ResolvableType actual, ClassSpecification spec, Map<ClassSpecification, ClassReport> oldReports) {
		ModifiersSpecification modifiersSpec = spec.getModifiers();
		return analyzeModifiers(actual.getRawClass().getModifiers(), modifiersSpec, !modifiersSpec.isInterface() || actual.getRawClass().isInterface());
	}

	@Override
	public void add(ClassReport report, List<ReportProblem> returnType) {
		report.addProblems(returnType);
	}
}
