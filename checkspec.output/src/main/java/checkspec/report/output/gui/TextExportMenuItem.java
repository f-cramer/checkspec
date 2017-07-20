package checkspec.report.output.gui;

import java.io.File;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import checkspec.report.output.Outputter;
import checkspec.report.output.text.TextOutputter;

class TextExportMenuItem extends AbstractExportMenuItem {

	private static final long serialVersionUID = -3089459732868339613L;

	public TextExportMenuItem(final CheckSpecFrame parent) {
		super(parent, "As Text", 'T', "export_text");
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
			return Paths.get(path.toString() + CheckSpecFrame.TEXT_EXTENSION);
		}
	}

	private Optional<Throwable> exportText(Path path) {
		try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
			Outputter outputter = new TextOutputter(writer, false);
			outputter.output(parent.getReport());
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
				return CheckSpecFrame.TEXT_DESCRIPTION;
			}

			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(CheckSpecFrame.TEXT_EXTENSION) || file.isDirectory();
			}
		});

		fileChooser.setAcceptAllFileFilterUsed(false);

		fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), "out.txt"));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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