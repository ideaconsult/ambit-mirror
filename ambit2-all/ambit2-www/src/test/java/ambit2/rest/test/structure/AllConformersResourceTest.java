package ambit2.rest.test.structure;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.data.MediaType;

public class AllConformersResourceTest extends ConformerResourceTest {
	@Override
	public boolean verifyResponseSMILES(String uri, MediaType media,
			InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			Assert.assertEquals("F.[F-].[Na+]	SMILES=F.[F-].[Na+]	metric=1.0",line);
			count++;
		}
		return count ==2;
	}
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/compound/10/conformer", port);
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
		while ((line = r.readLine())!= null) {
			Assert.assertTrue(
					line.equals(String.format("http://localhost:%d/compound/10/conformer/999",port)) ||
					line.equals(String.format("http://localhost:%d/compound/10/conformer/100214",port))
					);
			
		}
		return true;
	}

	@Test
	public void testXML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_XML);
	}
	@Override
	public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
			throws Exception {
   	
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = r.readLine())!= null) {
			System.out.println(line);
		}
		return true;
	}	
}
