package ambit2.rest.test.task;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.core.data.model.Algorithm;
import ambit2.model.numeric.DataCoverageLeverage;
import ambit2.rest.AmbitApplication;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.task.CallableNumericalModelCreator;
import ambit2.rest.task.TaskResult;
import ambit2.rest.test.ResourceTest;

public class CallableDomainCreatorTest extends ResourceTest {
	@Test
	public void test() throws Exception {
		Reference app = new Reference(String.format("http://localhost:%d",port));
		Form form = new Form();
		form.add("dataset_uri", String.format("http://localhost:%d/dataset/1",port));

		Algorithm alg = new Algorithm();
		alg.setContent(DataCoverageLeverage.class.getName());
		
		ModelURIReporter uriReporter = new ModelURIReporter(null);
		
		CallableNumericalModelCreator c = new CallableNumericalModelCreator(
				form,
				app,
				component.getApplication()==null?null:
				((AmbitApplication)component.getApplication()).getContext(),
				alg, 
				uriReporter,
				new AlgorithmURIReporter(null,null),
				null);
		TaskResult ref = c.call();
	}
	@Override
	public void testGetJavaObject() throws Exception {
	}
}
