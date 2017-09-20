package checkspec.analysis;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.report.ReportProblem;
import checkspec.report.ReportProblemType;
import checkspec.specification.ExecutableSpecification;
import checkspec.type.MatchableType;
import checkspec.util.ClassUtils;

public class ExceptionsAnalysis implements Analysis<Executable, ExecutableSpecification<? extends Executable>, List<ReportProblem>, MultiValuedMap<Class<?>, Class<?>>> {

	private static final String SHOULD = "should throw throwable of type \"%s\"";
	private static final String SHOULD_NOT = "should not throw thowable of type \"%s\"";

	@Override
	public List<ReportProblem> analyze(Executable executable, ExecutableSpecification<? extends Executable> spec, MultiValuedMap<Class<?>, Class<?>> oldReports) {
		List<ReportProblem> problems = new ArrayList<>();

		List<MatchableType> notFoundThrowables = Arrays.stream(executable.getGenericExceptionTypes())
				.map(MatchableType::forType)
				// needs to be mutable
				.collect(Collectors.toCollection(ArrayList::new));
		MatchableType[] specifications = Arrays.stream(spec.getExceptions())
				.map(ExceptionSpecification::getRawElement)
				.toArray(MatchableType[]::new);

		for (MatchableType specification : specifications) {
			Optional<MatchableType> interf = notFoundThrowables.parallelStream()
					.filter(i -> specification.matches(i, oldReports).evaluate(true, true, false))
					.findAny();

			if (interf.isPresent()) {
				notFoundThrowables.remove(interf.get());
			} else {
				problems.add(new ReportProblem(15, String.format(SHOULD, ClassUtils.getName(specification)), ReportProblemType.ERROR));
			}
		}

		for (MatchableType notFoundInterface : notFoundThrowables) {
			problems.add(new ReportProblem(15, String.format(SHOULD_NOT, ClassUtils.getName(notFoundInterface)), ReportProblemType.ERROR));
		}

		return problems;
	}
}
