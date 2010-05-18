package ambit2.fastox.steps.step5;

import ambit2.fastox.wizard.WizardStep;

public class Step5 extends WizardStep {

	public Step5() {
		super(4,"Run"," prediction(s)",Step5Resource.class,Step5Processor.class);
	}

}
