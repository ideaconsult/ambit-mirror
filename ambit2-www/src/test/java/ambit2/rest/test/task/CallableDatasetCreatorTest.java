package ambit2.rest.test.task;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.rest.OpenTox;
import ambit2.rest.task.CallableDatasetCreator;
import ambit2.rest.test.ResourceTest;

public class CallableDatasetCreatorTest extends ResourceTest {
	@Test
	public void test() throws Exception {
		Reference app = new Reference("http://ambit.uni-plovdiv.bg:8080/ambit2");
		Form form = new Form();
		form.add(OpenTox.params.dataset_uri.toString(), 
				//"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/10");
				"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/169");
				//"http://ambit.uni-plovdiv.bg:8080/ambit2/compound/1/conformer/100205");
				//"http://194.141.0.136:8080/ambit2/dataset/48?feature_uris[]=http://194.141.0.136:8080/ambit2/feature/13592");
		form.add(OpenTox.params.model_uri.toString(), 
				String.format("http://opentox.informatik.tu-muenchen.de:8080/OpenTox-dev/model/%s","TUMOpenToxModel_kNN_6"));
		
		CallableDatasetCreator c = new CallableDatasetCreator(
				form,
				app,
				null);
		Reference ref = c.call();
		System.out.println(ref);
	}
	@Override
	public void testGetJavaObject() throws Exception {
	
	}
}
