package ambit2.rest.test;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Response;

public class Builder3DTest  extends ResourceTest  {
	protected static String URI = "http://localhost:8080/build3d/smiles/CCC";

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
}
