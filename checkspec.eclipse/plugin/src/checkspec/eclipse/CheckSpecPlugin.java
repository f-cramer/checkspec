package checkspec.eclipse;

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



import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import checkspec.eclipse.util.DisplayUtils;

public class CheckSpecPlugin extends AbstractUIPlugin implements BundleActivator {

	public static final String PLUGIN_ID = "checkspec.eclipse";
	private static BundleContext context;
	private static CheckSpecPlugin plugin;

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		CheckSpecPlugin.plugin = this;
		CheckSpecPlugin.context = bundleContext;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		CheckSpecPlugin.context = null;
	}

	static BundleContext getContext() {
		return context;
	}

	public static CheckSpecPlugin getInstance() {
		return plugin;
	}

	public Bundle getBundle(String bundleName) {
		Bundle[] bundles = getBundles(bundleName, null);
		if (bundles != null && bundles.length > 0) {
			return bundles[0];
		}
		return null;
	}

	public Bundle[] getBundles(String bundleName, String version) {
		Bundle[] bundles = Platform.getBundles(bundleName, version);
		if (bundles != null) {
			return bundles;
		}

		return null;
	}

	public static IConfigurationElement[] getExtensions(String extensionPointId) {
		return Platform.getExtensionRegistry().getConfigurationElementsFor(extensionPointId);
	}

	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow workBenchWindow = getActiveWorkbenchWindow();
		if (workBenchWindow == null) {
			return null;
		}
		return workBenchWindow.getShell();
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		if (plugin == null) {
			return null;
		}
		IWorkbench workBench = plugin.getWorkbench();
		if (workBench == null) {
			return null;
		}
		return DisplayUtils.get(() -> workBench.getActiveWorkbenchWindow());
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return null;
		}
		return activeWorkbenchWindow.getActivePage();
	}

	public static Image getImage(String relativePath) {
		IPath path = Constants.ICON_PATH.append(relativePath);
		URL url = FileLocator.find(plugin.getBundle(), path, null);
		Image image = ImageDescriptor.createFromURL(url).createImage();
		return image == null ? ImageDescriptor.getMissingImageDescriptor().createImage() : image;
	}

	public static void logInfo(String warning) {
		if (plugin != null) {
			plugin.getLog().log(new Status(IStatus.INFO, PLUGIN_ID, warning));
		}
	}

	public static void logWarning(String warning) {
		if (plugin != null) {
			plugin.getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, warning));
		}
	}

	public static void logError(String warning) {
		if (plugin != null) {
			plugin.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, warning));
		}
	}
}
