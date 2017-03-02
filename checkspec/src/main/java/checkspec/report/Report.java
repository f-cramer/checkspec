package checkspec.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Report<T> implements Comparable<Report<?>>, ReportEntry {

	private final T specObject;
	private final T implementingObject;

	private final String title;
	private final List<ReportLine> lines = new ArrayList<>();

	protected Report(T specObject, T implementingObject, String title, ReportLine... lines) {
		this.specObject = specObject;
		this.implementingObject = implementingObject;
		this.title = title;
		this.lines.addAll(Arrays.asList(lines));
	}

	public List<ReportLine> getLines() {
		return Collections.unmodifiableList(lines);
	}

	public List<Report<?>> getSubReports() {
		return Collections.emptyList();
	}

	public void add(@Nonnull ReportLine entry) {
		lines.add(entry);
	}

	public void addEntries(@Nonnull Report<?> report) {
		report.getSubReports().parallelStream().forEachOrdered(this::addSubReport);
		lines.addAll(report.getLines());
	}

	public void addError(@Nonnull String content) {
		addError(1, content);
	}

	public void addError(int score, @Nonnull String content) {
		lines.add(new ReportLine(score, content));
	}

	@Override
	public int getScore() {
		int linesSum = lines.parallelStream().mapToInt(ReportLine::getScore).sum();
		int subReportsSum = getSubReports().parallelStream().mapToInt(Report::getScore).sum();
		
		return linesSum + subReportsSum;
	}

	protected void addSubReport(Report<?> subReport) {
		throw new UnsupportedOperationException();
	}

	public static Report<?> success() {
		return success("");
	}

	public static Report<?> success(@Nonnull String title) {
		return new Report<>(null, null, title);
	}

	public static Report<?> error(@Nonnull String report) {
		return error("", report);
	}

	public static Report<?> error(@Nonnull String title, @Nonnull String report) {
		return error(title, 1, report);
	}

	public static Report<?> error(int score, @Nonnull String report) {
		return error("", score, report);
	}

	public static Report<?> error(@Nonnull String title, int score, @Nonnull String report) {
		return new Report<>(null, null, title, new ReportLine(score, report));
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
