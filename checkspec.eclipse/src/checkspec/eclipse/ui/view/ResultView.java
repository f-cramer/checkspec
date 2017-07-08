package checkspec.eclipse.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import checkspec.eclipse.CheckSpecPlugin;
import checkspec.eclipse.Constants;
import checkspec.report.ClassReport;
import checkspec.report.Report;
import checkspec.report.ReportProblem;
import checkspec.report.ReportType;
import checkspec.report.SpecReport;

public class ResultView extends ViewPart {

	public static final String VIEW_ID = Constants.CHECKSPEC_PREFIX + ".ui.resultView";
	private Tree tree;
	private SpecReport[] reports;

	@Override
	public void createPartControl(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		parent.setLayout(gridLayout);

		tree = new Tree(parent, SWT.SINGLE);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		draw();
	}

	@Override
	public void setFocus() {
		if (tree != null) {
			tree.setFocus();
		}
	}

	public void setReports(SpecReport[] reports) {
		this.reports = reports;
		draw();
	}

	private void draw() {
		if (tree != null && reports != null) {
			tree.setRedraw(false);
			tree.clearAll(true);
			tree.removeAll();

			for (SpecReport report : reports) {
				TreeItem item = new TreeItem(tree, 0);
				item.setText(report.toString());
				item.setImage(CheckSpecPlugin.getImage("report.png"));

				for (ClassReport classReport : report.getClassReports()) {
					addReport(classReport, item);
				}
			}
			expand(tree.getItems());
			tree.setRedraw(true);
		}
	}

	private void addReport(Report<?, ?> report, TreeItem parent) {
		TreeItem item = new TreeItem(parent, 0);
		item.setText(report.toString());
		item.setImage(CheckSpecPlugin.getImage(getImageName(report)));
		report.getSubReports().forEach(subReport -> addReport(subReport, item));
		report.getProblems().forEach(problem -> addProblem(problem, item));
	}

	private void addProblem(ReportProblem problem, TreeItem parent) {
		TreeItem item = new TreeItem(parent, 0);
		item.setText(problem.toString());
		item.setImage(CheckSpecPlugin.getImage(getImageName(problem)));
	}

	private void expand(TreeItem[] items) {
		for (TreeItem item : items) {
			item.setExpanded(true);
			expand(item.getItems());
		}
	}

	private String getImageName(Report<?, ?> report) {
		return getImageName(report.getType());
	}

	private String getImageName(ReportProblem problem) {
		return getImageName(problem.getType().toReportType());
	}

	private String getImageName(ReportType type) {
		switch (type) {
		case ERROR:
			return "failure.png";
		case SUCCESS:
			return "success.png";
		case WARNING:
			return "warning.png";
		}
		throw new IllegalArgumentException();
	}
}
