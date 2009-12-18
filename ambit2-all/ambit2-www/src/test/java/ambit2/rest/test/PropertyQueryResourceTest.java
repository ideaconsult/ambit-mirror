package ambit2.rest.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.propertyvalue.PropertyValueResource;
import ambit2.rest.query.PropertyQueryResource;

/**
 * Test for {@link PropertyQueryResource}
 * @author nina
 *
 */
public class PropertyQueryResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/query%s/like/1530-32-1", port,PropertyValueResource.featureKey);
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
			Assert.assertEquals(String.format("http://localhost:8181/compound/11/conformer/100215",port), line);
			count++;
		}
		return count==1;
	}
}
