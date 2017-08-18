package checkspec.analysis;

import static checkspec.util.ClassUtils.*;

import java.util.Optional;
import java.util.function.BinaryOperator;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.type.MatchableType;
import checkspec.util.MatchingState;
import lombok.experimental.UtilityClass;

@UtilityClass
class AnalysisUtils {

	public static Optional<ReportProblem> compareTypes(MatchableType specification, MatchableType actual, MultiValuedMap<Class<?>, Class<?>> oldReports, BinaryOperator<String> compatible,
			BinaryOperator<String> incompatible) {
		MatchingState state = specification.matches(actual, oldReports);
		if (state == MatchingState.FULL_MATCH) {
			return Optional.empty();
		}

		String specificationName = getName(specification);
		String actualName = getName(actual);
		if (state == MatchingState.PARTIAL_MATCH) {
			return Optional.of(new ReportProblem(5, compatible.apply(specificationName, actualName), ReportProblemType.WARNING));
		} else {
			return Optional.of(new ReportProblem(10, incompatible.apply(specificationName, actualName), ReportProblemType.ERROR));
		}
	}
}
