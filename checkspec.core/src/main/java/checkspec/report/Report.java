package checkspec.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import checkspec.specification.Specification;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
public abstract class Report<RawType, SpecificationType extends Specification<RawType>> implements Comparable<Report<?, ?>> {

	private final SpecificationType spec;
	private final RawType implementation;
	private final String title;

	private final List<ReportProblem> problems = new ArrayList<>();

	protected Report(SpecificationType spec) {
		this(spec, null, null);
	}

	protected Report(SpecificationType spec, RawType implementation) {
		this(spec, implementation, null);
	}

	protected Report(@NonNull SpecificationType spec, RawType implementation, String title) {
		this.spec = spec;
		this.implementation = implementation;
		this.title = title;
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
		problems.parallelStream()
				.filter(Objects::nonNull)
				.forEachOrdered(this.problems::add);
	}

	public ReportType getType() {
		if (implementation == null) {
			return ReportType.ERROR;
		}

		Stream<ReportType> reportTypes = getSubReports().parallelStream().map(Report::getType);
		Stream<ReportType> problemTypes = problems.parallelStream().map(ReportProblem::getType).map(ReportProblemType::toReportType);

		return Stream.concat(reportTypes, problemTypes).parallel()
				.max(Comparator.naturalOrder())
				.orElse(ReportType.SUCCESS);
	}

	protected boolean isNameFitting() {
		return implementation == null ? false : spec.getName().equals(getRawTypeName(implementation));
	}

	public int getScore() {
		if (getImplementation() == null) {
			return 100;
		} else {
			int problemsSum = problems.parallelStream().mapToInt(ReportProblem::getScore).sum();
			int subReportsSum = getSubReports().parallelStream().mapToInt(Report::getScore).sum();

			return problemsSum + subReportsSum;
		}
	}

	@Override
	public String toString() {
		int score = getScore();
		return score == 0 ? String.valueOf(getTitle()) : String.format("%s [%d]", getTitle(), score);
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

	protected abstract String getRawTypeName(RawType rawType);
}
