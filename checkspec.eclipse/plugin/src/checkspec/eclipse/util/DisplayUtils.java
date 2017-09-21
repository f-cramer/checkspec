package checkspec.eclipse.util;

/*-
 * #%L
 * checkspec.eclipse.plugin
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
