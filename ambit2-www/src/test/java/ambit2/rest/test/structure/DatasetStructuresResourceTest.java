package ambit2.rest.test.structure;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.restlet.data.MediaType;

import ambit2.rest.test.ResourceTest;

public class DatasetStructuresResourceTest extends ResourceTest {
		
		@Override
		public String getTestURI() {
			return String.format("http://localhost:%d/dataset/1/compound", port);
		}
		@Test
		public void testXML() throws Exception {
			testGet("http://194.141.0.136:8080/ambit2-www/dataset/5/compound",MediaType.TEXT_XML);
		}
		@Override
		public boolean verifyResponseXML(String uri, MediaType media, InputStream in)
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
