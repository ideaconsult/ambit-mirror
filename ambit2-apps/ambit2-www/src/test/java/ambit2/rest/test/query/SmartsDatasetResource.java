package ambit2.rest.test.query;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.rest.test.ResourceTest;

public class SmartsDatasetResource extends ResourceTest {

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/dataset/1/smarts?search=%s", 
				port,
				Reference.encode("c1ccccc1"));
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
		int count = 0;
		while ((line = r.readLine())!= null) {
			Assert.assertEquals("http://localhost:8181/compound/11/conformer/100215",line);
			count++;
		}
		return count == 1;
	}
}
