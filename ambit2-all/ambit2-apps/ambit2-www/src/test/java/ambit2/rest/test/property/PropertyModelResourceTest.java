package ambit2.rest.test.property;

import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.test.ResourceTest;

public class PropertyModelResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/model/1/predicted", port);
	}
	protected void setDatabase() throws Exception {
		setUpDatabase("src/test/resources/src-datasets_model.xml");
	}
	@Test
	public void testRDFXML() throws Exception {
		testGet(getTestURI(),MediaType.APPLICATION_RDF_XML);
	}	
	
	@Test
	public void testRDFXML_dependent() throws Exception {
		testGet(
				String.format("http://localhost:%d/model/1/dependent", port)
				,MediaType.APPLICATION_RDF_XML);
	}
	@Test
	public void testRDFXML_independent() throws Exception {
		testGet(
				String.format("http://localhost:%d/model/1/independent", port)
				,MediaType.APPLICATION_RDF_XML);
	}		
	
}
