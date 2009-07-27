package ambit2.rest.test;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.ChemicalMediaType;

public class HttpConnectionTest extends ResourceTest {
	protected static String URI = "http://localhost:8080/query/similarity/method/fp1024/distance/tanimoto/0.1/smiles/C";

	@Test
	public void testConnectSmiles() throws Exception {
		connect(ChemicalMediaType.CHEMICAL_SMILES);
	}	
	@Test
	public void testConnectSDF() throws Exception {
		connect(ChemicalMediaType.CHEMICAL_MDLSDF);
	}		
	
	@Test
	public void testConnectPlainText() throws Exception {
		connect(MediaType.TEXT_PLAIN);
	}		
	@Test
	public void testConnectURIList() throws Exception {
		connect(MediaType.TEXT_URI_LIST);
	}			
	protected void connect(MediaType mediaType) throws Exception {
		URL url = new URL(URI);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Accept",mediaType.getName());	
		conn.setRequestMethod("GET");
		conn.setDoInput(true) ;
		conn.setDoOutput(false) ;
		conn.setUseCaches(false);
	    
		System.out.println("Response code = " +	conn.getResponseCode());
		BufferedReader content = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line = null;
		while( (line = content.readLine()) != null) {
			System.out.println(line);
		}
		content.close();
		conn.disconnect();		
	}

}
