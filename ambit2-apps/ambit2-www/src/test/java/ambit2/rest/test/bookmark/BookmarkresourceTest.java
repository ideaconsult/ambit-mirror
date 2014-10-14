package ambit2.rest.test.bookmark;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.base.data.Bookmark;
import ambit2.rest.OpenTox;
import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.bookmark.BookmarkResource;
import ambit2.rest.rdf.Annotea;
import ambit2.rest.rdf.RDFBookmarkIterator;
import ambit2.rest.test.ProtectedResourceTest;

import com.hp.hpl.jena.vocabulary.DC;

public class BookmarkresourceTest extends ProtectedResourceTest {
	
	@Override
	protected boolean isAAEnabled() {
		//no access to bookmarks without AA
		return true;
	}
	
	@Override
	public void setUpDatabase(String xmlfile) throws Exception {
		super.setUpDatabase(xmlfile);
		if (!"test".equals(getCreator())) {
			IDatabaseConnection c = getConnection();	
			PreparedStatement st =null;
			try {
		        st = c.getConnection().prepareStatement("update bookmark set creator=? where idbookmark=1");
		        st.setString(1,OpenSSOServicesConfig.getInstance().getTestUser());
				st.execute();
					
			} catch (Exception x) {
				
			} finally {
				try {st.close();} catch (Exception x) {}
				try {c.close();} catch (Exception x) {}
			}
		}
	}
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/bookmark/%s/entries/1", port,getCreator());
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
	public void testCSV() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_CSV);
	}
	@Override
	public boolean verifyResponseCSV(String uri, MediaType media, InputStream in)
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
		RDFBookmarkIterator iterator = new RDFBookmarkIterator(new Reference(getTestURI()));
		iterator.setBaseReference(new Reference(String.format("http://localhost:%d",port)));
		while (iterator.hasNext()) {
			Bookmark target = iterator.next();
			Assert.assertEquals("my model", target.getTitle());
			Assert.assertEquals("http://example.com",target.getRecalls());
			Assert.assertEquals(getCreator(),target.getCreator());
			Assert.assertEquals(1,target.getId());			
		}
		iterator.close();
	}
	
	@Test
	public void testRDFXMLForeignURI() throws Exception {
		try {
			RDFBookmarkIterator iterator = new RDFBookmarkIterator(new Reference("http://google.com"));
			iterator.setBaseReference(new Reference(String.format("http://localhost:%d",port)));
			while (iterator.hasNext()) {
				Bookmark target = iterator.next();
				Assert.assertEquals("my model", target.getTitle());
				Assert.assertEquals("http://example.com",target.getRecalls());
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
			Assert.assertEquals(String.format("http://localhost:%d/bookmark/%s/entries/1",port,getCreator()), line);
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
	public void testCreateEntry() throws Exception {
		/*
		OntModel model = OT.createModel();
		Bookmark bookmark = new Bookmark();
		bookmark.setCreator("test");
		bookmark.setDescription("description");
		bookmark.setHasTopic("hasTopic");
		bookmark.setTitle("title");
		
		BookmarkRDFReporter.addToModel(model, bookmark, new BookmarkURIReporter<IQueryRetrieval<Bookmark>>());
		StringWriter writer = new StringWriter();
		model.write(writer,"RDF/XML");
		model.close();
		*/
		Form form = new Form();
		form.add(Annotea.BookmarkProperty.hasTopic.toString(),"Model");
		form.add(Annotea.BookmarkProperty.recalls.toString(),"http://example.com");
		form.add(DC.creator.toString(),getCreator());
		form.add(DC.title.toString(),"title");
		form.add(DC.description.toString(),"description");
		
		Response response =  testPost(
					String.format("http://localhost:%d%s/%s", port,BookmarkResource.resource,getCreator()),
					//MediaType.APPLICATION_RDF_XML,
					MediaType.APPLICATION_WWW_FORM,
					form.getWebRepresentation());
					//writer.toString());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
		Assert.assertEquals(String.format("http://localhost:%d/bookmark/%s/entries/2",port,getCreator()),
				
				response.getEntityAsText());
        
		IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				String.format(
				"SELECT * FROM bookmark where title='title' and recalls='http://example.com' and creator='%s' and hasTopic='Model'",
				getCreator()
						));
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}		
	@Test
	public void testCopyEntry() throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM bookmark where title='my model' and recalls='http://example.com'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
		String bookmark = String.format("http://localhost:%d%s/%s/entries/1", port,BookmarkResource.resource,getCreator());
		Form form = new Form();  
		form.add(OpenTox.params.source_uri.toString(),bookmark);
		
		Response response =  testPost(
					String.format("http://localhost:%d%s/%s", port,BookmarkResource.resource,getCreator()),
					MediaType.APPLICATION_RDF_XML,
					form.getWebRepresentation());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
         c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM bookmark where title='my model' and recalls='http://example.com'");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
	}
	@Test
	public void testDeleteEntry() throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM bookmark where title='my model' and recalls='http://example.com'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
		Form form = new Form();
		Response response =  testDelete(
					String.format("http://localhost:%d%s/%s/entries/1", port,BookmarkResource.resource,getCreator()),
					MediaType.APPLICATION_RDF_XML,
					form.getWebRepresentation());
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
         c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM bookmark where title='my model' and recalls='http://example.com'");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
	}
	
	@Test
	public void testSearchTopic() throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM bookmark where hasTopic='Model'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
		String bookmark = String.format("http://localhost:%d%s/%s?hasTopic=Model", port,BookmarkResource.resource,getCreator());
		testGet(bookmark,MediaType.TEXT_HTML);
		
         c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM bookmark where title='my model' and recalls='http://example.com'");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
	}	
	
	@Test
	public void testSearchTopicNone() throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM bookmark where hasTopic='ZZZZZZZ'");
		Assert.assertEquals(0,table.getRowCount());
		c.close();
		
		String bookmark = String.format("http://localhost:%d%s/%s?hasTopic=ZZZZZZZ", port,BookmarkResource.resource,getCreator());
		testGet(bookmark,MediaType.TEXT_HTML);

		c.close();
	}	
}
