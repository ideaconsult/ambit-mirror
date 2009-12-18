package ambit2.rest.test.reference;

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
import org.restlet.data.Reference;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.w3c.dom.Document;

import com.hp.hpl.jena.rdf.model.Resource;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.rest.query.XMLTags;
import ambit2.rest.reference.RDFReferenceParser;
import ambit2.rest.reference.ReferenceDOMParser;
import ambit2.rest.reference.ReferenceResource;
import ambit2.rest.test.ResourceTest;

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
	public void testXML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_XML);
	}
	
	@Test
	public void testRDFXML() throws Exception {
		RDFReferenceParser parser = new RDFReferenceParser(String.format("http://localhost:%d",port));
		parser.setProcessorChain(new ProcessorsChain<ILiteratureEntry, IBatchStatistics, IProcessor>());
		parser.getProcessorChain().add(new IProcessor<ILiteratureEntry,ILiteratureEntry>(){
			public long getID() {
				return 0;
			}
			public ILiteratureEntry process(ILiteratureEntry target) throws AmbitException {
				Assert.assertEquals("CAS Registry Number", target.getName());
				Assert.assertEquals("http://www.cas.org",target.getURL());
				Assert.assertEquals(1,target.getId());
				return target;
			}
			public boolean isEnabled() {
				return true;
			}
			public void setEnabled(boolean value) {
			}
		});
		parser.process(new Reference(getTestURI()));
	}
	
	@Test
	public void testRDFXMLForeignURI() throws Exception {
		RDFReferenceParser parser = new RDFReferenceParser(String.format("http://localhost:%d",port)) {
			@Override
			protected ILiteratureEntry parseRecord(Resource newEntry,
					ILiteratureEntry record) {
				return super.parseRecord(newEntry, record);
			}
		};
		try {
			parser.process(new Reference("http://google.com"));
			Assert.assertTrue(false);
		} catch (Exception x) {
			Assert.assertTrue(true);
		}
	}
	
	@Override
	public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
			throws Exception {

		Document doc = createDOM(in);
        ReferenceDOMParser parser = new ReferenceDOMParser() {
        	@Override
        	public void handleItem(LiteratureEntry entry) throws AmbitException {
        		Assert.assertEquals(1,entry.getId());
        		Assert.assertEquals("CAS Registry Number",entry.getName());
        		Assert.assertEquals("http://www.cas.org",entry.getURL());
        		//count++;
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
	public void testCreateEntry() throws Exception {
		Form headers = new Form();  
		headers.add(ReferenceResource.headers.name.toString(),"My New Name");
		headers.add(ReferenceResource.headers.algorithm_id.toString(),"My New Algorithm");
		Response response =  testPost(
					String.format("http://localhost:%d/reference", port),
					MediaType.TEXT_XML,
					headers);
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM catalog_references where title='My New Name' and url='My New Algorithm'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}
}
