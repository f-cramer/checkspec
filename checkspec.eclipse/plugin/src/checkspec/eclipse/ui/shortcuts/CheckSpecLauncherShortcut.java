package checkspec.eclipse.ui.shortcuts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import checkspec.eclipse.CheckSpecPlugin;
import checkspec.eclipse.Constants;
import checkspec.eclipse.util.classpath.ClassPath;
import checkspec.eclipse.util.classpath.ProjectClassPathEntry;
import checkspec.eclipse.util.classpath.SourceClassPathEntry;

public class CheckSpecLauncherShortcut implements ILaunchShortcut2 {

	@Override
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			launch(((IStructuredSelection) selection).toArray(), mode);
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		ITypeRoot element = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
		launch(new Object[] { element }, mode);
	}

	private void launch(Object[] elements, String mode) {
		try {
			IJavaElement elementToLaunch = null;

			if (elements.length == 1) {
				IJavaElement selected = adaptToJavaElement(elements[0]);
				if (selected != null) {
					IJavaElement element = selected;
					switch (element.getElementType()) {
					case IJavaElement.TYPE:
						elementToLaunch = element;
						break;
					case IJavaElement.CLASS_FILE:
						elementToLaunch = ((IClassFile) element).getType();
						break;
					case IJavaElement.COMPILATION_UNIT:
						elementToLaunch = findTypeToLaunch((ICompilationUnit) element, mode);
						break;
					}
				}
			}

			if (elementToLaunch == null) {
				return;
			}
			performLaunch(elementToLaunch, mode);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void performLaunch(IJavaElement element, String mode) throws InterruptedException, CoreException {
		ILaunchConfigurationWorkingCopy temparary = createLaunchConfiguration(element);
		ILaunchConfiguration config = findExistingLaunchConfiguration(temparary, mode);
		if (config == null) {
			// no existing found: create a new one
			config = temparary.doSave();
		}
		DebugUITools.launch(config, mode);
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if (ss.size() == 1) {
				return findExistingLaunchConfigurations(ss.getFirstElement());
			}
		}
		return null;
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editor) {
		ITypeRoot element = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
		if (element != null) {
			return findExistingLaunchConfigurations(element);
		}
		return null;
	}

	private ILaunchConfiguration[] findExistingLaunchConfigurations(Object candidate) {
		IJavaElement element = adaptToJavaElement(candidate);
		if (element != null) {
			IJavaElement elementToLaunch = null;
			try {
				switch (element.getElementType()) {
				case IJavaElement.METHOD:
					elementToLaunch = ((IMethod) element).getDeclaringType();
					break;
				case IJavaElement.TYPE:
					elementToLaunch = element;
					break;
				case IJavaElement.CLASS_FILE:
					elementToLaunch = ((IClassFile) element).getType();
					break;
				case IJavaElement.COMPILATION_UNIT:
					elementToLaunch = ((ICompilationUnit) element).findPrimaryType();
					break;
				}
				if (elementToLaunch == null) {
					return null;
				}

				ILaunchConfigurationWorkingCopy wc = createLaunchConfiguration(elementToLaunch);
				List<ILaunchConfiguration> list = findExistingLaunchConfigurations(wc);
				return list.toArray(new ILaunchConfiguration[list.size()]);
			} catch (CoreException expected) {
			}
		}
		return null;
	}

	private List<ILaunchConfiguration> findExistingLaunchConfigurations(ILaunchConfigurationWorkingCopy temporary) throws CoreException {
		ILaunchConfigurationType type = temporary.getType();
		ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(type);
		String[] attributeToCompare = getAttributeNamesToCompare();
	
		List<ILaunchConfiguration> candidateConfigs = new ArrayList<>(configs.length);
		for (ILaunchConfiguration config : configs) {
			if (hasSameAttributes(config, temporary, attributeToCompare)) {
				candidateConfigs.add(config);
			}
		}
		return candidateConfigs;
	}

	private ILaunchConfiguration findExistingLaunchConfiguration(ILaunchConfigurationWorkingCopy temporary, String mode) throws CoreException, InterruptedException {
		List<ILaunchConfiguration> candidateConfigs = findExistingLaunchConfigurations(temporary);
		int candidateCount = candidateConfigs.size();
		if (candidateCount == 0) {
			return null;
		} else if (candidateCount == 1) {
			return candidateConfigs.get(0);
		} else {
			return chooseConfiguration(candidateConfigs, mode);
		}
	}

	private ILaunchConfiguration chooseConfiguration(List<ILaunchConfiguration> configList, String mode) throws InterruptedException {
		IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle("select specification");
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			dialog.setMessage("select debug configuration");
		} else {
			dialog.setMessage("select run configuration");
		}
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		throw new InterruptedException(); // cancelled by user
	}

	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	@Override
	public IResource getLaunchableResource(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if (ss.size() == 1) {
				IJavaElement selected = adaptToJavaElement(ss.getFirstElement());
				if (selected != null) {
					return selected.getResource();
				}
			}
		}
		return null;
	}

	@Override
	public IResource getLaunchableResource(IEditorPart editor) {
		ITypeRoot element = JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
		if (element != null) {
			try {
				return element.getCorrespondingResource();
			} catch (JavaModelException expected) {
			}
		}
		return null;
	}

	private IJavaElement adaptToJavaElement(Object o) {
		if (o instanceof IJavaElement) {
			return (IJavaElement) o;
		} else if (o instanceof IAdaptable) {
			return ((IAdaptable) o).getAdapter(IJavaElement.class);
		}
		return null;
	}

	private ILaunchConfigurationWorkingCopy createLaunchConfiguration(IJavaElement element) throws CoreException {
		String mainTypeQualifiedName;

		switch (element.getElementType()) {
		case IJavaElement.TYPE:
			mainTypeQualifiedName = ((IType) element).getFullyQualifiedName('.');
			break;
		case IJavaElement.METHOD:
			mainTypeQualifiedName = ((IMethod) element).getDeclaringType().getFullyQualifiedName('.');
			break;
		default:
			throw new IllegalArgumentException("Invalid element type to create a launch configuration: " + element.getClass());
		}

		ILaunchConfigurationType configType = getLaunchManager().getLaunchConfigurationType(Constants.ID_CHECK_SPECK);
		String configName = getLaunchManager().generateLaunchConfigurationName(suggestLaunchConfigurationName(element));
		ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, configName);

		ClassPath implementationPath = getImplementationPath(element.getJavaProject());
		String basePackage = "";

		wc.setAttribute(Constants.ATTR_SPECIFICATION_TYPE_NAMES, Arrays.asList(mainTypeQualifiedName));
		wc.setAttribute(Constants.ATTR_SPECIFICATION_CLASSPATH, implementationPath.toStringList());
		wc.setAttribute(Constants.ATTR_IMPLEMENTATION_CLASSPATH, implementationPath.toStringList());
		wc.setAttribute(Constants.ATTR_BASE_PACKAGE, basePackage);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, element.getJavaProject().getElementName());
		return wc;
	}

	private ClassPath getImplementationPath(IJavaProject project) {
		IClasspathEntry[] entries;
		ClassPath classPath = ClassPath.empty();
		try {
			entries = project.getResolvedClasspath(true);

			IPath path = project.getOutputLocation();
			if (path != null) {
				classPath.add(new SourceClassPathEntry(path));
			}

			for (IClasspathEntry entry : entries) {
				CheckSpecPlugin.logError(entry.toString());
				switch (entry.getEntryKind()) {
				case IClasspathEntry.CPE_SOURCE:
					path = entry.getOutputLocation();
					if (path != null) {
						classPath.add(new SourceClassPathEntry(path));
					}
					break;
				case IClasspathEntry.CPE_PROJECT:
					path = entry.getPath();
					if (path != null) {
						classPath.add(new ProjectClassPathEntry(path));
					}
					break;
				}
			}
		} catch (JavaModelException expected) {
		}
		return classPath;
	}

	private IType findTypeToLaunch(ICompilationUnit cu, String mode) throws CoreException, InterruptedException {
		IType[] types = findTypesToLaunch(cu);
		if (types.length == 0) {
			return null;
		} else if (types.length > 1) {
			return chooseType(types, mode);
		}
		return types[0];
	}

	private IType[] findTypesToLaunch(ICompilationUnit cu) throws CoreException {
		try {
			return cu.getAllTypes();
		} catch (JavaModelException e) {
			throw new CoreException(new Status(Status.ERROR, Constants.PLUGIN_ID, e.getMessage()));
		}
	}

	private IType chooseType(IType[] types, String mode) throws InterruptedException {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_POST_QUALIFIED));
		dialog.setElements(types);
		dialog.setTitle("");
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			dialog.setMessage("select spec to debug");
		} else {
			dialog.setMessage("select spec to run");
		}
		dialog.setMultipleSelection(false);
		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		throw new InterruptedException(); // cancelled by user
	}

	private String suggestLaunchConfigurationName(IJavaElement element) {
		switch (element.getElementType()) {
		case IJavaElement.TYPE:
			return element.getElementName();
		case IJavaElement.METHOD:
			IMethod method = (IMethod) element;
			return method.getDeclaringType().getElementName() + '.' + method.getElementName();
		default:
			throw new IllegalArgumentException("Invalid element type to create a launch configuration: " + element.getClass().getName());
		}
	}

	protected String[] getAttributeNamesToCompare() {
		return new String[] { Constants.ATTR_SPECIFICATION_TYPE_NAMES, IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, };
	}

	public Shell getShell() {
		return CheckSpecPlugin.getActiveWorkbenchShell();
	}

	private static boolean hasSameAttributes(ILaunchConfiguration config1, ILaunchConfiguration config2, String[] attributeToCompare) {
		try {
			for (String element : attributeToCompare) {
				String val1 = config1.getAttribute(element, "");
				String val2 = config2.getAttribute(element, "");
				if (!val1.equals(val2)) {
					return false;
				}
			}
			return true;
		} catch (CoreException e) {
			// ignore access problems here, return false
		}
		return false;
	}
}
