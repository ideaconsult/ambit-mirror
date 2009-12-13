package ambit2.rest.test.algorithm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import ambit2.rest.test.ResourceTest;

public class AlgorithmResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/algorithm", port);
	}
	@Test
	public void testRDFXML() throws Exception {
		testGet(getTestURI(),MediaType.APPLICATION_RDF_XML);
	}	
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return count == 9;
	}	
	
	@Test
	public void testHTML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_HTML);
	}
	@Override
	public boolean verifyResponseHTML(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
		//	System.out.println(line);
			count++;
		}
		return count > 0;
	}	
	@Test
	public void testPost() throws Exception {
		Form headers = new Form();  
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreecramer", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"ToxTree%3A+Cramer+rules"));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreecramer2", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/3", port));
		
	}
	@Override
	public void testGetJavaObject() throws Exception {
	}
}
