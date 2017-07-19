package checkspec.eclipse.ui.tabs;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import checkspec.eclipse.CheckSpecPlugin;
import checkspec.eclipse.Constants;

@SuppressWarnings("restriction")
public class CheckSpecTab extends JavaLaunchTab {

	private Text projectText;
	private Button projectButton;

	private Text specificationText;
	private Button searchButton;

	private WidgetListener listener = new WidgetListener();

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);

		createProjectEditor(composite);
		createVerticalSpacer(composite, 1);
		createSpecificationEditor(composite);
		setControl(composite);
	}

	private void createProjectEditor(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText("Project");
		GridData groupData = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(groupData);

		projectText = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		projectText.setLayoutData(textData);
		projectText.addModifyListener(listener);
		projectButton = createPushButton(group, "&Browse...", null);
		projectButton.addSelectionListener(listener);
	}

	private void createSpecificationEditor(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText("Specification Class");
		GridData groupData = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(groupData);

		specificationText = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		specificationText.setLayoutData(textData);
		specificationText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		searchButton = createPushButton(group, "&Search...", null);
		searchButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSearchButtonSelected();
			}
		});
	}

	private void handleSearchButtonSelected() {
		IJavaProject project = getJavaProject();
		IJavaElement[] elements = null;
		if ((project == null) || !project.exists()) {
			IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
			if (model != null) {
				try {
					elements = model.getJavaProjects();
				} catch (JavaModelException expected) {
				}
			}
		} else {
			elements = new IJavaElement[] { project };
		}
		if (elements == null) {
			elements = new IJavaElement[] {};
		}
		int constraints = IJavaSearchScope.SOURCES | IJavaSearchScope.APPLICATION_LIBRARIES;
		IJavaSearchScope searchScope = SearchEngine.createJavaSearchScope(elements, constraints);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, CheckSpecPlugin.getActiveWorkbenchWindow(), searchScope, IJavaSearchConstants.CLASS_AND_INTERFACE);
		if (dialog.open() == Window.CANCEL) {
			return;
		}
		Object[] results = dialog.getResult();
		IType type = (IType) results[0];
		if (type != null) {
			specificationText.setText(type.getFullyQualifiedName());
			projectText.setText(type.getJavaProject().getElementName());
		}
	}

	private void handleProjectButtonSelected() {
		IJavaProject project = chooseJavaProject();
		if (project == null) {
			return;
		}
		String projectName = project.getElementName();
		projectText.setText(projectName);
	}

	private IJavaProject chooseJavaProject() {
		ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle("Project Selection");
		dialog.setMessage("Select a project to constrain your search.");
		try {
			dialog.setElements(JavaCore.create(getWorkspaceRoot()).getJavaProjects());
		} catch (JavaModelException expected) {
		}
		IJavaProject javaProject = getJavaProject();
		if (javaProject != null) {
			dialog.setInitialSelections(new Object[] { javaProject });
		}
		if (dialog.open() == Window.OK) {
			return (IJavaProject) dialog.getFirstResult();
		}
		return null;
	}

	protected IJavaProject getJavaProject() {
		String projectName = projectText.getText().trim();
		if (projectName.length() < 1) {
			return null;
		}
		return getJavaModel().getJavaProject(projectName);
	}

	private IJavaModel getJavaModel() {
		return JavaCore.create(getWorkspaceRoot());
	}

	protected IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		try {
			projectText.setText(configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""));

			List<String> specifications = configuration.getAttribute(Constants.ATTR_SPECIFICATION_TYPE_NAMES, Collections.emptyList());
			specificationText.setText(specifications.size() > 0 ? specifications.get(0) : "");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		super.initializeFrom(config);
		updateProjectFromConfig(config);
		updateSpecificationFromConfig(config);
	}

	private void updateProjectFromConfig(ILaunchConfiguration config) {
		String projectName = "";
		try {
			projectName = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
		} catch (CoreException expected) {
		}
		projectText.setText(projectName);
	}

	private void updateSpecificationFromConfig(ILaunchConfiguration config) {
		String specificationName = "";
		try {
			List<String> names = config.getAttribute(Constants.ATTR_SPECIFICATION_TYPE_NAMES, Collections.emptyList());
			if (names.size() > 0) {
				specificationName = names.get(0);
			}
		} catch (CoreException expected) {
		}
		specificationText.setText(specificationName);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectText.getText());
		configuration.setAttribute(Constants.ATTR_SPECIFICATION_TYPE_NAMES, Collections.singletonList(specificationText.getText()));
	}

	@Override
	public String getName() {
		return null;
	}

	private class WidgetListener implements ModifyListener, SelectionListener {

		@Override
		public void widgetSelected(SelectionEvent e) {
			Object source = e.getSource();
			if (source == projectButton) {
				handleProjectButtonSelected();
			} else {
				updateLaunchConfigurationDialog();
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		@Override
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}
	}
}
