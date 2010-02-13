package ambit2.fastox.test.step;

import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.Form;

import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.test.StepProcessorTest;
import ambit2.fastox.wizard.Wizard;
import ambit2.fastox.wizard.WizardStep;

public class Step3ProcessorTest extends StepProcessorTest {

	@Override
	protected WizardStep getStep() {
		return Wizard.getInstance(mode).getStep(3);
	}
	
	
	@Test
	public void testDataset() throws Exception {
		Form form = new Form();
		form.add(FastoxStepResource.params.dataset.toString(),"http://localhost/dataset/1");
		form = getStepProcessor().process(form.getWebRepresentation());
		
		String dataset = form.getFirstValue(FastoxStepResource.params.dataset.toString());
		
		Assert.assertNotNull(dataset);

	}
	@Test
	public void testNoDataset() throws Exception {
		Form form = new Form();
		try {
			form = getStepProcessor().process(form.getWebRepresentation());
		} catch (Exception x) {
			Assert.assertTrue("No dataset",true);
		}
	}	
}
