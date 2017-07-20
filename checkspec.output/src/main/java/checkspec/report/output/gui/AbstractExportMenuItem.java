package checkspec.report.output.gui;

import java.awt.event.ActionEvent;
import java.util.Optional;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import lombok.NonNull;

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
		Optional<Throwable> export = export();
		if (export != null) {
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
	protected abstract Optional<Throwable> export();
}