package ambit2.fastox.test.step;

import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.Form;

import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.steps.step5.Step5;
import ambit2.fastox.test.StepProcessorTest;
import ambit2.fastox.users.IToxPredictSession;
import ambit2.fastox.users.ToxPredictSession;
import ambit2.fastox.users.ToxPredictUser;
import ambit2.fastox.wizard.Wizard;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.fastox.wizard.Wizard.WizardMode;
import ambit2.fastox.wizard.WizardStep;

public class Step5ProcessorTest extends StepProcessorTest {

	@Override
	protected WizardStep getStep() {
		return new Step5();
	}

	
	@Test
	public void testRunModels() throws Exception {
		IToxPredictSession session = new ToxPredictSession(new ToxPredictUser());
		Wizard wizard = Wizard.getInstance(WizardMode.A);
		Assert.assertNotNull(wizard.getService(SERVICE.compound));
		Assert.assertNotNull(wizard.getService(SERVICE.model));
		Form form = new Form();
		form.add(FastoxStepResource.params.dataset.toString(),String.format("%s/1",
				wizard.getService(SERVICE.compound)));
		String model = String.format("%s/1",wizard.getService(SERVICE.model));
		form.add(FastoxStepResource.params.model.toString(),model);
		form.add(model,"on");
		form = getStepProcessor().process(form.getWebRepresentation(),session);
		
		String[] models = form.getValuesArray(FastoxStepResource.params.model.toString());
		Assert.assertNotNull(models);
		Assert.assertEquals(1,models.length);
		Assert.assertEquals(model,models[0]);
		
		String[] taskUri = form.getValuesArray(models[0]); 
		Assert.assertNotNull(taskUri);
		Assert.assertEquals(1,taskUri.length);
		
		String[] taskStatus = form.getValuesArray(taskUri[0]); 
		Assert.assertNotNull(taskStatus);
		Assert.assertEquals(1,taskStatus.length);
	}
}
