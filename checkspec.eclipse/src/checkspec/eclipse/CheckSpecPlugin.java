package checkspec.eclipse;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
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

	private static final String PLUGIN_ID = "checkspec.eclipse";
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

	public static String getPluginId() {
		return PLUGIN_ID;
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
}