package ambit2.fastox.steps.step3;

import ambit2.fastox.wizard.WizardStep;

public class Step3 extends WizardStep {
	public Step3() {
		super(3,"Endpoints","Select endpoints and models",Step3Resource.class,Step3Processor.class);
	}
}
