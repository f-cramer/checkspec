package checkspec.analysis;

import static checkspec.util.ClassUtils.*;

import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
class AnalysisUtils {

	public static Optional<ReportProblem> compareTypes(ResolvableType specification, ResolvableType actual, Map<ResolvableType, ClassReport> oldReports, BinaryOperator<String> compatible, BinaryOperator<String> incompatible) {
		if (isTypeMatching(specification, actual, oldReports)) {
			return Optional.empty();
		}

		String specificationName = getName(specification);
		String actualName = getName(actual);
		if (isCompatible(specification, actual, oldReports)) {
			return Optional.of(new ReportProblem(5, compatible.apply(specificationName, actualName), ReportProblemType.WARNING));
		} else {
			return Optional.of(new ReportProblem(10, incompatible.apply(specificationName, actualName), ReportProblemType.ERROR));
		}
	}

	private static boolean isTypeMatching(ResolvableType specification, ResolvableType actual, Map<ResolvableType, ClassReport> oldReports) {
		if (equal(specification, actual)) {
			return true;
		}

		if (oldReports == null) {
			return false;
		}

		ClassReport oldReport = oldReports.get(specification);
		return oldReport != null && equal(actual, oldReport.getImplementation());
	}

	private static boolean isCompatible(ResolvableType specification, ResolvableType actual, Map<ResolvableType, ClassReport> oldReports) {
		if (ClassUtils.isCompatible(specification, actual)) {
			return true;
		}

		if (oldReports == null) {
			return false;
		}

		ClassReport oldReport = oldReports.get(specification);
		return oldReport != null && ClassUtils.isCompatible(oldReport.getImplementation(), actual);
	}
}
