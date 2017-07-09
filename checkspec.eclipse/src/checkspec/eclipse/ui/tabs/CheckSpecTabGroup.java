package checkspec.eclipse.ui.tabs;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class CheckSpecTabGroup extends AbstractLaunchConfigurationTabGroup {

	public CheckSpecTabGroup() {
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = {
				new CheckSpecTab(),
		};
		setTabs(tabs);
	}
}
