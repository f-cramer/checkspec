package checkspec.report.output.gui;

import java.nio.file.Path;
import java.util.Optional;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import checkspec.report.output.Outputter;
import checkspec.report.output.html.HtmlOutputter;

class HtmlExportMenuItem extends AbstractExportMenuItem {

	private static final long serialVersionUID = -3089459732868339613L;

	public HtmlExportMenuItem(final CheckSpecFrame parent) {
		super(parent, "As HTML", 'H', "export_html");
	}

	@Override
	protected Optional<Throwable> export() {
		Optional<Path> directory = selectHtmlDirectory();
		return directory == null ? null : directory.flatMap(this::exportHtml);
	}

	private Optional<Throwable> exportHtml(Path path) {
		try {
			Outputter outputter = new HtmlOutputter(path);
			outputter.output(parent.getReport());
			return Optional.empty();
		} catch (Exception e) {
			return Optional.of(e);
		}
	}

	private Optional<Path> selectHtmlDirectory() {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		int option = fileChooser.showSaveDialog(parent);

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