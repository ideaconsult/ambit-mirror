package ambit2.rest.test.algorithm;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.rest.rdf.RDFPropertyIterator;
import ambit2.rest.test.ResourceTest;

public class OnlineStructureFinderTest  extends ResourceTest {
	@Override
	protected void setDatabase() throws Exception {
		setUpDatabase("src/test/resources/src-datasets.xml");
	}
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/algorithm/finder", port);
	}
	
	@Test
	public void testFinder() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		String feature = String.format("http://localhost:%d/feature/3", port);
		headers.add("feature_uris[]",feature);
		Reference ref = testAsyncTask(
				String.format("http://localhost:%d/algorithm/finder", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/1?%s=%s", port,
						Reference.encode("feature_uris[]"),
						Reference.encode(feature)
						));

		int count = 0;
		RDFPropertyIterator i = new RDFPropertyIterator(ref);
		i.setCloseModel(true);
		while (i.hasNext()) {
			count++;
		}
		i.close();
		Assert.assertEquals(1,count);
	}	
}
