package ambit2.rest.test.bundle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.test.ProtectedResourceTest;

public class BundleSummaryResourceTest extends ProtectedResourceTest {
    protected String dbFile = "src/test/resources/descriptors-datasets.xml";

    @Override
    public String getTestURI() {
	return String.format("http://localhost:%d/bundle/1/summary", port);
    }

    @Test
    public void testJSON() throws Exception {
	setUpDatabase(dbFile);
	testGet(getTestURI(), MediaType.APPLICATION_JSON);
    }

    @Override
    public boolean verifyResponseJSON(String uri, MediaType media, InputStream in) throws Exception {
	// todo parse json
	BufferedReader reader = new BufferedReader(new InputStreamReader(in));

	String line = null;
	int count = 0;
	while ((line = reader.readLine()) != null) {
	    System.out.println(line);
	    count++;
	}
	return count == 1;
    }
}