package checkspec.eclipse.ui.tabs;

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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import checkspec.eclipse.CheckSpecPlugin;
import checkspec.eclipse.util.classpath.ClasspathEntry;
import checkspec.eclipse.util.classpath.ClasspathEntrySerializer;
import checkspec.eclipse.util.classpath.ProjectClasspathEntry;
import checkspec.util.StreamUtils;

public abstract class CheckSpecClasspathTab extends JavaLaunchTab {

	private final String name;
	private final String attributeName;

	private Table list;

	public CheckSpecClasspathTab(String name, String attributeName) {
		this.name = name;
		this.attributeName = attributeName;
	}

	@Override
	public Image getImage() {
		return CheckSpecPlugin.getImage("library.png");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		composite.setLayout(layout);

		TableViewer viewer = new TableViewer(composite, SWT.CHECK | SWT.V_SCROLL | SWT.SINGLE | SWT.FILL);
		list = viewer.getTable();
		list.setToolTipText("");
		list.addListener(SWT.Selection, new CheckSpecSelectionListener());

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		for (IProject project : workspace.getRoot().getProjects()) {
			TableItem item = new TableItem(list, SWT.NONE);
			item.setData(project.getFullPath());
			item.setText(project.getName());
			item.setImage(CheckSpecPlugin.getImage("projects.png"));
		}

		setControl(composite);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(attributeName, Collections.emptyList());
	}

	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		super.initializeFrom(config);

		try {
			List<String> entryStrings = config.getAttribute(attributeName, Collections.emptyList());
			ClasspathEntry[] entries = entryStrings.stream()
					.map(ClasspathEntrySerializer::from)
					.toArray(ClasspathEntry[]::new);

			setListItems(entries);
		} catch (CoreException expected) {
		}
	}

	private void setListItems(ClasspathEntry[] entries) {
		List<IPath> selectedPaths = Arrays.stream(entries)
				.flatMap(StreamUtils.filterClass(ProjectClasspathEntry.class))
				.map(ProjectClasspathEntry::getProjectPath)
				.collect(Collectors.toList());

		for (TableItem item : list.getItems()) {
			item.setChecked(selectedPaths.contains(item.getData()));
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		TableItem[] tableItems = list.getItems();
		List<String> items = Arrays.stream(tableItems)
				.filter(TableItem::getChecked)
				.map(item -> (IPath) item.getData())
				.map(ProjectClasspathEntry::new)
				.map(ClasspathEntrySerializer::toString)
				.collect(Collectors.toList());

		configuration.setAttribute(attributeName, items);
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	private class CheckSpecSelectionListener implements Listener {

		@Override
		public void handleEvent(Event event) {
			if (event.detail == SWT.CHECK) {
				updateLaunchConfigurationDialog();
			}
		}
	}
}
