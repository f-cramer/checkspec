package checkspec.eclipse.util.classpath;

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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;

import checkspec.eclipse.util.PathConverter;

public class SourceClassPathEntry implements ClassPathEntry {

	private final IPath path;

	public SourceClassPathEntry(IPath path) {
		Objects.requireNonNull(path, "path");
		this.path = path;
	}

	public IPath getPath() {
		return path;
	}

	@Override
	public List<URL> resolve(IJavaProject project) {
		IPath workspacePath = project.getProject().getWorkspace().getRoot().getLocation();
		return Collections.singletonList(PathConverter.toUrl(workspacePath.append(path)));
	}

	@Override
	public ClassPathType getType() {
		return ClassPathType.SOURCE;
	}

	@Override
	public String getName(IWorkspace workspace) {
		return path.toString();
	}
}
