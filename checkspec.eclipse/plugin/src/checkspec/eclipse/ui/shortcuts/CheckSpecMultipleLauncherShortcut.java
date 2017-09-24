package checkspec.eclipse.ui.shortcuts;

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

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;

import checkspec.eclipse.util.classpath.Classpath;
import checkspec.eclipse.util.classpath.ProjectClasspathEntry;

public class CheckSpecMultipleLauncherShortcut extends CheckSpecLauncherShortcut {

	@Override
	protected Classpath getImplementationPath(IJavaProject project) {
		IProject[] projects = project.getProject().getWorkspace().getRoot().getProjects();
		Classpath classpath = Classpath.empty();

		Arrays.stream(projects)
				.filter(IProject::isOpen)
				.map(ProjectClasspathEntry::new)
				.forEach(classpath::add);
		return classpath;
	}
}
