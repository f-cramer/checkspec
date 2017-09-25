package checkspec.eclipse.ui.view;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import checkspec.eclipse.CheckSpecPlugin;
import checkspec.eclipse.Constants;
import checkspec.eclipse.util.PathConverter;
import checkspec.report.ClassReport;
import checkspec.report.Report;
import checkspec.report.ReportProblem;
import checkspec.report.ReportType;
import checkspec.report.SpecReport;
import checkspec.util.ClassUtils;

public class ResultView extends ViewPart {

	public static final String VIEW_ID = Constants.PLUGIN_ID + ".ui.resultView";
	private static final String TOOLTIP = "tooltip";
	private static final String TREE_ITEM = "_TREEITEM";

	private Tree tree;
	private SpecReport[] reports;
	private IPath rootPath;

	private final Listener labelListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			Label label = (Label) event.widget;
			Shell shell = label.getShell();
			switch (event.type) {
			case SWT.MouseDown:
				Event e = new Event();
				e.item = (TreeItem) label.getData(TREE_ITEM);
				tree.setSelection(new TreeItem[] { (TreeItem) e.item });
				tree.notifyListeners(SWT.Selection, e);
			case SWT.MouseExit:
				shell.dispose();
				break;
			}
		}
	};

	@Override
	public void createPartControl(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		parent.setLayout(gridLayout);

		tree = new Tree(parent, SWT.SINGLE);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		CustomTableListener tableListener = new CustomTableListener(parent.getShell());
		tree.addListener(SWT.Dispose, tableListener);
		tree.addListener(SWT.KeyDown, tableListener);
		tree.addListener(SWT.MouseMove, tableListener);
		tree.addListener(SWT.MouseHover, tableListener);

		rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();

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

				Class<?> rawClass = report.getSpecification().getRawElement().getRawClass();
				item.setData(TOOLTIP, getPath(rawClass));

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
		item.setImage(CheckSpecPlugin.getImage(getImageName(report.getType())));
		if (report instanceof ClassReport) {
			Class<?> rawClass = ((ClassReport) report).getImplementation().getRawClass();
			item.setData(TOOLTIP, getPath(rawClass));
		}
		report.getProblems().forEach(problem -> addProblem(problem, item));
		report.getSubReports().forEach(subReport -> addReport(subReport, item));
	}

	private void addProblem(ReportProblem problem, TreeItem parent) {
		TreeItem item = new TreeItem(parent, 0);
		item.setText(problem.toString());
		item.setImage(CheckSpecPlugin.getImage(getImageName(problem.getType().toReportType())));
	}

	private void expand(TreeItem[] items) {
		for (TreeItem item : items) {
			item.setExpanded(true);
			expand(item.getItems());
		}
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

	private String getPath(Class<?> rawClass) {
		IPath path = PathConverter.fromUrl(ClassUtils.getLocation(rawClass));
		if (rootPath.isPrefixOf(path)) {
			path = path.makeRelativeTo(rootPath);
		}
		return path.toOSString();
	}

	private class CustomTableListener implements Listener {

		private Shell tip = null;
		private Label label = null;
		private final Shell shell;

		public CustomTableListener(Shell shell) {
			this.shell = shell;
		}

		@Override
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.Dispose:
			case SWT.KeyDown:
			case SWT.MouseMove:
				if (tip == null) {
					break;
				}
				tip.dispose();
				tip = null;
				label = null;
				break;
			case SWT.MouseHover:
				TreeItem item = tree.getItem(new Point(event.x, event.y));
				Object tooltip = item.getData(TOOLTIP);
				if (item != null && tooltip != null) {
					if (tip != null && !tip.isDisposed()) {
						tip.dispose();
					}
					tip = new Shell(shell, SWT.ON_TOP | SWT.TOOL);
					tip.setLayout(new FillLayout());
					label = new Label(tip, SWT.NONE);
					label.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
					label.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
					label.setData(TREE_ITEM, item);
					label.setText(tooltip.toString());
					label.addListener(SWT.MouseExit, labelListener);
					label.addListener(SWT.MouseDown, labelListener);
					Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					Point pt = tree.toDisplay(event.x + 1, event.y - size.y - 1);
					tip.setBounds(pt.x, pt.y, size.x, size.y);
					tip.setVisible(true);
				}
			}
		}
	};
}
