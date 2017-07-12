package checkspec.eclipse.util;

import java.util.function.Supplier;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;

import checkspec.util.Wrapper;

public class DisplayUtils {

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	public static <T> T get(Supplier<T> supplier) {
		return new SyncUiThreadRunner<T>().get(supplier);
	}

	public static <T> T getWithException(SupplierWithCoreException<T> supplier) throws CoreException {
		SyncUiThreadRunner<Wrapper<T, CoreException>> runner = new SyncUiThreadRunner<>();
		Wrapper<T, CoreException> result = runner.get(() -> {
			try {
				return Wrapper.ofValue(supplier.get());
			} catch (CoreException e) {
				return Wrapper.ofThrowable(e);
			}
		});
		if (result.hasValue()) {
			return result.getValue();
		} else {
			throw result.getThrowable();
		}
	}

	public static void syncExec(Runnable runnable) {
		getDisplay().syncExec(runnable);
	}

	public static void asyncExec(Runnable runnable) {
		getDisplay().asyncExec(runnable);
	}

	private static class SyncUiThreadRunner<T> {

		private T result;

		public T get(Supplier<T> supplier) {
			getDisplay().syncExec(() -> {
				result = supplier.get();
			});
			return result;
		}
	}

	public static interface SupplierWithCoreException<T> {

		T get() throws CoreException;
	}
}
