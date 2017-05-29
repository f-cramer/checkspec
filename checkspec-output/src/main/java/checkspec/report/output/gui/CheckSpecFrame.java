package checkspec.report.output.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import checkspec.report.ClassReport;
import checkspec.report.ProblemType;
import checkspec.report.Report;
import checkspec.report.ReportProblem;
import checkspec.report.SpecReport;
import checkspec.report.output.Outputter;
import checkspec.report.output.html.HtmlOutputter;
import checkspec.report.output.text.TextOutputter;
import lombok.RequiredArgsConstructor;

class CheckSpecFrame extends JFrame {

	private static final long serialVersionUID = -954917589134719758L;

	private static final String ICON_FORMAT = "%s.png";
	private static final String FILE_EXTENSION = ".%s";
	private static final String FILE_DESCRIPTION = ".%s Files";

	private static final String SUCCESS = "Export was successful";
	private static final String ERROR = "An error occurred while exporting:\n%s";

	private static final String TEXT = "txt";
	private static final String TEXT_EXTENSION = String.format(FILE_EXTENSION, TEXT);
	private static final String TEXT_DESCRIPTION = String.format(FILE_DESCRIPTION, TEXT);

	private static final JMenuBar menuBar = new JMenuBar();

	private final SpecReport report;

	public CheckSpecFrame(SpecReport report) {
		this.report = report;
		init();
	}

	private void init() {
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

		add(new JScrollPane(getReportTreeView(report)), constraints);

		pack();
		setLocationRelativeTo(null);

		JMenu exportMenu = new JMenu("Export");
		exportMenu.setMnemonic('E');
		exportMenu.add(new TextExportMenuItem());
		exportMenu.add(new HtmlExportMenuItem());

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

	private MutableTreeNode createNode(SpecReport report) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(report);

		for (ClassReport cr : report.getClassReports()) {
			node.add(createNode(cr));
		}

		return node;
	}

	private MutableTreeNode createNode(Report<?, ?> report) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(report);

		for (ReportProblem e : report.getProblems()) {
			node.add(createNode(e));
		}

		for (Report<?, ?> e : report.getSubReports()) {
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
					ProblemType type = null;
					if (userObject instanceof ReportProblem) {
						type = ((ReportProblem) userObject).getType().toProblemType();
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
	
	private String getIconName(@Nonnull ProblemType type) {
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

	private abstract class AbstractExportMenuItem extends JMenuItem {

		private static final long serialVersionUID = 8386968626698622266L;

		public AbstractExportMenuItem(String text, char mnemonic, String iconName) {
			setText(text);
			setMnemonic(mnemonic);
			setIcon(CheckSpecFrame.getIcon(iconName));
			addActionListener(this::export);
		}

		private void export(ActionEvent event) {
			Optional<Throwable> export = export();
			if (export != null) {
				String message = export.map(e -> String.format(CheckSpecFrame.ERROR, e.getMessage())).orElse(CheckSpecFrame.SUCCESS);
				JOptionPane.showMessageDialog(CheckSpecFrame.this, message.trim());
			}
		}

		/**
		 *
		 * @return
		 *         <ul>
		 *         <li>{@code null} - if file selection was aborted</li>
		 *         <li>Optional with value - if an exception occurred while
		 *         exporting</li>
		 *         <li>Optional without value - if export was successful</li>
		 *         </ul>
		 */
		protected abstract Optional<Throwable> export();
	}

	private class TextExportMenuItem extends AbstractExportMenuItem {

		private static final long serialVersionUID = -3089459732868339613L;

		public TextExportMenuItem() {
			super("As Text", 'T', "export_text");
		}

		@Override
		protected Optional<Throwable> export() {
			Optional<Path> file = selectTextFile();
			return file == null ? null : file.map(this::addTextExtensionIfNecessary).flatMap(this::exportText);
		}

		private Path addTextExtensionIfNecessary(Path path) {
			if (path.getFileName().toString().contains(".")) {
				return path;
			} else {
				return Paths.get(path.toString() + TEXT_EXTENSION);
			}
		}

		private Optional<Throwable> exportText(Path path) {
			try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
				Outputter outputter = new TextOutputter(writer);
				outputter.output(report);
			} catch (Exception e) {
				return Optional.of(e);
			}

			return Optional.empty();
		}

		private Optional<Path> selectTextFile() {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.addChoosableFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return TEXT_DESCRIPTION;
				}

				@Override
				public boolean accept(File f) {
					return f.getName().endsWith(TEXT_EXTENSION) || f.isDirectory();
				}
			});

			fileChooser.setAcceptAllFileFilterUsed(false);

			fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), "out.txt"));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);
			int option = fileChooser.showSaveDialog(CheckSpecFrame.this);

			switch (option) {
			case JFileChooser.APPROVE_OPTION:
				return Optional.ofNullable(fileChooser.getSelectedFile() == null ? null : fileChooser.getSelectedFile().toPath());
			case JFileChooser.ERROR_OPTION:
				JOptionPane.showMessageDialog(this, String.format(CheckSpecFrame.ERROR).trim());
				return Optional.empty();
			}

			return null;
		}
	}

	public class HtmlExportMenuItem extends AbstractExportMenuItem {

		private static final long serialVersionUID = -3089459732868339613L;

		public HtmlExportMenuItem() {
			super("As HTML", 'H', "export_html");
		}

		@Override
		protected Optional<Throwable> export() {
			Optional<Path> directory = selectHtmlDirectory();
			return directory == null ? null : directory.flatMap(this::exportHtml);
		}

		private Optional<Throwable> exportHtml(Path path) {
			try {
				Outputter outputter = new HtmlOutputter(path);
				outputter.output(report);
			} catch (Exception e) {
				return Optional.of(e);
			}

			return Optional.empty();
		}

		private Optional<Path> selectHtmlDirectory() {
			JFileChooser fileChooser = new JFileChooser();

			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);
			int option = fileChooser.showSaveDialog(CheckSpecFrame.this);

			switch (option) {
			case JFileChooser.APPROVE_OPTION:
				return Optional.ofNullable(fileChooser.getSelectedFile() == null ? null : fileChooser.getSelectedFile().toPath());
			case JFileChooser.ERROR_OPTION:
				JOptionPane.showMessageDialog(this, String.format(CheckSpecFrame.ERROR).trim());
				return Optional.empty();
			}

			return null;
		}
	}
}
