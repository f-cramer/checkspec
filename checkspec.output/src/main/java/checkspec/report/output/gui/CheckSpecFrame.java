package checkspec.report.output.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import checkspec.report.ClassReport;
import checkspec.report.Report;
import checkspec.report.ReportProblem;
import checkspec.report.ReportType;
import checkspec.report.SpecReport;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

class CheckSpecFrame extends JFrame {

	private static final long serialVersionUID = -954917589134719758L;

	private static final String ICON_FORMAT = "%s.png";
	private static final String FILE_EXTENSION = ".%s";
	private static final String FILE_DESCRIPTION = ".%s Files";

	static final String SUCCESS = "Export was successful";
	static final String ERROR = "An error occurred while exporting:%n%s";

	private static final String TEXT = "txt";
	static final String TEXT_EXTENSION = String.format(FILE_EXTENSION, TEXT);
	static final String TEXT_DESCRIPTION = String.format(FILE_DESCRIPTION, TEXT);

	private static final JMenuBar menuBar = new JMenuBar();

	@Getter(AccessLevel.PACKAGE)
	private final SpecReport report;

	public CheckSpecFrame(SpecReport report) {
		this.report = report;
		init();
	}

	private void init() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new GridBagLayout());

		setTitle(report.toString());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;

		add(new JScrollPane(getReportTreeView(report)), constraints);

		pack();
		setLocationRelativeTo(null);

		JMenu exportMenu = new JMenu("Export");
		exportMenu.setMnemonic('E');
		exportMenu.add(new TextExportMenuItem(this));
		exportMenu.add(new HtmlExportMenuItem(this));

		menuBar.add(exportMenu);
		setJMenuBar(menuBar);

	}

	private Component getReportTreeView(SpecReport report) {
		MutableTreeNode rootNode = createNode(report);
		JTree tree = new JTree(rootNode);
		// tree.setRootVisible(false);
		tree.setCellRenderer(new CustomTreeCellRenderer(tree.getCellRenderer()));
		expandAllNodes(tree);

		return tree;
	}

	private static MutableTreeNode createNode(SpecReport report) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(report);

		for (ClassReport cr : report.getClassReports()) {
			node.add(createNode(cr));
		}

		return node;
	}

	private static MutableTreeNode createNode(Report<?, ?> report) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(report);

		for (ReportProblem e : report.getProblems()) {
			node.add(createNode(e));
		}

		for (Report<?, ?> e : report.getSubReports()) {
			node.add(createNode(e));
		}
		return node;
	}

	private static MutableTreeNode createNode(ReportProblem line) {
		return new DefaultMutableTreeNode(line, false);
	}

	private static void expandAllNodes(JTree tree) {
		expandAllNodes(tree, 0, tree.getRowCount());
	}

	private static void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
		for (int i = startingIndex; i < rowCount; ++i) {
			tree.expandRow(i);
		}

		if (tree.getRowCount() != rowCount) {
			expandAllNodes(tree, rowCount, tree.getRowCount());
		}
	}

	public static Icon getIcon(String name) {
		return new ImageIcon(CheckSpecFrame.class.getResource(String.format(ICON_FORMAT, name)));
	}

	@RequiredArgsConstructor
	private class CustomTreeCellRenderer implements TreeCellRenderer {

		private final TreeCellRenderer delegate;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			Component component = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			if (component instanceof JLabel) {
				JLabel label = (JLabel) component;

				String iconName = null;
				Object userObject = node.getUserObject();

				if (userObject instanceof SpecReport) {
					iconName = "report";
				} else {
					ReportType type = null;
					if (userObject instanceof ReportProblem) {
						type = ((ReportProblem) userObject).getType().toReportType();
					} else if (userObject instanceof Report) {
						type = ((Report<?, ?>) userObject).getType();
					}

					iconName = type == null ? null : getIconName(type);
				}

				if (iconName != null) {
					label.setIcon(getIcon(iconName));
				}

			}

			return component;
		}
	}

	private String getIconName(@NonNull ReportType type) {
		switch (type) {
		case SUCCESS:
			return "success";
		case WARNING:
			return "exclamation_mark";
		case ERROR:
			return "failure";
		}

		return null;
	}
}
