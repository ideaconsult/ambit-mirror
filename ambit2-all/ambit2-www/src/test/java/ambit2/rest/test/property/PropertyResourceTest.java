package ambit2.rest.test.property;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.update.property.ReadPropertyByNameAndReference;
import ambit2.rest.property.PropertyDOMParser;
import ambit2.rest.property.PropertyRDFReporter;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.XMLTags;
import ambit2.rest.rdf.OT;
import ambit2.rest.rdf.RDFPropertyIterator;
import ambit2.rest.reference.ReferenceURIReporter;
import ambit2.rest.test.ResourceTest;

import com.hp.hpl.jena.ontology.OntModel;

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

	@Test
	public void testRDFXML() throws Exception {
		RDFPropertyIterator iterator = new RDFPropertyIterator(new Reference(getTestURI()));
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d", port)));
		while (iterator.hasNext()) {
			Property p = iterator.next();
			Assert.assertEquals("Property 1",p.getName());
			Assert.assertEquals(1,p.getId());
		}
		iterator.close();
	}	
	
	@Test
	public void testRDFXMLForeignURI() throws Exception {
		try {		
			RDFPropertyIterator iterator = new RDFPropertyIterator(new Reference("http://google.com"));
			iterator.setBaseReference(new Reference(String.format("http://localhost:%d", port)));
			while (iterator.hasNext()) {
				Property p = iterator.next();
				Assert.assertTrue(false);
			}
			iterator.close();
			Assert.assertTrue(false);
		} catch (Exception x) {
			Assert.assertTrue(true);
		}
	}		
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
			Assert.assertEquals(
					String.format("http://localhost:%d%s/1",port,PropertyResource.featuredef)
							, line);
			count++;
		}
		return count==1;
	}	
	@Test
	public void testCopyEntry() throws Exception {
		
		Form form = new Form();  
		form.add(QueryResource.headers.source_uri.toString(),String.format("http://localhost:%d%s/1", port,PropertyResource.featuredef));
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					form,
					null);
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(3,table.getRowCount());
		c.close();
	}	
	
	public void testCopyEntry1() throws Exception {
		OntModel model = OT.createModel();
		PropertyRDFReporter.addToModel(model, 
				new Property(String.format("http://localhost:%d%s/1", port,PropertyResource.featuredef)),
				new PropertyURIReporter(),
				new ReferenceURIReporter());
		StringWriter writer = new StringWriter();
		model.write(writer,"RDF/XML");
		
		Form form = new Form();  
		form.add(QueryResource.headers.source_uri.toString(),String.format("http://localhost:%d%s/1", port,PropertyResource.featuredef));
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					form,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(3,table.getRowCount());
		c.close();
	}		
	
	@Test
	public void testCreateEntry2() throws Exception {
		
		StringWriter writer = new StringWriter();
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("feature.rdf")));
        String line;
        while ((line = br.readLine()) != null) {
        	writer.append(line);
        }
        br.close();    		
		
		Form form = new Form();
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					form,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where name='cas' and comments='CasRN' and title='http://my.resource.org' and url='http://my.resource.org'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}		
	/**
<pre>
<rdf:RDF
    xmlns:j.0="http://purl.org/net/nknouf/ns/bibtex#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
    xmlns:ot="http://www.opentox.org/api/1.1#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/" > 
  <rdf:Description rdf:about="http://www.opentox.org/api/1.1#units">
    <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#DatatypeProperty"/>
  </rdf:Description>
  <rdf:Description rdf:about="http://purl.org/net/nknouf/ns/bibtex#Entry">
    <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#Class"/>
  </rdf:Description>
  <rdf:Description rdf:about="http://www.opentox.org/api/1.1#hasSource">
    <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#ObjectProperty"/>
  </rdf:Description>
  <rdf:Description rdf:nodeID="A0">
    <rdfs:seeAlso>bbb</rdfs:seeAlso>
    <dc:title>aaa</dc:title>
    <rdf:type rdf:resource="http://purl.org/net/nknouf/ns/bibtex#Entry"/>
  </rdf:Description>
  <rdf:Description rdf:about="http://www.opentox.org/api/1.1#Feature">
    <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#Class"/>
  </rdf:Description>
  <rdf:Description rdf:nodeID="A1">
    <dc:type>http://www.w3.org/2001/XMLSchema#string</dc:type>
    <ot:hasSource rdf:nodeID="A0"/>
    <owl:sameAs>CasRN</owl:sameAs>
    <ot:units></ot:units>
    <dc:title>cas</dc:title>
    <rdf:type rdf:resource="http://www.opentox.org/api/1.1#Feature"/>
  </rdf:Description>
</rdf:RDF>

</pre>
	 * @throws Exception
	 */
	@Test
	public void testCreateEntry() throws Exception {

		OntModel model = OT.createModel();
		PropertyRDFReporter.addToModel(model, 
				new Property("cas",new LiteratureEntry("aaa","bbb")),
				new PropertyURIReporter(),
				new ReferenceURIReporter());
		StringWriter writer = new StringWriter();
		model.write(writer,"RDF/XML");

		Form form = new Form();
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.APPLICATION_RDF_XML,
					form,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where name='cas' and comments='http://www.opentox.org/api/1.1#CasRN' and title='aaa' and url='bbb'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}	
	
	@Test
	public void testCreateForeignEntry() throws Exception {
		Form form = new Form();  
		form.add(QueryResource.headers.source_uri.toString(),"http://my.new.property.org");
		
		
		Response response =  testPost(
					String.format("http://localhost:%d%s", port,PropertyResource.featuredef),
					MediaType.TEXT_RDF_N3,
					form,
					null);
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM properties join catalog_references using(idreference) where name='http://my.new.property.org' and comments='http://my.new.property.org' and title='http://my.new.property.org' and url='http://my.new.property.org'");
		Assert.assertEquals(1,table.getRowCount());

	}

	@Test
	public void testParser() throws Exception {
		String xml = String.format( 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><FeatureDefinitions xmlns=\"http://opentox.org/1.0\">" +
			"<%s ID=\"1\" %s=\"Property 1\" %s=\"8\" %s=\"Property\">" +
			"<link href=\"http://localhost:%d%s/1\"/>" +
			"</%s></FeatureDefinitions>",
			XMLTags.node_featuredef,
			XMLTags.attr_name,
			XMLTags.node_reference,
			XMLTags.attr_type,
			port,
			PropertyResource.featuredef,			
			XMLTags.node_featuredef,
			//?
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
	@Test
	public void testGetJavaObject() throws Exception {
		testGetJavaObject(String.format("http://localhost:%d%s/%s", port,PropertyResource.featuredef,Reference.encode("http://other.com/feature/200Default")),
				MediaType.APPLICATION_JAVA_OBJECT,org.restlet.data.Status.SUCCESS_OK);
	}
	
	@Override
	public Object verifyResponseJavaObject(String uri, MediaType media,
			Representation rep) throws Exception {
		Object o = super.verifyResponseJavaObject(uri, media, rep);
		Assert.assertTrue(o instanceof ReadPropertyByNameAndReference);
		ReadPropertyByNameAndReference query = (ReadPropertyByNameAndReference)o;
		Assert.assertEquals("http://other.com/feature/200Default",query.getValue());
		return o;
	}
	
}
