package checkspec.report.output.gui;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FrameHolder {

	@Getter
	private static final FrameHolder instance = new FrameHolder();

	private final List<Window> windows = new LinkedList<>();

	private WindowListener listener = new WindowAdapter() {

		public void windowClosed(WindowEvent e) {
			shutdownIfAllWindowsClosed();
		}

		public void windowOpened(WindowEvent e) {
			shutdownIfAllWindowsClosed();
		}
	};

	private void shutdownIfAllWindowsClosed() {
		if (windows.parallelStream().noneMatch(Window::isVisible)) {
			System.exit(0);
		}
	}

	public void addWindow(Window window) {
		windows.add(window);
		window.addWindowListener(listener);
	}
}
