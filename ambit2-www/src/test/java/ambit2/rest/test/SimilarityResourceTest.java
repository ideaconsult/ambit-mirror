package ambit2.rest.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.rest.ChemicalMediaType;
import ambit2.rest.similarity.SimilarityResource;



public class SimilarityResourceTest extends ResourceTest {
	protected static String URI = "http://localhost:8080/query/similarity/method/fp1024/distance/tanimoto/0.1/smiles/C";
	//protected static String URI = "http://nina.acad.bg/ambit2/query/similarity/method/fp1024/distance/tanimoto/0.1/smiles/C";
	@Test
	public void testGet() throws Exception {
		Client client = new Client(Protocol.HTTP);
		Response response =	client.get(URI);
		String out = response.getEntity().getText();
		System.out.println(out);
		/*
		Diff diff = new Diff(new FileReader(
			new File("./etc/control-xml/control-web-races.xml")),
			new StringReader(response.getEntity().getText()));
		assertTrue(diff.toString(), diff.identical());
		*/
	}
	
	@Test
	public void testNoResults() throws Exception {
		runQuery(ChemicalMediaType.CHEMICAL_SMILES,"http://localhost:8080/query/similarity/method/fp1024/distance/tanimoto/0.1/smiles/[I]");
	}		
	@Test
	public void testSmiles() throws Exception {
		runQuery(ChemicalMediaType.CHEMICAL_SMILES);
	}	
	@Test
	public void testSDF() throws Exception {
		runQuery(ChemicalMediaType.CHEMICAL_MDLSDF);
	}		
	
	@Test
	public void testPlainText() throws Exception {
		runQuery(MediaType.TEXT_PLAIN);
	}		
	@Test
	public void testURIList() throws Exception {
		runQuery(MediaType.TEXT_URI_LIST);
	}		
	@Test
	public void testGetSmiles() {
		SimilarityResource resource = new SimilarityResource(null,null,null);
		try {
			resource.getMolecule("NC1=CC=C(N)C=C");
			Assert.assertTrue(false);
		} catch (InvalidSmilesException x) {
			Assert.assertTrue(true);
		}
		try {
			resource.getMolecule("[13CH4]");			
			Assert.assertTrue(true);
		} catch (InvalidSmilesException x) {
			Assert.assertTrue(false);
		}
		try {
			resource.getMolecule("C1CC[13CH4]CCC11");			
			Assert.assertTrue(false);
		} catch (InvalidSmilesException x) {
			Assert.assertTrue(true);
		}		
		try {
			resource.getMolecule("c1cccc11");			
			Assert.assertTrue(false);
		} catch (InvalidSmilesException x) {
			Assert.assertTrue(true);
		}			
		try {
			resource.getMolecule("c2cccc2c1ccccc1");			
			Assert.assertTrue(true);
		} catch (InvalidSmilesException x) {
			Assert.assertTrue(false);
		}		
	 	
	}
	public void runQuery(MediaType mediaType) throws Exception {
		runQuery(mediaType,URI);
	}
	public void runQuery(MediaType mediaType,String uri) throws Exception {
		long now = System.currentTimeMillis();
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		request.setResourceRef(uri);
		request.setMethod(Method.GET);
		request.getClientInfo().getAcceptedMediaTypes().add(new Preference(mediaType));
		System.out.println(request.getClientInfo().getAcceptedMediaTypes());
		Response response = client.handle(request);
		
		System.out.println(response.getStatus());
		if (response.getStatus().equals(org.restlet.data.Status.SUCCESS_OK)) {
			Assert.assertTrue(response.isEntityAvailable());
			InputStream in = response.getEntity().getStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine())!=null)
				System.out.println(line);
	
			in.close();
		}
		/*
		Diff diff = new Diff(new FileReader(
			new File("./etc/control-xml/control-web-races.xml")),
			new StringReader(response.getEntity().getText()));
		assertTrue(diff.toString(), diff.identical());
		*/
		System.out.println(System.currentTimeMillis()-now);
	}		
	
	
}
