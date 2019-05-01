package ambit2.rest.test.assaytemplate;

import java.io.InputStream;

import org.junit.Test;
import org.restlet.data.MediaType;

import com.fasterxml.jackson.databind.JsonNode;

import ambit2.rest.substance.templates.AssayTemplatesFacetResource;
import ambit2.rest.test.ResourceTest;

public class AssayTemplateResourceTest extends ResourceTest {

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/%s", port, AssayTemplatesFacetResource.assaytemplate_facet);
	}

	@Test
	public void testJson() throws Exception {
		testGet(getTestURI(), MediaType.APPLICATION_JSON);
	}

	@Override
	public JsonNode parseResponseJSON(String uri, MediaType media, InputStream in) throws Exception {
		JsonNode node = super.parseResponseJSON(uri, media, in);
		System.out.println(node);
		return node;
	}
	
	@Override
	public void testGetJavaObject() throws Exception {

	}
}
