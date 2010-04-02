package ambit2.rest;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.rest.test.ResourceTest;

public class LauncherTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/launcher",port);
	}
	
	@Test
	public void testPostDataset() throws Exception {
		Form headers = new Form();
		String dataset = String.format("http://localhost:%d/dataset/1", port);
		headers.add(OpenTox.params.dataset_uri.toString(), dataset);
		testAsyncTask(getTestURI(), headers, Status.SUCCESS_OK, String.format(
				"%s?%s=%s",
				dataset, 
				OpenTox.params.feature_uris.toString(),
				Reference.encode(String
						.format("%s/predicted", getTestURI()))));
	}
}
