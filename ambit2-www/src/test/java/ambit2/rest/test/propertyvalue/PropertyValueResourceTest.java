package ambit2.rest.test.propertyvalue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.rest.propertyvalue.PropertyValueResource;
import ambit2.rest.test.ResourceTest;

/**
 * Test for {@link PropertyValueResource}
 * @author nina
 *
 */
public class PropertyValueResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/compound/11%s/%s", 
				port,
				PropertyValueResource.featureKey,
				Reference.encode(Property.opentox_CAS));
	}

	@Test
	public void testRDFTurtle() throws Exception {
		testGet(getTestURI(),MediaType.APPLICATION_RDF_TURTLE);
	}	
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
	/*
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
	public void testTXT() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_PLAIN);
	}
	@Override
	public boolean verifyResponseTXT(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			Assert.assertEquals("http://www.opentox.org/api/1.1#CASRN = 1530-32-1", line);
			count++;
		}
		return count==1;
	}
}
