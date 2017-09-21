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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.URIUtil;

public class PathConverter {

	public static String toString(IPath path) {
		try {
			return URIUtil.toFile(URIUtil.toURI(toUrl(path))).getCanonicalFile().getAbsolutePath();
		} catch (URISyntaxException | IOException e) {
			return null;
		}
	}

	public static URL toUrl(IPath path) {
		try {
			return FileLocator.toFileURL(path.toFile().getAbsoluteFile().getCanonicalFile().toURI().toURL());
		} catch (IOException e) {
			return null;
		}
	}
}
