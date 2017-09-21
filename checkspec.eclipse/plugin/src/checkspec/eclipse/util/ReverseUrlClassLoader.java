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
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Objects;

public class ReverseUrlClassLoader extends ClassLoader {

	private final ClassLoader primary;
	private final ClassLoader secondary;

	public ReverseUrlClassLoader(ClassLoader primaryClassLoader, URL[] secondaryUrls) {
		this.primary = Objects.requireNonNull(primaryClassLoader, "primaryClassLoader");
		this.secondary = new URLClassLoader(secondaryUrls);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		try {
			return primary.loadClass(name);
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			return secondary.loadClass(name);
		}
	}

	@Override
	public URL getResource(String name) {
		URL resource = primary.getResource(name);
		if (resource != null) {
			return resource;
		}
		return secondary.getResource(name);
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		Enumeration<URL> resources = primary.getResources(name);
		if (resources.hasMoreElements()) {
			return primary.getResources(name);
		}
		return secondary.getResources(name);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		InputStream stream = primary.getResourceAsStream(name);
		if (stream != null) {
			return stream;
		}
		return secondary.getResourceAsStream(name);
	}
}
