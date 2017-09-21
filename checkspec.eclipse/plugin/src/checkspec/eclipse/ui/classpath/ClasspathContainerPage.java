package checkspec.eclipse.ui.classpath;

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



import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import checkspec.eclipse.Constants;

public class ClasspathContainerPage extends WizardPage implements IClasspathContainerPage {

	private static final String PAGE_NAME = "CheckSpec API";

	private IClasspathEntry result = JavaCore.newContainerEntry(Constants.CHECKSPEC_CONTAINER_PATH);

	public ClasspathContainerPage() {
		super(PAGE_NAME);
		setTitle(PAGE_NAME);
		setDescription("Adding CheckSpec API Library Container");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);

		Text text = new Text(composite, SWT.MULTI);
		text.setText("Finishing this dialog will add the CheckSpec API Library to the classpath of the currently selected project.");
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		setControl(composite);
	}

	@Override
	public boolean finish() {
		return true;
	}

	@Override
	public IClasspathEntry getSelection() {
		return result;
	}

	@Override
	public void setSelection(IClasspathEntry containerEntry) {
		if (containerEntry != null) {
			result = containerEntry;
		}
	}
}
