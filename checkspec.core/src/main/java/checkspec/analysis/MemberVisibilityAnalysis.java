package checkspec.analysis;

import java.lang.reflect.Member;
import java.util.Optional;

import checkspec.report.ReportProblem;
import checkspec.specification.MemberSpecification;

public class MemberVisibilityAnalysis extends AbstractVisibilityAnalysis implements AnalysisWithoutPayload<Member, MemberSpecification<? extends Member>, Optional<ReportProblem>> {

	@Override
	public Optional<ReportProblem> analyze(Member actual, MemberSpecification<? extends Member> specification, Void payload) {
		return analyseVisibility(actual.getModifiers(), specification.getVisibility());
	}
}
