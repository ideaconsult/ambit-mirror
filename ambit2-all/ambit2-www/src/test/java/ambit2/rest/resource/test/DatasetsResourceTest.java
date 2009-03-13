package ambit2.rest.resource.test;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.restlet.data.Response;

import ambit2.core.config.Preferences;
import ambit2.rest.AmbitApplication;

public class DatasetsResourceTest extends ResourceTest {

	@Test
	public void testGetRepresentationVariant() {
		fail("Not yet implemented");
	}
	@Test
	public void getDatasets() throws Exception {
		Client client = new Client(Protocol.HTTP);
		Response response =	client.get("http://localhost:8080"+AmbitApplication.datasets);
		String out = response.getEntity().getText();
		System.out.println(out);
		/*
		Diff diff = new Diff(new FileReader(
			new File("./etc/control-xml/control-web-races.xml")),
			new StringReader(response.getEntity().getText()));
		assertTrue(diff.toString(), diff.identical());
		*/
	}
	@Test
	public void getDataset() throws Exception {
		Client client = new Client(Protocol.HTTP);
		Response response =	client.get("http://localhost:8080"+AmbitApplication.datasets + "/Dataset 2");
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
