package checkspec.eclipse;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public interface Constants {

	public static final String PLUGIN_ID = CheckSpecPlugin.getPluginId();

	public static final String CHECKSPEC_PREFIX = "checkspec.eclipse";

	public static final String ID_CHECK_SPECK = CHECKSPEC_PREFIX + ".checkSpec";

	public static final String ATTR_SPECIFICATION_TYPE_NAMES = CHECKSPEC_PREFIX + ".specificationTypeNames";
	public static final String ATTR_SPECIFICATION_CLASSPATH = CHECKSPEC_PREFIX + ".specificationClasspath";
	public static final String ATTR_IMPLEMENTATION_CLASSPATH = CHECKSPEC_PREFIX + ".implementationClasspath";
	public static final String ATTR_BASE_PACKAGE = CHECKSPEC_PREFIX + ".basePackage";

	public static final String CLASSPATH_SEPARATOR = File.pathSeparator;

	public static final String CHECKSPEC_CONTAINER_ID = CHECKSPEC_PREFIX + ".CHECKSPEC_CONTAINER";
	public static final IPath CHECKSPEC_CONTAINER_PATH = new Path(CHECKSPEC_CONTAINER_ID);

	public static final String API_LIBRARY_PATH = "/lib/checkspec.api.jar";
	public static final String API_LIBRARY_SOURCE_PATH = "/lib/checkspec.api-sources.jar";
	public static final String FULL_LIBRARY_PATH = "/lib/checkspec.bundle.jar";

	public static final IPath ICON_PATH = new Path("/img");

}
