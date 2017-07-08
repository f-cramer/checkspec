package checkspec.eclipse.ui.classpath;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import checkspec.eclipse.Constants;

public class ClasspathContainerPage extends WizardPage implements IClasspathContainerPage {

	private static final String PAGE_NAME = "CheckSpec API Container";

	private IClasspathEntry result = JavaCore.newContainerEntry(Constants.CHECKSPEC_CONTAINER_PATH);

	public ClasspathContainerPage() {
		this(PAGE_NAME);
	}

	public ClasspathContainerPage(String pageName) {
		super(pageName);
	}

	public static IJavaProject getPlaceholderProject() {
		String name = "####checkspecinternal";
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		while (true) {
			IProject project = root.getProject(name);
			if (!project.exists()) {
				return JavaCore.create(project);
			}
			name += '1';
		}
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
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
