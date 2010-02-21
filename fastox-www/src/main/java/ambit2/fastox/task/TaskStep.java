package ambit2.fastox.task;

import ambit2.fastox.wizard.WizardStep;

/**
 * Shows running tasks in the system / per user
 * @author nina
 *
 */
public class TaskStep extends WizardStep {

	public TaskStep() {
		super(1,"Jobs","Jobs)",ToxPredictTaskResource.class);
	}

}
