package ambit2.rest.test.query;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.rest.OpenTox;
import ambit2.rest.test.ResourceTest;

public class QueryResultsResourceTest extends ResourceTest {
	
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/dataset/R1", port);
	}
	@Test
	public void testPostNewDataset() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/2", port));
		
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.POST,
				new Reference(String.format("http://localhost:%d/dataset/R3", port)));
		
         c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query_results join query using(idquery) join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(4,table.getRowCount());		

		table = 	c.createQueryTable("EXPECTED","SELECT idquery,idtemplate FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(3,table.getRowCount());
		Assert.assertNotNull(table.getValue(0,"idtemplate"));

	
		c.close();
		
	}
	
	@Test
	public void testPostNewQueryResult() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/R1", port));
		
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.POST,
				new Reference(String.format("http://localhost:%d/dataset/R3", port)));

	
         c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(3,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query_results join query using(idquery) join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(3,table.getRowCount());		
		c.close();
		
		
	}	
	
	public String getSimilarityURI() {
		return String.format("http://localhost:%d/query/similarity?search=%s&threshold=0.16&feature_uris[]=http://localhost:%d/dataset/2/feature", 
				port,
				Reference.encode("c1ccccc1"),
				port
				);
	}
	
	
	@Test
	public void testPostQuery() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),getSimilarityURI());
		
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.POST,
				new Reference(String.format("http://localhost:%d/dataset/R3", port)));

		
         c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(3,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query_results join query using(idquery) join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(4,table.getRowCount());		
		table = 	c.createQueryTable("EXPECTED","SELECT idquery,idtemplate FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(3,table.getRowCount());
		Assert.assertNotNull(table.getValue(0,"idtemplate"));		
		c.close();
		
	}	
	@Test
	public void testPostSame() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query_results join query using(idquery) join sessions using(idsessions) where idquery=1");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/R1", port));
		
		
		
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset/R1", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.POST,
				new Reference(String.format("http://localhost:%d/dataset/R1", port)));
		
         c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) ");
		Assert.assertEquals(2,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query_results join query using(idquery) join sessions using(idsessions) where idquery=2");
		Assert.assertEquals(1,table.getRowCount());		
		c.close();
		
	}		
	@Test
	public void testPostNewQueryResult1() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query_results join query using(idquery) join sessions using(idsessions) where idquery=2");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/R1", port));
		
	
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset/R2", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.POST,
				new Reference(String.format("http://localhost:%d/dataset/R2", port)));

		
         c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) ");
		Assert.assertEquals(2,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query_results join query using(idquery) join sessions using(idsessions) where idquery=2");
		Assert.assertEquals(1,table.getRowCount());		
		c.close();
		
	}		
	
	@Test
	public void testAddtoExistingQueryResult() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/3", port));
		
	
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset/R1", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.PUT,
				new Reference(String.format("http://localhost:%d/dataset/R1", port)));

		
         c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where idquery=1");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query_results join query using(idquery) join sessions using(idsessions) where idquery=1");
		Assert.assertEquals(3,table.getRowCount());		
		c.close();
		
	}	
	
	@Test
	public void testReplaceQueryResult() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/3", port));
		
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset/R1", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.POST,
				new Reference(String.format("http://localhost:%d/dataset/R1", port)));
		
		
         c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where idquery=1");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query_results join query using(idquery) join sessions using(idsessions) where idquery=1");
		Assert.assertEquals(2,table.getRowCount());		
		c.close();
		
	}	
	
	@Test
	public void testAddQueryResult() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/3", port));
		
	
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset/R1", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.PUT,
				new Reference(String.format("http://localhost:%d/dataset/R1", port)));

		
         c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where idquery=1");
		Assert.assertEquals(1,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query_results join query using(idquery) join sessions using(idsessions) where idquery=1");
		Assert.assertEquals(3,table.getRowCount());		
		c.close();
		
	}	
	
	@Test
	public void testPostDataset() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/1", port));
		

		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset/1", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.POST,
				new Reference(String.format("http://localhost:%d/dataset/1", port)));

		
	}		
	
	@Test
	public void testPutConformer2Dataset() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset where id_srcdataset=3");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.compound_uris.toString(),String.format("http://localhost:%d/compound/7/conformer/100211", port));
		
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset/3", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.POST,
				new Reference(String.format("http://localhost:%d/dataset/3", port)));

		
		c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset where id_srcdataset=3");
		Assert.assertEquals(3,table.getRowCount());
		c.close();		
	}
	
	@Test
	public void testPutPubchemSearchResult2Dataset() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset where id_srcdataset=3");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.compound_uris.toString(),String.format("http://localhost:%d/query/pubchem/50-00-0", port));
		form.add(OpenTox.params.compound_uris.toString(),String.format("http://localhost:%d/query/pubchem/50-00-0", port));
		
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset/3", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.PUT,
				new Reference(String.format("http://localhost:%d/dataset/3", port)));
		
		
		c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset where id_srcdataset=3");
		Assert.assertEquals(4,table.getRowCount());

		c.close();		
	}	
	
	@Test
	public void testPutCompound2Dataset() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset where id_srcdataset=3");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.compound_uris.toString(),String.format("http://localhost:%d/compound/7", port));
		
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset/3", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.PUT,
				new Reference(String.format("http://localhost:%d/dataset/3", port)));

		
		c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset where id_srcdataset=3");
		Assert.assertEquals(3,table.getRowCount());
		c.close();				
	}
	
	@Test
	public void testPutDataset() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/2", port));
		
		
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset/3", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.POST,
				new Reference(String.format("http://localhost:%d/dataset/3", port)));

		
	}		
	
	@Test
	public void testPutDatasetFeatures() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/2", port));
		
		
		testAsyncPoll(new Reference(String.format("http://localhost:%d/dataset/3", port)),
				MediaType.TEXT_URI_LIST, form.getWebRepresentation(), Method.POST,
				new Reference(String.format("http://localhost:%d/dataset/3", port)));

		
	}		
	@Test
	public void testDeleteDataset() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT *  FROM src_dataset where id_srcdataset=1");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/1", port));
		
		Response response =  testDelete(
					String.format("http://localhost:%d/dataset/1", port),
					MediaType.APPLICATION_WWW_FORM,
					null);
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT *  FROM src_dataset where id_srcdataset=1");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset where id_srcdataset=1");
		Assert.assertEquals(0,table.getRowCount());		
		c.close();
				
		
	}		
	
	@Test
	public void testDeleteDatasetTunnel() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT *  FROM src_dataset where id_srcdataset=1");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/1", port));
		
		Response response =  testPost(
					String.format("http://localhost:%d/dataset/1?method=DELETE", port),
					MediaType.APPLICATION_WWW_FORM,
					(Form)null);
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT *  FROM src_dataset where id_srcdataset=1");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset where id_srcdataset=1");
		Assert.assertEquals(0,table.getRowCount());		
		c.close();
				
		
	}		
	/**
	 * The query is used as a training set in models table and delete should fail with integrity violation
	 * @throws Exception
	 */
	@Test
	public void testDeleteForbidenQuery() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where title='temp'");
		Assert.assertEquals(2,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/1", port));
		
		Response response =  testDelete(
					String.format("http://localhost:%d/dataset/R1", port),
					MediaType.APPLICATION_WWW_FORM,
					null);
		Assert.assertEquals(Status.CLIENT_ERROR_FORBIDDEN, response.getStatus());
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query where idquery=1");
		Assert.assertEquals(1,table.getRowCount());
		c.close();		
	}			
	
	@Test
	public void testDeleteQueryResult() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query join sessions using(idsessions) where idquery=2");
		Assert.assertEquals(1,table.getRowCount());
		c.close();
		
		Form form = new Form();  
		form.add(OpenTox.params.dataset_uri.toString(),String.format("http://localhost:%d/dataset/1", port));
		
		Response response =  testDelete(
					String.format("http://localhost:%d/dataset/R2", port),
					MediaType.APPLICATION_WWW_FORM,
					null);
		Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
        c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query where idquery=2");
		Assert.assertEquals(0,table.getRowCount());
		table = 	c.createQueryTable("EXPECTED","SELECT idquery FROM query_results where idquery=2");
		Assert.assertEquals(0,table.getRowCount());		
		c.close();		
	}				
}
