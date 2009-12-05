package ambit2.rest.test.property;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.w3c.dom.Document;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.rest.property.PropertyDOMParser;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.XMLTags;
import ambit2.rest.test.ResourceTest;

/**
 * test for {@link PropertyResource}
 * @author nina
 *
 */
public class PropertyResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s/1", port,PropertyResource.featuredef);
	}
	
	@Test
	public void testXML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_XML);
	}
	/*
	public boolean verifyResponseRDFXML(String uri, MediaType media, InputStream in)
	throws Exception {
		AbstractRDFParser<Property> p = new AbstractRDFParser<Property>() {
			@Override
			public Property read(OntModel model) throws AmbitException {
				return null;
			}
		};
		
		p.process(in);
		return false;
	}
	*/	
	@Test
	public void testRDFXML() throws Exception {
		testGet(getTestURI(),MediaType.APPLICATION_RDF_XML);
	}	
	/*
<?xml version="1.0" encoding="UTF-8"?><FeatureDefinitions xmlns="http://opentox.org/1.0"><FeatureDefinition ID="1" Name="Property 1" Reference="8" type="TODO"><link href="http://localhost:8181/feature_definition/1"/><Reference xmlns="http://www.opentox.org/Reference/1.0" AlgorithmID="NA" ID="8" Name="Dummy"/></FeatureDefinition></FeatureDefinitions>
	 */
	
	@Override
	public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
			throws Exception {

		Document doc = createDOM(in);
        PropertyDOMParser parser = new PropertyDOMParser() {
        	@Override
        	public void handleItem(Property entry) throws AmbitException {
        		Assert.assertEquals(1,entry.getId());
        		Assert.assertEquals("Property 1",entry.getName());
        		Assert.assertEquals(8,entry.getReference().getId());
        	}
        };
        parser.parse(doc);
        return true;
	}	
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
			Assert.assertEquals("http://localhost:8181/feature_definition/1", line);
			count++;
		}
		return count==1;
	}	
	
	@Test
	public void testCreateEntry() throws Exception {
		Form headers = new Form();  
		headers.add(PropertyResource.headers.name.toString(),"cas");
		headers.add(PropertyResource.headers.reference_id.toString(),"4");
		Response response =  testPost(
					String.format("http://localhost:%d/feature_definition", port),
					MediaType.TEXT_XML,
					headers);
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties where name='cas' and comments='CasRN' and idreference=4");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}	
	
	
	
	@Test
	public void testParser() throws Exception {
		String xml = String.format( 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><FeatureDefinitions xmlns=\"http://opentox.org/1.0\">" +
			"<%s ID=\"1\" %s=\"Property 1\" %s=\"8\" %s=\"Property\">" +
			"<link href=\"http://localhost:8181/feature_definition/1\"/>" +
			"</%s></FeatureDefinitions>",
			XMLTags.node_featuredef,
			XMLTags.attr_name,
			XMLTags.node_reference,
			XMLTags.attr_type,
			XMLTags.node_featuredef,
			
			port,
			XMLTags.node_reference);
			
		final List<Property> le = new ArrayList<Property>();
		Document doc = createDOM(new StringReader(xml));
		PropertyDOMParser parser = new PropertyDOMParser() {
        	@Override
        	public void handleItem(Property entry) throws AmbitException {
        		le.add(entry);

        	}
        };
        parser.parse(doc);
        Assert.assertEquals(1,le.size());
        Assert.assertEquals("Property 1",le.get(0).getName());
        Assert.assertEquals("Property",le.get(0).getLabel());
        Assert.assertEquals(8,le.get(0).getReference().getId());
	}	
}
