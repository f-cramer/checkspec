package checkspec.report.output.gui;

import checkspec.report.SpecReport;
import checkspec.report.output.Outputter;

public class GuiOutputter implements Outputter {

	@Override
	public void output(SpecReport report) {
		CheckSpecFrame frame = new CheckSpecFrame(report);
		frame.setVisible(true);
	}
}
