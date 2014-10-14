package ambit2.rest.test.dataset;

import junit.framework.Assert;

import org.junit.Test;
import org.opentox.dsl.OTObject;

import ambit2.rest.OpenTox;
import ambit2.rest.test.ResourceTest;

public class ResourceIntersectionCounterTest extends ResourceTest {

	@Override
	public String getTestURI() {
		return String.format(
				"http://localhost:%d/stats?%s=http://localhost:%d/dataset/1&%s=http://localhost:%d/dataset/2",
					port,OpenTox.params.dataset_uri,port,OpenTox.params.dataset_uri,port);
	}

	@Test
	public void testDatasetsIntersection() throws Exception {
		OTObject o = OTObject.object(getTestURI());
		o.readTextLineAsName();
		Assert.assertEquals("2", o.getName());
	}
	
	@Test
	public void testDatasetAndQueryIntersection() throws Exception {
		String uri = String.format(
				"http://localhost:%d/stats?%s=http://localhost:%d/dataset/1&%s=http://localhost:%d/dataset/R2",
					port,OpenTox.params.dataset_uri,port,OpenTox.params.dataset_uri,port);		
		OTObject o = OTObject.object(uri);
		o.readTextLineAsName();
		Assert.assertEquals("1", o.getName());
	}	
	@Test
	public void testQueryIntersection() throws Exception {
		String uri = String.format(
				"http://localhost:%d/stats?%s=http://localhost:%d/dataset/R1&%s=http://localhost:%d/dataset/R2",
					port,OpenTox.params.dataset_uri,port,OpenTox.params.dataset_uri,port);		
		OTObject o = OTObject.object(uri);
		o.readTextLineAsName();
		Assert.assertEquals("0", o.getName());
	}	
}
