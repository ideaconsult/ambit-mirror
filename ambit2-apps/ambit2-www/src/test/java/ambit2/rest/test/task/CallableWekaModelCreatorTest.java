package ambit2.rest.test.task;

import net.idea.restnet.c.ResourceDoc;

import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.core.data.model.Algorithm;
import ambit2.rest.AmbitApplication;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.task.TaskResult;
import ambit2.rest.task.weka.CallableWekaModelCreator;
import ambit2.rest.test.ResourceTest;
import ambit2.rest.test.dataset.DatasetReporterTest;

public class CallableWekaModelCreatorTest extends ResourceTest {
	
	public void test() throws Exception {
		Reference app = new Reference(String.format("http://localhost:%d",port));
		Form form = new Form();
		form.add("dataset_uri", DatasetReporterTest.class.getResource("/input.rdf").toString());
		form.add("target", "http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11938");
		ModelURIReporter uriReporter = new ModelURIReporter(null,new ResourceDoc());
		
		CallableWekaModelCreator c = new CallableWekaModelCreator(
				form,
				app,
				((AmbitApplication)component.getApplication()).getContext(),
				(Algorithm)null, 
				uriReporter,
				new AlgorithmURIReporter(null),
				null);
		TaskResult ref = c.call();
	}
	@Override
	public void testGetJavaObject() throws Exception {
	}
	
}
