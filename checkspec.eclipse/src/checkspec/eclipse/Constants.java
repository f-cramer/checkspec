package checkspec.eclipse;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public interface Constants {

	public static final String PLUGIN_ID = CheckSpecPlugin.PLUGIN_ID;

	public static final String ID_CHECK_SPECK = PLUGIN_ID + ".checkSpec";

	public static final String ATTR_SPECIFICATION_TYPE_NAMES = PLUGIN_ID + ".specificationTypeNames";
	public static final String ATTR_SPECIFICATION_CLASSPATH = PLUGIN_ID + ".specificationClasspath";
	public static final String ATTR_IMPLEMENTATION_CLASSPATH = PLUGIN_ID + ".implementationClasspath";
	public static final String ATTR_BASE_PACKAGE = PLUGIN_ID + ".basePackage";
	public static final String ATTR_EXTENSION_CLASSPATH = PLUGIN_ID + ".extensionClasspath";

	public static final String CLASSPATH_SEPARATOR = File.pathSeparator;

	public static final String CHECKSPEC_CONTAINER_ID = PLUGIN_ID + ".CHECKSPEC_CONTAINER";
	public static final IPath CHECKSPEC_CONTAINER_PATH = new Path(CHECKSPEC_CONTAINER_ID);

	public static final String API_LIBRARY_PATH = "/lib/checkspec.api.jar";
	public static final String API_LIBRARY_SOURCE_PATH = "/lib/checkspec.api-sources.jar";
	public static final String FULL_LIBRARY_PATH = "/lib/checkspec.bundle.jar";

	public static final IPath ICON_PATH = new Path("/img");

}
