package ambit2.rest.test.structure;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.test.ResourceTest;

public class MalformedRequestTest  extends ResourceTest {

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/compound/AAA", port);
	}
	@Test
	public void testInvalidID() throws Exception {
		Status status = testHandleError(String.format("http://localhost:%d/compound/AAA", port),MediaType.TEXT_URI_LIST);
		Assert.assertEquals(400,status.getCode());
		Assert.assertEquals("Invalid resource id AAA",status.getDescription());
	}
	@Test
	public void testNotFound() throws Exception {
		Status status = testHandleError(String.format("http://localhost:%d/compound/99999999", port),MediaType.TEXT_URI_LIST);
		Assert.assertEquals(Status.CLIENT_ERROR_NOT_FOUND.getCode(),status.getCode());
		Assert.assertEquals("Query returns no results! idcompound=99999999",status.getDescription());
	}
	
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			//System.out.println(line);
			count++;
		}
		return count >0;
	}		
	@Test
	public void testGetJavaObject() throws Exception {
		try {
			testGetJavaObject(getTestURI(),MediaType.APPLICATION_JAVA_OBJECT,org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (ResourceException x) {
			Assert.assertEquals(Status.CLIENT_ERROR_BAD_REQUEST,x.getStatus());
		}
	}
}
