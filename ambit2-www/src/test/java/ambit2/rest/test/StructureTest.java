package ambit2.rest.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.rest.ChemicalMediaType;

public class StructureTest extends ResourceTest {
	@Test
	public void testGet() throws Exception {
		Assert.fail("Not implemented");
		/*
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		request.setResourceRef("http://localhost:8080/structure/100214");
		request.setMethod(Method.GET);
		request.getClientInfo().getAcceptedMediaTypes().
	//	add(new Preference(MediaType.TEXT_XML));
		Response response = client.handle(request);
		
		String out = response.getEntity().getText();
		System.out.println(out);
		
		Diff diff = new Diff(new FileReader(
			new File("./etc/control-xml/control-web-races.xml")),
			new StringReader(response.getEntity().getText()));
		assertTrue(diff.toString(), diff.identical());
		*/
	}	
	
	@Test
	public void testGetSDF() throws Exception {
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		request.setResourceRef("http://localhost:8080/structure/100214");
		request.setMethod(Method.GET);
		request.getClientInfo().getAcceptedMediaTypes().
		add(new Preference(ChemicalMediaType.CHEMICAL_MDLSDF));
		Response response = client.handle(request);
		
		Assert.assertTrue(response.isEntityAvailable());
		InputStream in = response.getEntity().getStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = reader.readLine())!=null)
			System.out.println(line);
		in.close();

	}		
	@Test
	public void testGetPNG() throws Exception {
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		request.setResourceRef("http://localhost:8080/structure/100214");
		request.setMethod(Method.GET);
		request.getClientInfo().getAcceptedMediaTypes().
		add(new Preference(MediaType.IMAGE_PNG));
		Response response = client.handle(request);
		
		Assert.assertTrue(response.isEntityAvailable());
		InputStream in = response.getEntity().getStream();
		/*
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = reader.readLine())!=null)
			System.out.println(line);
			*/
		in.close();

	}		
	@Test
	public void testGetSmiles() throws Exception {
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		request.setResourceRef("http://localhost:8080/structure/100214");
		request.setMethod(Method.GET);
		request.getClientInfo().getAcceptedMediaTypes().
		add(new Preference(ChemicalMediaType.CHEMICAL_SMILES));
		Response response = client.handle(request);
		
		Assert.assertTrue(response.isEntityAvailable());
		InputStream in = response.getEntity().getStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = reader.readLine())!=null)
			Assert.assertEquals("F.[F-].[Na+]	SMILES=F.[F-].[Na+]	metric=1.0",line);

		in.close();

	}		
	
}
