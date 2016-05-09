package ambit2.rest.test.bundle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.codehaus.jackson.JsonNode;
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