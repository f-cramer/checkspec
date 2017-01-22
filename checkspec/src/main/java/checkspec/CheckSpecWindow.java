package checkspec;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import checkspec.report.ErrorReport;
import checkspec.report.ErrorReportEntry;
import checkspec.report.ErrorReportLine;

public class CheckSpecWindow extends JFrame {

	private static final long serialVersionUID = -954917589134719758L;

	public CheckSpecWindow(ErrorReport report) {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;

		add(getErrorReportTreeView(report), constraints);

		pack();
	}

	private Component getErrorReportTreeView(ErrorReport report) {
		MutableTreeNode rootNode = create(report);
		JTree tree = new JTree(rootNode);

		return tree;
	}

	private MutableTreeNode create(ErrorReportEntry entry) {
		if (entry instanceof ErrorReport) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(((ErrorReport) entry).getTitle());

			for (ErrorReportEntry e : ((ErrorReport) entry).getEntries()) {
				node.add(create(e));
			}
			return node;
		} else if (entry instanceof ErrorReportLine) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(((ErrorReportLine) entry).getContent(), false);
			return node;
		} else {
			throw new IllegalArgumentException(entry.getClass().toString());
		}
	}
}
