package checkspec.report.output.gui;

/*-
 * #%L
 * CheckSpec Output
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



import java.nio.file.Path;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import checkspec.report.output.Outputter;
import checkspec.report.output.html.HtmlOutputter;

/**
 * Menu item used to export a {@link checkspec.report.SpecReport SpecReport} to
 * HTML.
 *
 * @author Florian Cramer
 *
 */
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
