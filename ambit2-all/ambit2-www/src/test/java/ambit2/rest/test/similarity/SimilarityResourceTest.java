package ambit2.rest.test.similarity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.rest.ChemicalMediaType;
import ambit2.rest.test.ResourceTest;



public class SimilarityResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/query/similarity?search=%s&threshold=0.165", port,
				Reference.encode("c1ccccc1"));
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
			System.out.println(line);
			//Assert.assertEquals(String.format("http://localhost:%d/compound/11/conformer/100215",port),line);
			count++;
		}
		return count==1;
	}
	
	@Test
	public void testSDF() throws Exception {
		//testGet(getTestURI(),ChemicalMediaType.CHEMICAL_MDLSDF);
	}
	
	@Test
	public void testSMILES() throws Exception {
		//testGet(getTestURI(),ChemicalMediaType.CHEMICAL_SMILES);
	}
	
	@Test
	public void testCSV() throws Exception {
		//testGet(getTestURI(),MediaType.TEXT_CSV);
	}			
	@Override
	public boolean verifyResponseCSV(String uri, MediaType media, InputStream in)
			throws Exception {
		return super.verifyResponseCSV(uri, media, in);
	}
	@Override
	public boolean verifyResponseSDF(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count = 0;
		while ((line = r.readLine())!= null) {
			System.out.println(line);
			//Assert.assertEquals(String.format("http://localhost:%d/compound/11/conformer/100215",port),line);
			count++;
		}
		return count==1;
	}
	@Override
	public boolean verifyResponseSMILES(String uri, MediaType media,
			InputStream in) throws Exception {
		return super.verifyResponseSMILES(uri, media, in);
	}
}
