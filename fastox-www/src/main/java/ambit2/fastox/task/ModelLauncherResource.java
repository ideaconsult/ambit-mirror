package ambit2.fastox.task;

import org.restlet.data.Reference;

import ambit2.fastox.wizard.Wizard;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.fastox.wizard.Wizard.WizardMode;
import ambit2.rest.task.LauncherResource;

public class ModelLauncherResource extends LauncherResource {
	public static final String resource = "/algorithm";
	@Override
	protected Reference getApplicationRoot() {
		return Wizard.getInstance(WizardMode.A).getService(SERVICE.application);
	}
	@Override
	protected Reference getDatasetService() {

		return Wizard.getInstance(WizardMode.A).getService(SERVICE.dataset);
	}
}
