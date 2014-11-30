package ambit2.rest.test.bundle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.test.ProtectedResourceTest;


public class BundleResourceTest extends ProtectedResourceTest {
	protected String dbFile = "src/test/resources/descriptors-datasets.xml";
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/bundle", port);
	}
	@Test
	public void testURI() throws Exception {
		setUpDatabase(dbFile);
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
		return count ==1;
	}	

	
	@Test
	public void testCreateEntry() throws Exception {
		/*

		InputStream in  = getClass().getClassLoader().getResourceAsStream("input.sdf");

		StringBuilder b = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line=reader.readLine())!=null) {
			b.append(line);
			b.append('\n');
		}
		
		testAsyncPoll(new Reference(getTestURI()),ChemicalMediaType.CHEMICAL_MDLSDF, 
				new StringRepresentation(b.toString(),ChemicalMediaType.CHEMICAL_MDLSDF),Method.POST,
				new Reference(String.format("http://localhost:%d/dataset/4",port)));
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(12,table.getRowCount());
		c.close();
		*/
		
	}	


}