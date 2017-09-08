package checkspec.report.output;

import checkspec.report.SpecReport;

public interface Outputter {

	void output(SpecReport report) throws OutputException;

	default void finished() {
	}
}
