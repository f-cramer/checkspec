package checkspec.report.output.gui;

import java.nio.file.Path;

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
	protected ExportResult<Throwable> export() {
		ExportResult<Path> directory = selectHtmlDirectory();
		return directory.flatMap(this::exportHtml);
	}

	private ExportResult<Throwable> exportHtml(Path path) {
		try {
			Outputter outputter = new HtmlOutputter(path);
			outputter.output(parent.getCurrentReport());
			return ExportResult.error();
		} catch (Exception e) {
			return ExportResult.of(e);
		}
	}

	private ExportResult<Path> selectHtmlDirectory() {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		int option = fileChooser.showSaveDialog(parent);

		switch (option) {
		case JFileChooser.APPROVE_OPTION:
			return ExportResult.of(fileChooser.getSelectedFile() == null ? null : fileChooser.getSelectedFile().toPath());
		case JFileChooser.ERROR_OPTION:
			JOptionPane.showMessageDialog(this, String.format(CheckSpecFrame.ERROR, "").trim());
			return ExportResult.error();
		}

		return ExportResult.noExport();
	}
}