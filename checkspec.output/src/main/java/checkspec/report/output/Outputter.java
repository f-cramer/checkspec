package checkspec.report.output;

import checkspec.report.SpecReport;

public interface Outputter {

	public static final Outputter NULL_OUTPUTTER = specReport -> {
	};

	void output(SpecReport report) throws OutputException;
}
