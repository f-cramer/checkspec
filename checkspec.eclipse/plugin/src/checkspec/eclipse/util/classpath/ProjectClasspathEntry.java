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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import checkspec.eclipse.util.PathConverter;

public class ProjectClasspathEntry implements ClasspathEntry {

	private final IPath projectPath;

	public ProjectClasspathEntry(IProject project) {
		Objects.requireNonNull(project);
		this.projectPath = project.getFullPath();
	}

	public ProjectClasspathEntry(IPath projectPath) {
		Objects.requireNonNull(projectPath);
		this.projectPath = projectPath;
	}

	public IPath getProjectPath() {
		return projectPath;
	}

	@Override
	public List<URL> resolve() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		return Arrays.stream(projects)
				.filter(p -> projectPath.equals(p.getFullPath()))
				.findAny()
				.map(ProjectClasspathEntry::resolve)
				.orElseGet(Collections::emptyList);
	}

	private static boolean hasJavaNature(IProject project) {
		try {
			return project.hasNature(JavaCore.NATURE_ID);
		} catch (CoreException e) {
			return false;
		}
	}

	private static List<URL> resolve(IProject project) {
		if (hasJavaNature(project)) {
			return resolve(JavaCore.create(project));
		} else {
			URL url = PathConverter.toUrl(project.getFullPath(), true);
			return Collections.singletonList(url);
		}
	}

	private static List<URL> resolve(IJavaProject project) {
		List<URL> urls = new ArrayList<>();
		try {
			addIfNonNull(urls, project.getOutputLocation());

			IClasspathEntry[] classpath = project.getResolvedClasspath(true);
			for (IClasspathEntry entry : classpath) {
				switch (entry.getEntryKind()) {
				case IClasspathEntry.CPE_PROJECT:
					urls.addAll(resolve(getProject(entry.getPath())));
					break;
				case IClasspathEntry.CPE_SOURCE:
					addIfNonNull(urls, entry.getOutputLocation());
					break;
				}
			}
		} catch (JavaModelException expected) {
		}
		return urls;
	}

	private static IProject getProject(IPath path) {
		return Arrays.stream(ResourcesPlugin.getWorkspace().getRoot().getProjects())
				.filter(project -> project.getFullPath().equals(path))
				.findAny()
				.get();
	}

	private static void addIfNonNull(List<URL> urls, IPath path) {
		if (path != null) {
			urls.add(PathConverter.toUrl(path, true));
		}
	}

	@Override
	public String getName(IWorkspace workspace) {
		IProject[] projects = workspace.getRoot().getProjects();
		Optional<IProject> optProject = Arrays.stream(projects)
				.filter(p -> projectPath.equals(p.getFullPath()))
				.findAny();
		if (!optProject.isPresent()) {
			return null;
		}

		IProject project = optProject.get();
		return project.getName();
	}
}
