package checkspec.report;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import lombok.Getter;

@Getter
public class ErrorReport implements ErrorReportEntry, Comparable<ErrorReport> {

	private final String title;
	private final List<ErrorReportEntry> entries = new LinkedList<>();

	private ErrorReport(String title, ErrorReportEntry... lines) {
		this.title = title;
		this.entries.addAll(Arrays.asList(lines));
	}

	public boolean isSuccess() {
		return entries.isEmpty();
	}

	public List<ErrorReportEntry> getEntries() {
		return Collections.unmodifiableList(entries);
	}

	public void add(@Nonnull ErrorReportEntry entry) {
		entries.add(entry);
	}

	public void addEntries(@Nonnull ErrorReport report) {
		entries.addAll(report.getEntries());
	}

	public void addError(@Nonnull String content) {
		addError(1, content);
	}

	public void addError(int score, @Nonnull String content) {
		entries.add(new ErrorReportLine(score, content));
	}

	@Override
	public int getScore() {
		return entries.parallelStream().mapToInt(ErrorReportEntry::getScore).sum();
	}

	public static ErrorReport success() {
		return success("");
	}

	public static ErrorReport success(@Nonnull String title) {
		return new ErrorReport(title);
	}

	public static ErrorReport error(@Nonnull String report) {
		return error("", report);
	}

	public static ErrorReport error(@Nonnull String title, @Nonnull String report) {
		return error(title, 1, report);
	}

	public static ErrorReport error(int score, @Nonnull String report) {
		return error("", score, report);
	}

	public static ErrorReport error(@Nonnull String title, int score, @Nonnull String report) {
		return new ErrorReport(title, new ErrorReportLine(score, report));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(String.format("%s [%d]", title, getScore()));
		entries.parallelStream().filter(e -> e.getScore() > 0).map(Object::toString).forEachOrdered(e -> builder.append("\n").append(e));
		return builder.toString().replaceAll("\n", "\n\t");
	}

	@Override
	public int compareTo(ErrorReport report) {
		Objects.requireNonNull(report, "report");

		return Integer.compare(getScore(), report.getScore());
	}
}
