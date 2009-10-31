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
import ambit2.db.readers.PropertyValue;
import ambit2.rest.property.PropertyDOMParser;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.propertyvalue.FeatureResource;
import ambit2.rest.propertyvalue.PropertyValueDOMParser;
import ambit2.rest.propertyvalue.PropertyValueResource;
import ambit2.rest.query.XMLTags;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.test.ResourceTest;

public class FeatureResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s%s/%d%s/%d", port,
				PropertyValueResource.featureKey,
				CompoundResource.compound,
				11,
				PropertyResource.featuredef,
				3);
	}
	///feature/compound/{idcompound}/feature_definition/{id_feature_definition}
	@Test
	public void testXML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_XML);
	}
	/*
<?xml version="1.0" encoding="UTF-8"?><Features xmlns="http://opentox.org/Feature/1.0"><Feature CompoundID="11" ID="1" Name="CAS" Value="1530-32-1"/></Features>
	 */
	
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
			Assert.assertEquals("http://localhost:8181/feature/compound/11/feature_definition/3", line);
			count++;
		}
		return count==1;
	}	
	
	@Test
	public void testTXT() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	@Override
	public boolean verifyResponseTXT(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			Assert.assertEquals("1530-32-1", line);
			count++;
		}
		return count==1;
	}	
	
	@Test
	public void testCreateEntry() throws Exception {
		Form headers = new Form();  
		headers.add(FeatureResource.headers.value.toString(),"XXXXXX");
		
		Response response =  testPost(
					String.format("http://localhost:%d/feature/compound/7/conformer/100211/feature_definition/2", port),
					MediaType.TEXT_XML,
					headers);
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT value FROM property_values join property_string using(idvalue_string) where idstructure=100211 and idproperty=2");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals("XXXXXX",table.getValue(0,"value"));
		c.close();
	}	
	
	@Test
	public void testCreateEntryNum() throws Exception {
		Form headers = new Form();  
		headers.add(FeatureResource.headers.value.toString(),"3.14");
		
		Response response =  testPost(
					String.format("http://localhost:%d/feature/compound/7/conformer/100211/feature_definition/2", port),
					MediaType.TEXT_XML,
					headers);
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT value_num FROM property_values where idstructure=100211 and idproperty=2");
		Assert.assertEquals(1,table.getRowCount());
		Assert.assertEquals(3.14,table.getValue(0,"value_num"));
		c.close();
	}		
	
	@Test
	public void testParserReferenceChild() throws Exception {
		String xml = String.format( 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><FeatureDefinitions xmlns=\"http://opentox.org/1.0\">" +
			"<%s ID=\"1\" %s=\"Property 1\" %s=\"8\" %s=\"Property\">" +
			"<link href=\"http://localhost:8181/feature_definition/1\"/>" +
			"<Reference xmlns=\"http://www.opentox.org/Reference/1.0\" AlgorithmID=\"NA\" ID=\"8\" Name=\"Dummy\"/>" +
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
