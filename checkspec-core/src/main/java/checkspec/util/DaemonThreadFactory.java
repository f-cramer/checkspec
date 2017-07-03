package checkspec.util;

import java.util.concurrent.ThreadFactory;

final class DaemonThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		return thread;
	}
}
