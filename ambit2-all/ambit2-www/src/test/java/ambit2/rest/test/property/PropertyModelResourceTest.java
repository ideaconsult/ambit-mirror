package ambit2.rest.test.property;

import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.property.PropertyResource;
import ambit2.rest.test.ResourceTest;

public class PropertyModelResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s/model/1/predicted", port,PropertyResource.featuredef);
	}
	@Test
	public void testRDFXML() throws Exception {
		testGet(getTestURI(),MediaType.APPLICATION_RDF_XML);
	}	
	
	@Test
	public void testRDFXML_dependent() throws Exception {
		testGet(
				String.format("http://localhost:%d%s/model/1/dependent", port,PropertyResource.featuredef)
				,MediaType.APPLICATION_RDF_XML);
	}
	@Test
	public void testRDFXML_independent() throws Exception {
		testGet(
				String.format("http://localhost:%d%s/model/1/independent", port,PropertyResource.featuredef)
				,MediaType.APPLICATION_RDF_XML);
	}		
	
}
