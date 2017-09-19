package checkspec.report.output.gui;

import java.io.File;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import checkspec.report.output.Outputter;
import checkspec.report.output.text.TextOutputter;

class TextExportMenuItem extends AbstractExportMenuItem {

	private static final long serialVersionUID = -3089459732868339613L;

	private static final String TEXT = "txt";
	private static final String FILE_EXTENSION = ".%s";
	private static final String FILE_DESCRIPTION = ".%s Files";

	private static final String TEXT_EXTENSION = String.format(FILE_EXTENSION, TEXT);
	private static final String TEXT_DESCRIPTION = String.format(FILE_DESCRIPTION, TEXT);

	public TextExportMenuItem(final CheckSpecFrame parent) {
		super(parent, "As Text", 'T', "export_text");
	}

	@Override
	protected ExportResult<Throwable> export() {
		ExportResult<Path> file = selectTextFile();
		return file.map(this::addTextExtensionIfNecessary).flatMap(this::exportText);
	}

	private Path addTextExtensionIfNecessary(Path path) {
		if (path.getFileName().toString().contains(".")) {
			return path;
		} else {
			return Paths.get(path.toString() + TEXT_EXTENSION);
		}
	}

	private ExportResult<Throwable> exportText(Path path) {
		try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
			Outputter outputter = new TextOutputter(writer, false);
			outputter.output(parent.getCurrentReport());
			return ExportResult.error();
		} catch (Exception e) {
			return ExportResult.of(e);
		}
	}

	private ExportResult<Path> selectTextFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return TEXT_DESCRIPTION;
			}

			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(TEXT_EXTENSION) || file.isDirectory();
			}
		});

		fileChooser.setAcceptAllFileFilterUsed(false);

		fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), "out.txt"));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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