package ambit2.rest.test.structure;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.test.ResourceTest;

public class FastDatasetResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/fastdataset/1?chemicals=false", port);
	}
	
	@Test
	public void testCSV() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_CSV);
	}
	@Override
	public boolean verifyResponseCSV(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return count == 4;
	}	
}
