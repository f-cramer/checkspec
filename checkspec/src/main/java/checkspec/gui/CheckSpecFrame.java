package checkspec.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import checkspec.report.ClassReport;
import checkspec.report.Report;
import checkspec.report.ReportEntry;
import checkspec.report.ReportProblem;
import checkspec.report.SpecReport;
import lombok.RequiredArgsConstructor;

public class CheckSpecFrame extends JFrame {

	private static final long serialVersionUID = -954917589134719758L;

	public CheckSpecFrame(SpecReport report) {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());

		setTitle(report.toString());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;

		add(new JScrollPane(getErrorReportTreeView(report)), constraints);
//		add(getErrorReportTreeView(report), constraints);

		pack();
		setLocationRelativeTo(null);
	}

	private Component getErrorReportTreeView(SpecReport report) {
		MutableTreeNode rootNode = createNode(report);
		JTree tree = new JTree(rootNode);
		// tree.setRootVisible(false);
		tree.setCellRenderer(new CustomTreeCellRenderer(tree.getCellRenderer()));
		expandAllNodes(tree);

		return tree;
	}

	private MutableTreeNode createNode(SpecReport report) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(report);

		for (ClassReport cr : report.getClassReports()) {
			node.add(createNode(cr));
		}

		return node;
	}

	private MutableTreeNode createNode(Report<?> report) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(report);

		for (ReportProblem e : report.getLines()) {
			node.add(createNode(e));
		}

		for (Report<?> e : report.getSubReports()) {
			node.add(createNode(e));
		}
		return node;
	}

	private MutableTreeNode createNode(ReportProblem line) {
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

	@RequiredArgsConstructor
	private static class CustomTreeCellRenderer implements TreeCellRenderer {

		private final TreeCellRenderer delegate;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			Component component = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			if (component instanceof JLabel) {
				JLabel label = (JLabel) component;
				label.setFont(label.getFont().deriveFont(24.0f));

				String iconName = null;
				Object userObject = node.getUserObject();

				if (userObject instanceof SpecReport) {
					iconName = "report";
				} else if (userObject instanceof ReportEntry) {
					if (userObject instanceof Report<?> && ((Report<?>) userObject).getImplementingObject() == null) {
						iconName = "failure";
					} else if (((ReportEntry) userObject).isSuccess()) {
						iconName = "success";
					} else {
						iconName = "exclamation_mark";
					}
				}

				if (iconName != null) {
					label.setIcon(new ImageIcon(CheckSpecFrame.class.getResource(String.format("%s.png", iconName))));
				}

			}

			return component;
		}
	}
}
