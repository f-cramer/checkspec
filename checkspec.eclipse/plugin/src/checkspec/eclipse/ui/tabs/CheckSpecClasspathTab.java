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

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import checkspec.eclipse.CheckSpecPlugin;
import checkspec.eclipse.util.classpath.ClassPathEntry;
import checkspec.eclipse.util.classpath.ClassPathEntrySerializer;

public abstract class CheckSpecClasspathTab extends JavaLaunchTab {

	private final String name;
	private final String attributeName;

	private Table list;

	private boolean initialized = false;

	public CheckSpecClasspathTab(String name, String attributeName) {
		this.name = name;
		this.attributeName = attributeName;
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		composite.setLayout(layout);

		list = new Table(composite, SWT.FILL);
		setControl(composite);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(attributeName, Collections.emptyList());
	}

	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		super.initializeFrom(config);

		if (!initialized) {
			try {
				List<String> entryStrings = config.getAttribute(attributeName, Collections.emptyList());
				ClassPathEntry[] entries = entryStrings.stream()
						.map(ClassPathEntrySerializer::from)
						.toArray(ClassPathEntry[]::new);

				setListItems(entries);
			} catch (CoreException expected) {
			}
			initialized = true;
		}
	}

	private void setListItems(ClassPathEntry[] entries) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		for (ClassPathEntry entry : entries) {
			TableItem item = new TableItem(list, 0);
			item.setData(entry);
			item.setText(entry.getName(workspace));
			item.setImage(CheckSpecPlugin.getImage(entry.getType().toString()));
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		TableItem[] tableItems = list.getItems();
		List<String> items = Arrays.stream(tableItems)
				.map(item -> (ClassPathEntry) item.getData())
				.map(ClassPathEntrySerializer::toString)
				.collect(Collectors.toList());

		configuration.setAttribute(attributeName, items);
	}

	@Override
	public String getName() {
		return name;
	}
}
