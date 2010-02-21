package ambit2.fastox.test.step;

import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;

import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.test.StepProcessorTest;
import ambit2.fastox.wizard.Wizard;
import ambit2.fastox.wizard.WizardStep;

public class Step2ProcessorTest  extends StepProcessorTest {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1832505512213787514L;
	
	@Override
	protected WizardStep getStep() {
		return Wizard.getInstance(mode).getStep(2);
	}
	
	
	@Test
	public void testFreeTextSearch() throws Exception {
		Form form = new Form();
		form.add(FastoxStepResource.params.text.toString(),"556-82-1");
		form = getStepProcessor().process(form.getWebRepresentation());
		
		String dataset = form.getFirstValue(FastoxStepResource.params.dataset.toString());
		
		Assert.assertNotNull(dataset);
		
		Form datasetQuery = new org.restlet.data.Reference(dataset).getQueryAsForm();
		String max = datasetQuery.getFirstValue("max");
		Assert.assertNotNull(max);
		Assert.assertEquals("100",max);
		
		String[] features = datasetQuery.getValuesArray("feature_uris[]");
		Assert.assertNotNull(features);
		Assert.assertEquals(4,features.length);
	}

	
	public void testPostDataset() throws Exception {
		String datasetService = "http://localhost:8080/ambit2/dataset";
		
		Form form = new Form();
		form.add(FastoxStepResource.params.text.toString(),"556-82-1");
		form = getStepProcessor().process(form.getWebRepresentation());
		
		String dataset = form.getFirstValue(FastoxStepResource.params.dataset.toString());
		
		Assert.assertNotNull(dataset);
		
		Form datasetQuery = new org.restlet.data.Reference(dataset).getQueryAsForm();
		String max = datasetQuery.getFirstValue("max");
		Assert.assertNotNull(max);
		Assert.assertEquals("100",max);
		
		String[] features = datasetQuery.getValuesArray("feature_uris[]");
		
		Representation r = getStepProcessor().post(new Reference(datasetService)
				,form.getWebRepresentation());
		
	}
}
