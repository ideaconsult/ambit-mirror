package ambit2.fastox.steps.step2;

import ambit2.fastox.wizard.WizardStep;

public class Step2 extends WizardStep {
	public Step2() {
		super(2,"Verify","structure(s)",Step2Resource.class, Step2Processor.class);
	}
}
