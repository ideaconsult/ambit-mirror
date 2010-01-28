package ambit2.rest.test.reference;

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
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OpenTox;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.XMLTags;
import ambit2.rest.rdf.OT;
import ambit2.rest.rdf.RDFReferenceIterator;
import ambit2.rest.reference.ReferenceDOMParser;
import ambit2.rest.reference.ReferenceRDFReporter;
import ambit2.rest.reference.ReferenceURIReporter;
import ambit2.rest.test.ResourceTest;

import com.hp.hpl.jena.ontology.OntModel;

public class ReferenceResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/reference/1", port);
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
			System.out.println(line);
			count++;
		}
		return count>0;
	}		

	
	@Test
	public void testRDFXML() throws Exception {
		RDFReferenceIterator iterator = new RDFReferenceIterator(new Reference(getTestURI()));
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d",port)));
		while (iterator.hasNext()) {
			ILiteratureEntry target = iterator.next();
			Assert.assertEquals("CAS Registry Number", target.getName());
			Assert.assertEquals("http://www.cas.org",target.getURL());
			Assert.assertEquals(1,target.getId());			
		}
		iterator.close();
	}
	
	@Test
	public void testRDFXMLForeignURI() throws Exception {
		try {
			RDFReferenceIterator iterator = new RDFReferenceIterator(new Reference("http://google.com"));
			iterator.setBaseReference(new Reference(String.format("http://localhost:%d",port)));
			while (iterator.hasNext()) {
				ILiteratureEntry target = iterator.next();
				Assert.assertEquals("CAS Registry Number", target.getName());
				Assert.assertEquals("http://www.cas.org",target.getURL());
				Assert.assertEquals(1,target.getId());			
			}
			iterator.close();
			Assert.assertTrue(false);
		} catch (Exception x) {
			Assert.assertTrue(true);
		}
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
			Assert.assertEquals(String.format("http://localhost:%d/reference/1",port), line);
			count++;
		}
		return count==1;
	}
	/*
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
			Assert.assertEquals("1530-32-1 ", line);
			count++;
		}
		return count==1;
	}
	*/
	@Test
	public void testParser() throws Exception {
		String xml = String.format( 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><References xmlns=\"http://www.opentox.org/Reference/1.0\">"+
			"<%s %s=\"http://www.cas.org\" %s=\"1\" %s=\"CAS Registry Number\">"+
			"<link xmlns=\"http://opentox.org/1.0\" %s=\"http://localhost:%d/reference/1\"/>" +
			"</%s>" +
			"</References>",
			XMLTags.node_reference,
			XMLTags.attr_algorithm,
			XMLTags.attr_id,
			XMLTags.attr_name,
			XMLTags.attr_href,
			port,
			XMLTags.node_reference);
			
		final List<LiteratureEntry> le = new ArrayList<LiteratureEntry>();
		Document doc = createDOM(new StringReader(xml));
		ReferenceDOMParser parser = new ReferenceDOMParser() {
        	@Override
        	public void handleItem(LiteratureEntry entry) throws AmbitException {
        		le.add(entry);

        	}
        };
        parser.parse(doc);
        Assert.assertEquals(1,le.size());
	}
	@Test
	public void testCreateForeignEntry() throws Exception {
		Form form = new Form();  
		form.add(OpenTox.params.source_uri.toString(),"http://my.new.algorithm.org");
		
		
		Response response =  testPost(
					String.format("http://localhost:%d/reference", port),
					MediaType.TEXT_RDF_N3,
					form.getWebRepresentation());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM catalog_references where title='http://my.new.algorithm.org' and url='http://my.new.algorithm.org'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}
	
	@Test
	public void testCreateEntry() throws Exception {
		OntModel model = OT.createModel();
		ReferenceRDFReporter.addToModel(model, new LiteratureEntry("aaa","bbb"), new ReferenceURIReporter<IQueryRetrieval<ILiteratureEntry>>());
		StringWriter writer = new StringWriter();
		model.write(writer,"RDF/XML");
		model.close();

		Response response =  testPost(
					String.format("http://localhost:%d/reference", port),
					MediaType.APPLICATION_RDF_XML,
					writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM catalog_references where title='CAS Registry Number' and url='http://www.cas.org'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}		
	@Test
	public void testCopyEntry() throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM catalog_references where title='CAS Registry Number' and url='http://www.cas.org'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.source_uri.toString(),String.format("http://localhost:%d/reference/1", port));
		
		Response response =  testPost(
					String.format("http://localhost:%d/reference", port),
					MediaType.APPLICATION_RDF_XML,
					form.getWebRepresentation());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
         c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM catalog_references where title='CAS Registry Number' and url='http://www.cas.org'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}	
}
