package checkspec.eclipse.classpath;

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



import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

import checkspec.eclipse.CheckSpecPlugin;
import checkspec.eclipse.Constants;

public class CheckSpecContainer implements IClasspathContainer {

	public static final String CLASSPATH_ID = "checkspec.eclipse.classpath";

	private final IPath path = Constants.CHECKSPEC_CONTAINER_PATH;
	private final IClasspathEntry[] entries = new IClasspathEntry[1];

	public CheckSpecContainer() {
		Bundle bundle = CheckSpecPlugin.getInstance().getBundle();

		IPath path = getPathFromBundle(bundle, Constants.API_LIBRARY_PATH);
		IPath sourcePath = getPathFromBundle(bundle, Constants.API_LIBRARY_SOURCE_PATH);
		IClasspathEntry entry = JavaCore.newLibraryEntry(path, sourcePath, new Path("/"));
		entries[0] = entry;
	}

	private IPath getPathFromBundle(Bundle bundle, String path) {
		try {
			URL entry = FileLocator.toFileURL(bundle.getEntry(path));
			String absolutePath = URIUtil.toFile(URIUtil.toURI(entry)).getCanonicalFile().getAbsolutePath();
			return Path.fromOSString(absolutePath);
		} catch (IOException | URISyntaxException e) {
			return null;
		}
	}

	@Override
	public IClasspathEntry[] getClasspathEntries() {
		return entries;
	}

	@Override
	public String getDescription() {
		return "CheckSpec API";
	}

	@Override
	public int getKind() {
		return K_APPLICATION;
	}

	@Override
	public IPath getPath() {
		return path;
	}
}
