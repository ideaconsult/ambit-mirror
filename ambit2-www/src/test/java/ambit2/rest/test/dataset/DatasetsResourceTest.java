package ambit2.rest.test.dataset;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.rest.ChemicalMediaType;
import ambit2.rest.test.ResourceTest;


public class DatasetsResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/dataset", port);
	}
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
	}
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return count ==3;
	}	
	
		
	@Test
	public void testHTML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_HTML);
	}
	@Override
	public boolean verifyResponseHTML(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return count>0;
	}		
	public Response testPost(String uri, MediaType media, String content) throws Exception {
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;  
		ChallengeResponse authentication = new ChallengeResponse(scheme,  
		         "guest", "guest");  
		request.setChallengeResponse(authentication);  		
		request.setResourceRef(uri);
		request.setMethod(Method.POST);
		request.setEntity(content,media);
		//request.getAttributes().put("org.restlet.http.headers", headers);		
		request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(media));
		return client.handle(request);
	}
	
	public Response testPostFile(String uri, MediaType media, Representation file) throws Exception {
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;  
		ChallengeResponse authentication = new ChallengeResponse(scheme,  
		         "guest", "guest");  
		request.setChallengeResponse(authentication);  
		request.setResourceRef(uri);
		request.setMethod(Method.POST);
		request.setEntity(file);
		request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(media));
		return client.handle(request);
	}	
		
	@Test
	public void testCreateEntry() throws Exception {
		
		InputStream in  = getClass().getClassLoader().getResourceAsStream("input.sdf");

		StringBuilder b = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line=reader.readLine())!=null) {
			b.append(line);
			b.append('\n');
		}
		Response r = testPost(getTestURI(), ChemicalMediaType.CHEMICAL_MDLSDF, b.toString());
		Reference uri = r.getLocationRef();
		
		while (!r.getStatus().equals(Status.SUCCESS_OK)) {
			//System.out.println(r.getStatus() + " " +r.getLocationRef());
			if (r.getStatus().equals(Status.CLIENT_ERROR_BAD_REQUEST)) 
				throw new ResourceException(r.getStatus());
			if (r.getStatus().equals(Status.CLIENT_ERROR_NOT_ACCEPTABLE))
				throw new ResourceException(r.getStatus());
			Request request = new Request();
			Client client = new Client(Protocol.HTTP);
			request.setResourceRef(uri);
			request.setMethod(Method.GET);
			r = client.handle(request);
			uri = r.getLocationRef();
		}
		//System.out.println(r.getLocationRef());
	//	Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(12,table.getRowCount());
		c.close();
		
	}	
	
	@Test
	public void testCreateEntryRDF() throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(5,table.getRowCount());
		c.close();
				
		CreateEntryRDF("import_dataset2.rdf");
		
		c = getConnection();	
		table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(5,table.getRowCount());
		c.close();
	}
	@Test
	public void testCreateEntryRDF1() throws Exception {
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(5,table.getRowCount());
		c.close();
				
		CreateEntryRDF("37.rdf");
		
		
	//	CreateEntryRDF("FeatureGenerationExample.rdf");
		
		c = getConnection();	
        table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(7,table.getRowCount());
		c.close();
	}	
	public void CreateEntryRDF(String name) throws Exception {
			
		

		InputStream in  = getClass().getClassLoader().getResourceAsStream(name);

		StringBuilder b = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line=reader.readLine())!=null) {
			b.append(line);
			b.append('\n');
		}
		Response r = testPost(getTestURI(), MediaType.APPLICATION_RDF_XML, b.toString());
		Reference uri = r.getLocationRef();
		
		while (!r.getStatus().equals(Status.SUCCESS_OK)) {
			System.out.println(r.getStatus() + " " +r.getLocationRef());
			if (r.getStatus().equals(Status.CLIENT_ERROR_NOT_FOUND))
				throw new ResourceException(r.getStatus());
			if (r.getStatus().equals(Status.CLIENT_ERROR_BAD_REQUEST)) 
				throw new ResourceException(r.getStatus());
			if (r.getStatus().equals(Status.CLIENT_ERROR_NOT_ACCEPTABLE))
				throw new ResourceException(r.getStatus());
			Request request = new Request();
			Client client = new Client(Protocol.HTTP);
			request.setResourceRef(uri);
			request.setMethod(Method.GET);
			System.out.println("read "+uri);
			r = client.handle(request);
			System.out.println("done "+uri);
			uri = r.getLocationRef();
		}
		//System.out.println(r.getLocationRef());
	//	Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());

		
	}	
	
	public void testCreateEntryFromFileRDF() throws Exception {
		
		FileRepresentation rep = new FileRepresentation(
				"E:/src/ambit2-all/ambit2-www/src/test/resources/input.rdf", 
				//"E:/src/ambit2-all/src/test/resources/endpoints/skin_sensitisation/LLNA_3D.sdf",
				 MediaType.APPLICATION_RDF_XML, 0);
				//EncodeRepresentation encodedRep = new EncodeRepresentation(Encoding.GZIP,rep);
				
		Response r = testPostFile(getTestURI(), MediaType.APPLICATION_RDF_XML, rep);
		Reference uri = r.getLocationRef();
		while (!r.getStatus().equals(Status.SUCCESS_OK)) {
			//System.out.println(r.getStatus() + " " +r.getLocationRef());
			
			Request request = new Request();
			Client client = new Client(Protocol.HTTP);
			request.setResourceRef(uri);
			request.setMethod(Method.GET);
			r = client.handle(request);
			uri = r.getLocationRef();
		}
		System.out.println(r.getStatus());
		//System.out.println(r.getLocationRef());
	//	Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(5,table.getRowCount());
		c.close();
		
	}		
	
@Test
	public void testCreateEntryFromFile() throws Exception {
		
		FileRepresentation rep = new FileRepresentation(
				"E:/src/ambit2-all/ambit2-www/src/test/resources/input.sdf", 
				//"E:/src/ambit2-all/src/test/resources/endpoints/skin_sensitisation/LLNA_3D.sdf",
				ChemicalMediaType.CHEMICAL_MDLSDF, 0);
				//EncodeRepresentation encodedRep = new EncodeRepresentation(Encoding.GZIP,rep);
				
		Response r = testPostFile(getTestURI(), ChemicalMediaType.CHEMICAL_MDLSDF, rep);
		Reference uri = r.getLocationRef();
		while (!r.getStatus().equals(Status.SUCCESS_OK)) {
			//System.out.println(r.getStatus() + " " +r.getLocationRef());
			
			Request request = new Request();
			Client client = new Client(Protocol.HTTP);
			request.setResourceRef(uri);
			request.setMethod(Method.GET);
			r = client.handle(request);
			uri = r.getLocationRef();
		}
		System.out.println(r.getStatus());
		//System.out.println(r.getLocationRef());
	//	Assert.assertEquals(Status.SUCCESS_OK, response.getStatus());
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(12,table.getRowCount());
		c.close();
		
	}	
	
	public void testCreateEntryTUMRDF1() throws Exception {
	        IDatabaseConnection c = getConnection();	
			ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
			Assert.assertEquals(5,table.getRowCount());
			c.close();
					
			CreateEntryRDF("tum1.rdf");
			
			c = getConnection();	
			table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
			Assert.assertEquals(5,table.getRowCount());
			c.close();

	}
	@Test
	public void testCreateEntryTUMRDF2() throws Exception {
		Assert.fail("test with tum1.rdf");
	}
	@Test
	public void testSameAs() throws Exception {
		Assert.fail("Same as is always the same as Name, like here http://ambit.uni-plovdiv.bg:8080/ambit2/feature/82119 or here http://ambit.uni-plovdiv.bg:8080/ambit2/feature/82138. Maybe this is a bug while parsing an rdf or sdf file.");
	}
	@Test
	public void testPostEmptyDataset() throws Exception {
		Assert.fail("Post empty dataset");
	}
	@Test
	public void testNTUA() throws Exception {
		/*
	> >From first sight noticed
	> > 1) It returns dataset content itself, not a dataset or task URI , as
	> > in the specification
	I have tried to POST the content to your server using this command:

	curl -H 'Content-type:application/rdf+xml' -X POST --data-binary
	@/path/to/my/ds.rdf http://ambit.uni-plovdiv.bg:8080/ambit2/dataset -v

	This seems to work properly and produces a task resource. The task
	completes and returns a dataset uri which is
	http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/30 but its content is
	different from the dataset I posted. So I return to the client the
	content of the dataset itself rather than the URI of a created dataset.
	Is it possible for you to solve this problem?
		 */
		Assert.fail("POST from NTUA");
	}
	/*
	public void testPostFileWithoutRestlet() throws Exception{
		postFileWithoutRestlet("E:/src/ambit2-all/ambit2-www/src/test/resources/input.sdf");
	
	}
	//should set name field
	  public void postFileWithoutRestlet(String filename) throws Exception{
			String NEWLINE = "\r\n";
			    
			String PREFIX = "--";
		 
	    	String userPassword = "guest:guest";
			
			String encoding = new sun.misc.BASE64Encoder().encode (userPassword.getBytes());

			URL url = new URL(getTestURI());


			// create a boundary string

			String boundary = "--------------------" + 
			    Long.toString(System.currentTimeMillis(), 16);


			URLConnection urlConn = url.openConnection();
			if(urlConn instanceof HttpURLConnection) {
			    HttpURLConnection httpConn = (HttpURLConnection)urlConn;
			    httpConn.setRequestMethod("POST");
			}
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setDefaultUseCaches(false);

			urlConn.setRequestProperty ("Authorization", "Basic " + encoding);

	
			urlConn.setRequestProperty("Content-Type", 
						   "multipart/form-data; boundary=" + boundary);

			// set some other request headers...

			urlConn.setRequestProperty("Connection", "Keep-Alive");
			urlConn.setRequestProperty("Cache-Control", "no-cache");


			DataOutputStream os = new DataOutputStream(urlConn.getOutputStream());


			os.writeBytes(PREFIX);
			os.writeBytes(boundary);
			os.writeBytes(NEWLINE);
			// write content header
			//os.writeBytes("Content-Disposition: form-data; name=\"datafile"  + 
			//	       "\"; filename=\"" + args[0] + "\"");
			os.writeBytes("Content-Disposition: form-data; name=\"file\"");

			os.writeBytes(NEWLINE);
			//if(mimeType != null) {
			//    os.writeBytes("Content-Type: " + mimeType);
			//    os.writeBytes(NEWLINE);
			//}
			os.writeBytes(NEWLINE);
			// write content
			byte[] data =new byte[1024];
			FileInputStream is = new FileInputStream(new File(filename));
			int r = 0;
			while((r = is.read(data, 0, data.length)) != -1) {
			    os.write(data, 0, r);
			}
			// close input stream, but ignore any possible exception for it
			try {
			    is.close();
			} catch(Exception e) {}
			os.writeBytes(NEWLINE);
			os.flush();
			
			os.writeBytes(PREFIX);
			os.writeBytes(boundary);
			os.writeBytes(PREFIX);
			os.writeBytes(NEWLINE);
			os.flush();
			os.close();



			// read response from server


			BufferedReader in = new BufferedReader(
							       new InputStreamReader(urlConn.getInputStream()));
			String line = "";
			while((line = in.readLine()) != null) {
			    System.out.println(line);
			}
			in.close();

     }
*/

}