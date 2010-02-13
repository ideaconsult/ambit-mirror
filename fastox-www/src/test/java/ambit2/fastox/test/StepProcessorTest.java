package ambit2.fastox.test;

import org.junit.Assert;
import org.junit.Test;

import ambit2.fastox.steps.StepProcessor;
import ambit2.fastox.wizard.WizardStep;
import ambit2.fastox.wizard.Wizard.WizardMode;

public abstract class StepProcessorTest {
	protected WizardStep step;
	protected WizardMode mode = WizardMode.A;
	protected abstract WizardStep getStep();
	@Test
	public void testStepExist() throws Exception {
		Assert.assertNotNull(getStep());
	}
	
	public StepProcessor getStepProcessor() throws Exception {
		return getStep().getProcessor();
	}
}
