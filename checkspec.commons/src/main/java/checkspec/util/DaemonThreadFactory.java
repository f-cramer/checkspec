package checkspec.util;

import java.util.concurrent.ThreadFactory;

/**
 * A {@link ThreadFactory} that creates a new daemon thread for each runnable.
 *
 * @author Florian Cramer
 *
 */
final class DaemonThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		return thread;
	}
}
