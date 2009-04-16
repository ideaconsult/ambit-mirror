package ambit2.rest.resource.test;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Response;

import ambit2.base.io.DownloadTool;

public class CDKDepictTest extends ResourceTest {
	protected static String URI = "http://localhost:8080/cdk/depict/c1ccccc1";
	@Test
	public void testGet() throws Exception {
		Client client = new Client(Protocol.HTTP);
		Response response =	client.get(URI);
		InputStream in = response.getEntity().getStream();
		DownloadTool.download(in, new File("test.png"));
	}
}
