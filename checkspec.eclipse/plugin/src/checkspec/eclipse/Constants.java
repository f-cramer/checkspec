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
