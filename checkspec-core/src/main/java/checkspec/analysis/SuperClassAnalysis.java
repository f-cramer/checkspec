package checkspec.analysis;

import static checkspec.util.ClassUtils.getName;

import java.util.Optional;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblem.Type;
import checkspec.spec.ClassSpecification;
import checkspec.spring.ResolvableType;

public class SuperClassAnalysis implements AnalysisForClass<Optional<ReportProblem>> {
	
	private static final String OBJECT_CLASS_NAME = Object.class.getName();
	private static final String SHOULD = "should not declare any super class";
	private static final String SHOULD_NOT = "should declare \"%s\" as its super class";

	@Override
	public Optional<ReportProblem> analyse(ResolvableType actual, ClassSpecification specification) {
		ResolvableType rawSpecSuperClass = specification.getSuperClassSpecification().getRawElement();
		if (actual.getRawClass().getSuperclass() != rawSpecSuperClass.getRawClass()) {
			String format;
			if (OBJECT_CLASS_NAME.equals(rawSpecSuperClass.getRawClass().getName())) {
				format = SHOULD;
			} else {
				format = SHOULD_NOT;
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