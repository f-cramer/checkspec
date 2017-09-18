package checkspec.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.tuple.Pair;

import checkspec.report.ClassReport;
import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ClassSpecification;
import checkspec.specification.InterfaceSpecification;
import checkspec.type.MatchableType;
import checkspec.util.ClassUtils;
import checkspec.util.MatchingState;

public class InterfaceAnalysis implements ClassAnalysis<List<ReportProblem>> {

	private static final String SHOULD = "should implement interface \"%s\"";
	private static final String SHOULD_NOT = "should not implement interface \"%s\"";

	@Override
	public List<ReportProblem> analyze(MatchableType actual, ClassSpecification spec, MultiValuedMap<Class<?>, Class<?>> payload) {
		List<ReportProblem> problems = new ArrayList<>();

		List<MatchableType> notFoundInterfaces = Arrays.stream(actual.getRawClass().getGenericInterfaces())
				.map(MatchableType::forType)
				// needs to be mutable
				.collect(Collectors.toCollection(ArrayList::new));

		InterfaceSpecification[] specifications = spec.getInterfaceSpecifications();

		for (InterfaceSpecification specification : specifications) {
			Optional<Pair<MatchableType, MatchingState>> interf = notFoundInterfaces.parallelStream()
					.map(i -> Pair.of(i, specification.getRawElement().matches(i, payload)))
					.max(Comparator.comparingInt(i -> i.getRight().evaluate(2, 1, 0)));

			if (interf.isPresent()) {
				if (interf.get().getRight() == MatchingState.PARTIAL_MATCH) {
					problems.add(new ReportProblem(5, String.format(SHOULD,
							ClassUtils.getName(specification.getRawElement()), ReportProblemType.ERROR), ReportProblemType.WARNING));
				}
				notFoundInterfaces.remove(interf.get().getLeft());
			} else {
				problems.add(new ReportProblem(15, String.format(SHOULD, ClassUtils.getName(specification.getRawElement())), ReportProblemType.ERROR));
			}
		}

		for (MatchableType notFoundInterface : notFoundInterfaces) {
			problems.add(new ReportProblem(15, String.format(SHOULD_NOT, ClassUtils.getName(notFoundInterface)), ReportProblemType.ERROR));
		}

		return problems;
	}

	@Override
	public void add(ClassReport report, List<ReportProblem> returnType) {
		report.addProblems(returnType);
	}
}
