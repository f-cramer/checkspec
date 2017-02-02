package checkspec.report.output;

import checkspec.report.ClassReport;
import checkspec.report.Report;
import checkspec.report.ReportLine;
import checkspec.report.SpecReport;

public class ConsoleOutputter implements Outputter {

	@Override
	public void output(SpecReport report) {
		System.out.println(toString(report));
	}

	private String toString(Report<?> report) {
		StringBuilder builder = new StringBuilder(report.toString());
		report.getLines().parallelStream().map(ReportLine::toString).forEachOrdered(e -> builder.append("\n").append(e));
		return builder.toString().replace("\n", "\n\t");
	}

	private String toString(ClassReport report) {
		StringBuilder builder = new StringBuilder(report.toString());
		report.getLines().parallelStream().map(ReportLine::toString).forEachOrdered(e -> builder.append("\n").append(e));
		report.getFieldReports().parallelStream().map(this::toString).forEachOrdered(e -> builder.append("\n").append(e));
		report.getConstructorReports().parallelStream().map(this::toString).forEachOrdered(e -> builder.append("\n").append(e));
		report.getMethodReports().parallelStream().map(this::toString).forEachOrdered(e -> builder.append("\n").append(e));
		return builder.toString().replace("\n", "\n\t");
	}

	private String toString(SpecReport report) {
		StringBuilder builder = new StringBuilder(report.toString());
		report.getClassReports().parallelStream().map(this::toString).forEachOrdered(e -> builder.append("\n").append(e));
		return builder.toString().replace("\n", "\n\t");
	}
}
