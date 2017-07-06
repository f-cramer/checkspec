package checkspec.eclipse.launching;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CheckSpecPlugin implements BundleActivator {

	private static final String PLUGIN_ID = "checkspec.eclipse.launching";
	private static BundleContext context;
	private static CheckSpecPlugin plugin;

	public void start(BundleContext bundleContext) throws Exception {
		CheckSpecPlugin.plugin = this;
		CheckSpecPlugin.context = bundleContext;
	}

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
		Bundle[] bundles= getBundles(bundleName, null);
		if (bundles != null && bundles.length > 0)
			return bundles[0];
		return null;
	}

	public Bundle[] getBundles(String bundleName, String version) {
		Bundle[] bundles= Platform.getBundles(bundleName, version);
		if (bundles != null) {
			return bundles;
		}

//		ServiceReference<?> serviceRef= context.getServiceReference(PackageAdmin.class.getName());
//		PackageAdmin admin= (PackageAdmin)context.getService(serviceRef);
//		bundles= admin.getBundles(bundleName, version);
//		if (bundles != null && bundles.length > 0) {
//			return bundles;
//		}
		return null;
	}
}
