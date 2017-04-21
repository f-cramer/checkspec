package checkspec.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Report<T> implements Comparable<Report<?>>, ReportEntry {

	private final T specObject;
	private final T implementingObject;

	private final String title;
	private final List<ReportProblem> problems = new ArrayList<>();

	protected Report(T specObject, T implementingObject, String title, ReportProblem... problems) {
		this.specObject = specObject;
		this.implementingObject = implementingObject;
		this.title = title;
		this.problems.addAll(Arrays.asList(problems));
	}

	public List<ReportProblem> getLines() {
		return Collections.unmodifiableList(problems);
	}

	public List<Report<?>> getSubReports() {
		return Collections.emptyList();
	}

	public void addSubReports(@Nonnull List<? extends Report<?>> reports) {
		reports.forEach(this::addSubReport);
	}

	public void addProblem(@Nonnull ReportProblem entry) {
		problems.add(entry);
	}

	public void addProblems(@Nonnull Collection<ReportProblem> probs) {
		problems.addAll(probs);
	}

	public ProblemType getType() {
		Stream<ProblemType> reportTypes = getSubReports().parallelStream().map(Report::getType);
		Stream<ProblemType> problemTypes = problems.parallelStream().map(ReportProblem::getType).map(ReportProblem.Type::toProblemType);
		
		return Stream.concat(reportTypes, problemTypes).max(Comparator.naturalOrder()).orElse(ProblemType.SUCCESS);
	}

	@Override
	public int getScore() {
		int problemsSum = problems.parallelStream().mapToInt(ReportProblem::getScore).sum();
		int subReportsSum = getSubReports().parallelStream().mapToInt(Report::getScore).sum();

		return problemsSum + subReportsSum;
	}

	protected void addSubReport(Report<?> subReport) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		int score = getScore();
		return score == 0 ? getTitle() : String.format("%s [%d]", getTitle(), score);
	}

	@Override
	public int compareTo(Report<?> report) {
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
