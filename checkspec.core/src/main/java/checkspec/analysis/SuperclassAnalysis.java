package checkspec.analysis;

import static checkspec.util.ClassUtils.*;

import java.util.Map;
import java.util.Optional;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.type.ResolvableType;
import checkspec.util.ClassUtils;

public class SuperclassAnalysis implements AnalysisForClass<Optional<ReportProblem>> {

	private static final Class<?> OBJECT_CLASS = Object.class;
	private static final String HAS_BUT_SHOULD_NOT = "should not declare any super class";
	private static final String DECLARES_COMPATIBLE = "declares compatible super class \"%s\"";
	private static final String INCORRECT_GENERICS = "super type has incorrect generics";
	private static final String SHOULD_DECLARE_DIFFERENT = "should declare \"%s\" as its super class";

	@Override
	public Optional<ReportProblem> analyze(ResolvableType actual, ClassSpecification specification, Map<ClassSpecification, ClassReport> oldReports) {
		ResolvableType specificationSuperType = specification.getSuperclassSpecification().getRawElement();
		ResolvableType actualSuperType = actual.getSuperType();

		if (ClassUtils.equal(specificationSuperType, actualSuperType)) {
			return Optional.empty();
		}

		if (OBJECT_CLASS == specificationSuperType.getRawClass() && OBJECT_CLASS != actualSuperType.getRawClass()) {
			return Optional.of(new ReportProblem(1, HAS_BUT_SHOULD_NOT, ReportProblemType.WARNING));
		}

		if (ClassUtils.isAssignable(actualSuperType, specificationSuperType)) {
			String message = String.format(DECLARES_COMPATIBLE, getName(specificationSuperType));
			return Optional.of(new ReportProblem(1, message, ReportProblemType.ERROR));
		}

		if (specificationSuperType.getRawClass() == actualSuperType.getRawClass()) {
			return Optional.of(new ReportProblem(1, INCORRECT_GENERICS, ReportProblemType.WARNING));
		}

		String message = String.format(SHOULD_DECLARE_DIFFERENT, getName(specificationSuperType));
		return Optional.of(new ReportProblem(1, message, ReportProblemType.ERROR));
	}

	@Override
	public void add(ClassReport report, Optional<ReportProblem> returnType) {
		returnType.ifPresent(report::addProblem);
	}
}
