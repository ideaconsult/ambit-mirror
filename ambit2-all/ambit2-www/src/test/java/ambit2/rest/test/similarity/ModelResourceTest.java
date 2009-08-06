package ambit2.rest.test.similarity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.test.ResourceTest;

public class ModelResourceTest extends ResourceTest {

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/task/1", port);
	}
	@Test
	public void testURI() throws Exception {
		testGet(String.format("http://localhost:%d/model/pka", port),MediaType.TEXT_URI_LIST);
		while (true)
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

}
