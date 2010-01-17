package ambit2.rest.test.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.rest.OpenTox;
import ambit2.rest.model.ModelResource;
import ambit2.rest.test.ResourceTest;

public class ModelResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s/1", port,ModelResource.resource);
	}

	/*
	 * @Test public void testXML() throws Exception {
	 * testGet(getTestURI(),MediaType.TEXT_XML); }
	 * 
	 * @Override public boolean verifyResponseXML(String uri, MediaType media,
	 * InputStream in) throws Exception { BufferedReader r = new
	 * BufferedReader(new InputStreamReader(in)); String line = null; int count
	 * = 0; while ((line = r.readLine())!= null) { System.out.println(line);
	 * count++; } return count>0; }
	 */
	@Test
	public void testHTML() throws Exception {
		testGet(getTestURI(), MediaType.TEXT_HTML);
	}

	@Override
	public boolean verifyResponseHTML(String uri, MediaType media,
			InputStream in) throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine()) != null) {
			// Assert.assertEquals("1530-32-1 ", line);
			System.out.println(line);
			count++;
		}
		return count > 1;
	}

	@Test
	public void testRDFXML() throws Exception {
		testGet(getTestURI(), MediaType.APPLICATION_RDF_XML);
	}

	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(), MediaType.TEXT_URI_LIST);
	}

	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine()) != null) {
			Assert.assertEquals(String.format("http://localhost:%d/model/1",
					port), line);
			count++;
		}
		return count == 1;
	}

	@Test
	public void testPostDataset() throws Exception {
		Form headers = new Form();
		String dataset = String.format("http://localhost:%d/dataset/1", port);
		headers.add(OpenTox.params.dataset_uri.toString(), dataset);
		testAsyncTask(getTestURI(), headers, Status.SUCCESS_OK, String.format(
				"%s?feature_uris[]=%s", dataset, Reference.encode(String
						.format("%s/predicted", getTestURI()))));
	}

	@Test
	public void testPostCompound() throws Exception {
		Form headers = new Form();
		String dataset = "http://ambit.uni-plovdiv.bg:8080/ambit2/compound/1";
		headers.add(OpenTox.params.dataset_uri.toString(), dataset);
		
		testAsyncTask(getTestURI(), headers,
				Status.SUCCESS_OK, String.format("%s?feature_uris[]=%s",
						dataset, Reference.encode(String.format("%s/predicted",
								getTestURI()))));
	}
	
	@Test
	public void testClustering() throws Exception {
		predict(String.format("http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2",port,port,port),
				null,
				String.format("http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2",port,port,port),
				String.format("http://localhost:%d/algorithm/SimpleKMeans", port)
				);
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,value_num FROM property_values join properties using(idproperty) where name='Cluster'");
		Assert.assertEquals(4,table.getRowCount());
		c.close();			
	}
	@Test
	public void testJ48Test() throws Exception {
		predict(String.format("http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/2&feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/4",port,port,port,port),
				String.format("http://localhost:%d/feature/4",port),
				String.format("http://localhost:%d/dataset/2?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/3",port,port,port),
				String.format("http://localhost:%d/algorithm/J48", port));
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT name,value_num FROM property_values join properties using(idproperty) join struc_dataset using(idstructure) where id_srcdataset=2 and idproperty=5");
		Assert.assertEquals(2,table.getRowCount());
		c.close();			
	}	
	
	@Test
	public void testJ48() throws Exception {
		predict(String.format("http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2&feature_uris[]=http://localhost:%d/feature/4",port,port,port,port),
				String.format("http://localhost:%d/feature/4",port),
				String.format("http://localhost:%d/dataset/1?feature_uris[]=http://localhost:%d/feature/1&feature_uris[]=http://localhost:%d/feature/2",port,port,port,port),
				String.format("http://localhost:%d/algorithm/J48", port));
	
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT id_srcdataset,idstructure,idproperty,name,value_string,value_number FROM values_all join struc_dataset using(idstructure) where id_srcdataset=1 and idproperty=5 order by idstructure");
		Assert.assertEquals(4,table.getRowCount());
		c.close();		
		
	}	
	public void predict(String dataset, String target, String datasetTest, String algorithmURI) throws Exception {		
		setUpDatabase("src/test/resources/src-datasets_model.xml");
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED",
				"SELECT id_srcdataset,idstructure,idproperty,name,value_string,value_number FROM values_all join struc_dataset using(idstructure) where id_srcdataset=1 and idproperty=4 order by idstructure");
		Assert.assertEquals(3,table.getRowCount());
		c.close();		
		//First create a model
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(),dataset); 
		if (target!=null)
		headers.add(OpenTox.params.target.toString(),target);

		String wekaURI = String.format("http://localhost:%d%s/3", port,ModelResource.resource);

		
		testAsyncTask(
				algorithmURI,
				headers, Status.SUCCESS_OK,
				wekaURI);
		
		headers = new Form();  
		if (target!=null)
			headers.add(OpenTox.params.target.toString(),target);		
		headers.add(OpenTox.params.dataset_uri.toString(),datasetTest); 
		testAsyncTask(wekaURI, headers, Status.SUCCESS_OK, String.format(
				"%s?feature_uris[]=%s", datasetTest, Reference.encode(String
						.format("%s/predicted", wekaURI))));
		
	
	}	
	


		
}