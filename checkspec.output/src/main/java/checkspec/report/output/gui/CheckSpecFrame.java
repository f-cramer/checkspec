package checkspec.report.output.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import checkspec.report.ClassReport;
import checkspec.report.Report;
import checkspec.report.ReportProblem;
import checkspec.report.ReportType;
import checkspec.report.SpecReport;
import checkspec.util.ClassUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

class CheckSpecFrame extends JFrame {

	private static final long serialVersionUID = -954917589134719758L;

	private static final String ICON_FORMAT = "%s.png";

	static final String SUCCESS = "Export was successful";
	static final String ERROR = "An error occurred while exporting:%n%s";

	private final JMenuBar menuBar = new JMenuBar();
	private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
	private final JTree tree = new JTree(rootNode);

	private final JMenuItem textExport;
	private final JMenuItem htmlExport;
	private final TreeSelectionListener treeSelectionListener;
	private final JPopupMenu contextMenu;

	@Getter(AccessLevel.PACKAGE)
	private final List<SpecReport> reports = new LinkedList<>();

	public CheckSpecFrame() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new GridBagLayout());

		setTitle("CheckSpec");

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;

		treeSelectionListener = new CustomTreeSelectionListener();
		add(new JScrollPane(getReportTreeView()), constraints);

		pack();
		setLocationRelativeTo(null);

		JMenu exportMenu = new JMenu("Export");
		exportMenu.setMnemonic('E');
		exportMenu.add(textExport = new TextExportMenuItem(this));
		exportMenu.add(htmlExport = new HtmlExportMenuItem(this));
		treeSelectionListener.valueChanged(null);

		menuBar.add(exportMenu);
		setJMenuBar(menuBar);

		contextMenu = createContextMenu();
	}

	private JTree getReportTreeView() {
		tree.setCellRenderer(new CustomTreeCellRenderer(tree.getCellRenderer()));
		tree.addTreeSelectionListener(treeSelectionListener);
		tree.addMouseListener(new CustomMouseListener());
		tree.setRootVisible(false);
		ToolTipManager.sharedInstance().registerComponent(tree);
		expandAllNodes(tree);

		return tree;
	}

	public void addReport(SpecReport report) {
		reports.add(report);
		MutableTreeNode node = createNode(report);
		rootNode.add(node);
	}

	public void finishedAddingReports() {
		((DefaultTreeModel) tree.getModel()).reload();
		expandAllNodes(tree);
		pack();
	}

	public SpecReport getCurrentReport() {
		TreePath selectionPath = tree.getSelectionPath();

		if (selectionPath != null && selectionPath.getPathCount() > 1) {
			DefaultMutableTreeNode reportNode = (DefaultMutableTreeNode) selectionPath.getPathComponent(1);
			return (SpecReport) reportNode.getUserObject();
		}
		return null;
	}

	private static MutableTreeNode createNode(SpecReport report) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(report);

		for (Report<?, ?> cr : report.getClassReports()) {
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

	private JPopupMenu createContextMenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem removeItem = new JMenuItem("remove");
		removeItem.setIcon(new ImageIcon(CheckSpecFrame.class.getResource("remove.png")));
		removeItem.addActionListener(this::removeItem);

		menu.add(removeItem);
		return menu;
	}

	private void handleContextMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			if (path != null && path.getPathCount() > 1) {
				tree.setSelectionPaths(new TreePath[] { path });
				contextMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private void removeItem(ActionEvent e) {
		TreePath path = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		Object object = node.getUserObject();

		boolean remove = true;

		if (object instanceof SpecReport) {
			reports.remove(object);
		} else if (object instanceof Report<?, ?>) {
			DefaultMutableTreeNode parentComponent = (DefaultMutableTreeNode) path.getPathComponent(path.getPathCount() - 2);
			Object parentReport = parentComponent.getUserObject();
			if (parentReport instanceof SpecReport) {
				((SpecReport) parentReport).removeClassReport((ClassReport) object);
			} else if (parentReport instanceof Report<?, ?>) {
				((Report<?, ?>) parentReport).removeSubReport((Report<?, ?>) object);
			}
		} else if (object instanceof ReportProblem) {
			DefaultMutableTreeNode secondToLastComponent = (DefaultMutableTreeNode) path.getPathComponent(path.getPathCount() - 2);
			Report<?, ?> parentReport = (Report<?, ?>) secondToLastComponent.getUserObject();
			parentReport.removeProblem((ReportProblem) object);
		} else {
			remove = false;
		}

		if (remove) {
			TreePath rootPath = getRootPath(path);
			List<TreePath> expandedDescendants = Collections.list(tree.getExpandedDescendants(rootPath));
			node.removeFromParent();
			((DefaultTreeModel) tree.getModel()).reload();
			expandedDescendants.forEach(tree::expandPath);
		}
	}

	private TreePath getRootPath(TreePath path) {
		TreePath parent = path.getParentPath();
		return parent == null ? path : getRootPath(parent);
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
				String tooltip = null;
				Object userObject = node.getUserObject();

				if (userObject instanceof SpecReport) {
					iconName = "report";
					URL location = ClassUtils.getLocation(((SpecReport) userObject).getSpecification().getRawElement().getRawClass());
					tooltip = location == null ? null : location.toString();
				} else {
					ReportType type = null;
					if (userObject instanceof ReportProblem) {
						type = ((ReportProblem) userObject).getType().toReportType();
					} else if (userObject instanceof Report<?, ?>) {
						type = ((Report<?, ?>) userObject).getType();
					}

					if (userObject instanceof ClassReport) {
						URL location = ClassUtils.getLocation(((ClassReport) userObject).getImplementation().getRawClass());
						tooltip = location == null ? null : location.toString();
					}
					iconName = type == null ? null : getIconName(type);
				}

				if (iconName != null) {
					label.setIcon(getIcon(iconName));
				} else {
					label.setIcon(null);
				}
				label.setToolTipText(tooltip);
			}

			return component;
		}
	}

	private class CustomTreeSelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			boolean enabled = getCurrentReport() != null;
			textExport.setEnabled(enabled);
			htmlExport.setEnabled(enabled);
		}
	}

	private class CustomMouseListener extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			handleContextMenu(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			handleContextMenu(e);
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
