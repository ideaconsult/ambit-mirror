package ambit2.rest.test.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.rest.model.ModelResource;
import ambit2.rest.test.ResourceTest;

public class ModelResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s/1", port,ModelResource.resource);
	}
	/*
	@Test
	public void testXML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_XML);
	}
	@Override
	public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			System.out.println(line);
			count++;
		}
		return count>0;
	}	
	*/
	@Test
	public void testHTML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_HTML);
	}
	@Override
	public boolean verifyResponseHTML(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			//Assert.assertEquals("1530-32-1 ", line);
			System.out.println(line);
			count++;
		}
		return count>1;
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
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			Assert.assertEquals(String.format("http://localhost:%d/model/1",port), line);
			count++;
		}
		return count==1;
	}
	@Test
	public void testPostDataset() throws Exception {
		Form headers = new Form();  
		//headers.add("dataset-id", "1");
		String dataset = String.format("http://localhost:%d/dataset/1",port);
		testAsyncTask(
				String.format("%s?%s=%s",getTestURI(),ModelResource.dataset_uri,Reference.encode(dataset)),
				headers, Status.SUCCESS_OK, 
				String.format("%s?feature_uris[]=%s",
						dataset,
						Reference.encode(String.format("%s/predicted",getTestURI()))
				));
	}
	
	@Test
	public void testPostCompound() throws Exception {
		Form headers = new Form();  
		//headers.add("dataset-id", "1");
		String dataset = String.format("http://localhost:%d/compound/11",port);
		testAsyncTask(
				String.format("%s?%s=%s",getTestURI(),ModelResource.dataset_uri,Reference.encode(dataset)),
				headers, Status.SUCCESS_OK, 
				String.format("%s?feature_uris[]=%s",
						dataset,
						Reference.encode(String.format("%s/predicted",getTestURI()))
				));
	}	
}