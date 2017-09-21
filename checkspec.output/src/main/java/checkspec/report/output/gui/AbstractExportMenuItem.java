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

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import lombok.NonNull;

/**
 * Represents a menu item that is used to export a
 * {@link checkspec.report.SpecReport SpecReport}.
 *
 * @author Florian Cramer
 *
 */
abstract class AbstractExportMenuItem extends JMenuItem {

	private static final long serialVersionUID = 8386968626698622266L;

	protected final CheckSpecFrame parent;

	public AbstractExportMenuItem(@NonNull final CheckSpecFrame parent, String text, char mnemonic, String iconName) {
		this.parent = parent;
		setText(text);
		setMnemonic(mnemonic);
		setIcon(CheckSpecFrame.getIcon(iconName));
		addActionListener(this::export);
	}

	private void export(ActionEvent event) {
		ExportResult<Throwable> export = export();
		if (!export.isNoExport()) {
			String message = export.map(e -> String.format(CheckSpecFrame.ERROR, e.getMessage())).orElse(CheckSpecFrame.SUCCESS);
			JOptionPane.showMessageDialog(parent, message.trim());
		}
	}

	/**
	 * Performs the export and returns any {@link Throwable} that was thrown.
	 *
	 * @return
	 *         <ul>
	 *         <li>{@code null} - if file selection was aborted</li>
	 *         <li>Optional with value - if an exception occurred while
	 *         exporting</li>
	 *         <li>Optional without value - if export was successful</li>
	 *         </ul>
	 */
	protected abstract ExportResult<Throwable> export();
}
