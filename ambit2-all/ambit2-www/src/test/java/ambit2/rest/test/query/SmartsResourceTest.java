package ambit2.rest.test.query;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.test.ResourceTest;

public class SmartsResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/query/smarts/C=O", port);
	}
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = r.readLine())!= null) {
			System.out.println(line);
		}
		return true;
	}
}
