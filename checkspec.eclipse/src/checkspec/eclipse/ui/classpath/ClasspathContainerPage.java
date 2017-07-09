package checkspec.eclipse.ui.classpath;

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
