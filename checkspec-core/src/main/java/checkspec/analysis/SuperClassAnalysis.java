package checkspec.analysis;

import static checkspec.util.ClassUtils.getName;

import java.util.Optional;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.spec.ClassSpecification;
import checkspec.spring.ResolvableType;

public class SuperClassAnalysis implements AnalysisForClass<Optional<ReportProblem>> {

	@Override
	public Optional<ReportProblem> analyse(ResolvableType actual, ClassSpecification specification) {
		ResolvableType rawSpecSuperClass = specification.getSuperClassSpecification().getRawElement();
		if (actual.getRawClass().getSuperclass() != rawSpecSuperClass.getRawClass()) {
			String format;
			if (rawSpecSuperClass.getRawClass().getName().equals("java.lang.Object")) {
				format = "should not declare any super class";
			} else {
				format = "should declare \"%s\" as its super class";
			}
			return Optional.of(new ReportProblem(1, String.format(format, getName(rawSpecSuperClass)), Type.ERROR));
		}

		return Optional.empty();
	}

	@Override
	public void add(ClassReport report, Optional<ReportProblem> returnType) {
		returnType.ifPresent(report::addProblem);
	}
}
