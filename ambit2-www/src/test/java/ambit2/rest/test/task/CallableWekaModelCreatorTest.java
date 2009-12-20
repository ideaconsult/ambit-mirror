package ambit2.rest.test.task;

import org.restlet.data.Reference;

import ambit2.core.data.model.Algorithm;
import ambit2.rest.AmbitApplication;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.task.CallableWekaModelCreator;
import ambit2.rest.test.ResourceTest;
import ambit2.rest.test.dataset.DatasetReporterTest;

public class CallableWekaModelCreatorTest extends ResourceTest {
	
	public void test() throws Exception {
		Reference app = new Reference(String.format("http://localhost:%d",port));
		Reference dataset = new Reference(DatasetReporterTest.class.getResource("/input.rdf"));
		ModelURIReporter uriReporter = new ModelURIReporter(null);
		CallableWekaModelCreator c = new CallableWekaModelCreator(
				dataset,
				app,
				(AmbitApplication)component.getApplication(), 
				(Algorithm)null, 
				uriReporter);
		Reference ref = c.call();
	}
	@Override
	public void testGetJavaObject() throws Exception {
	}
	
}
