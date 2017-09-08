package checkspec.report.output.gui;

import checkspec.report.SpecReport;
import checkspec.report.output.Outputter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GuiOutputter implements Outputter {

	private final CheckSpecFrame frame = new CheckSpecFrame();

	@Override
	public void output(SpecReport report) {
		frame.addReport(report);
	}

	@Override
	public void finished() {
		frame.finishedAddingReports();
		frame.setVisible(true);
	}
}
