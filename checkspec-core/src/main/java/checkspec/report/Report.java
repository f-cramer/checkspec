package checkspec.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import checkspec.report.ReportProblem.Type;
import checkspec.spec.Specification;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
public class Report<T extends Specification<U>, U> implements Comparable<Report<?, ?>>, ReportEntry {

	private final T spec;
	private final U implementation;

	private final String title;
	private final List<ReportProblem> problems = new ArrayList<>();

	protected Report(T spec, U implementation, String title, ReportProblem... problems) {
		this.spec = spec;
		this.implementation = implementation;
		this.title = title;
		this.problems.addAll(Arrays.asList(problems));
	}

	public List<ReportProblem> getProblems() {
		return Collections.unmodifiableList(problems);
	}

	public List<Report<?, ?>> getSubReports() {
		return Collections.emptyList();
	}

	public void addProblem(@NonNull ReportProblem entry) {
		problems.add(entry);
	}

	public void addProblems(@NonNull Collection<ReportProblem> problems) {
		this.problems.addAll(problems);
	}

	public ProblemType getType() {
		Stream<ProblemType> reportTypes = getSubReports().parallelStream().map(Report::getType);
		Stream<ProblemType> problemTypes = problems.parallelStream().map(ReportProblem::getType).map(Type::toProblemType);

		return Stream.concat(reportTypes, problemTypes).parallel()
				.max(Comparator.naturalOrder())
				.orElseGet(() -> getImplementation() == null ? ProblemType.ERROR : ProblemType.SUCCESS);
	}

	@Override
	public int getScore() {
		if (getImplementation() == null) {
			return 10;
		} else {
			int problemsSum = problems.parallelStream().mapToInt(ReportProblem::getScore).sum();
			int subReportsSum = getSubReports().parallelStream().mapToInt(Report::getScore).sum();

			return problemsSum + subReportsSum;
		}
	}

	@Override
	public String toString() {
		int score = getScore();
		return score == 0 ? getTitle() : String.format("%s [%d]", getTitle(), score);
	}

	@Override
	public int compareTo(Report<?, ?> report) {
		if (report == null) {
			return 1;
		}

		int cmp = Integer.compare(getScore(), report.getScore());
		if (cmp != 0) {
			return cmp;
		}

		return title.compareToIgnoreCase(report.getTitle());
	}
}
