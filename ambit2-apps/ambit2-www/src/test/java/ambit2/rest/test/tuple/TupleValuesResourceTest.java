package ambit2.rest.test.tuple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.test.ResourceTest;

public class TupleValuesResourceTest extends ResourceTest {

	
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/compound/11/dataEntry/1", port);
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
		return count ==1;
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
			System.out.println(line);
			count++;
		}
		return count >0;
	}	

	@Test
	public void testRDFTurtle() throws Exception {
		testGet(getTestURI(),MediaType.APPLICATION_RDF_TURTLE);
	}
	@Test
	public void testRDFXML() throws Exception {
		testGet(getTestURI(),MediaType.APPLICATION_RDF_XML);
	}		
	/*
	@Override
	public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		//<?xml version="1.0" encoding="UTF-8"?><features xmlns="http://opentox.org/Feature/1.0"><feature CompoundID="11" ConformerID="100215" value="1530-32-1"/></features>
		throw new Exception("TODO: Parse XML and verify values");
	}		
	*/
	/*
	@Override
	public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
			throws Exception {

		Document doc = createDOM(in);
		PropertyValueDOMParser parser = new PropertyValueDOMParser() {
        	@Override
        	public void handleItem(PropertyValue entry) throws AmbitException {
        		Assert.assertEquals(3,entry.getProperty().getId());
        		Assert.assertEquals("CAS",entry.getProperty().getName());
        		Assert.assertEquals("1530-32-1",entry.getValue());
        	}
        };
        parser.parse(doc);
        return true;
	}	
	*/
	
}
