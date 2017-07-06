package checkspec.eclipse.launching;

import java.io.File;

public interface Constants {

	public static final String CHECKSPEC_PREFIX = "checkspec.eclipse.launching";

	public static final String ID_CHECK_SPECK = CHECKSPEC_PREFIX + ".checkSpec";
	public static final String ATTR_SPEC_TYPE_NAME = CHECKSPEC_PREFIX + ".specTypeName";
	public static final String ATTR_PROJECT_NAME = CHECKSPEC_PREFIX + ".projectName";
	public static final String ATTR_IMPL_PATH = CHECKSPEC_PREFIX + ".implPath";

	public static final String CLASSPATH_SEPARATOR = File.pathSeparator;
}
