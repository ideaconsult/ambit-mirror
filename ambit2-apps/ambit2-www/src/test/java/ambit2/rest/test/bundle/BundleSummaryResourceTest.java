package ambit2.rest.test.bundle;


import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.test.ProtectedResourceTest;

public class BundleSummaryResourceTest extends ProtectedResourceTest {
	protected String dbFile = "descriptors-datasets.xml";

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/bundle/1/summary", port);
	}

	@Test
	public void testJSON() throws Exception {
		setUpDatabaseFromResource(dbFile);
		testGet(getTestURI(), MediaType.APPLICATION_JSON);
	}

}