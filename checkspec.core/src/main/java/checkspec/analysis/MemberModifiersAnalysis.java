package checkspec.analysis;

import java.lang.reflect.Member;
import java.util.List;

import checkspec.report.ReportProblem;
import checkspec.specification.MemberSpecification;

public class MemberModifiersAnalysis extends AbstractModifiersAnalysis implements Analysis<Member, MemberSpecification<? extends Member>, List<ReportProblem>> {

	@Override
	public List<ReportProblem> analyze(Member actual, MemberSpecification<? extends Member> specification) {
		boolean checkAbstract = !specification.getRawElement().getDeclaringClass().isInterface() || actual.getDeclaringClass().isInterface();
		return analyzeModifiers(actual.getModifiers(), specification.getModifiers(), checkAbstract);
	}

}
