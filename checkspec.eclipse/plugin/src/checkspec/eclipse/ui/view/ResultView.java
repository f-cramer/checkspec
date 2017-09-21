package checkspec.eclipse.ui.view;

/*-
 * #%L
 * checkspec.eclipse.plugin
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



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

	public static final String VIEW_ID = Constants.PLUGIN_ID + ".ui.resultView";
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
		report.getProblems().forEach(problem -> addProblem(problem, item));
		report.getSubReports().forEach(subReport -> addReport(subReport, item));
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
