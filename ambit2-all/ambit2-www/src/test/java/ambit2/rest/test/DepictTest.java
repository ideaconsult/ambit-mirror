package ambit2.rest.test;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Response;

import ambit2.base.io.DownloadTool;

public class DepictTest extends ResourceTest {
	protected static String URI = "http://localhost:8080/daylight/depict/c1ccccc1";
	@Test
	public void testGet() throws Exception {
		Client client = new Client(Protocol.HTTP);
		Response response =	client.get(URI);
		InputStream in = response.getEntity().getStream();
		DownloadTool.download(in, new File("test.png"));
	}
	
}
