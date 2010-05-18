package ambit2.fastox.steps.step4;

import ambit2.fastox.wizard.WizardStep;

@Deprecated
public class Step4 extends WizardStep {
	public Step4() {
		super(4,"ExperimentalData","Retrieve endpoints data",Step4Resource.class,Step4Processor.class);
	}
}
