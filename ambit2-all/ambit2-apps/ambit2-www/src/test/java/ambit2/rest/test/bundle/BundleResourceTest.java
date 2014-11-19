package ambit2.rest.test.bundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.AbstractDataset;
import ambit2.base.data.ISourceDataset;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OpenTox;
import ambit2.rest.test.ProtectedResourceTest;


public class BundleResourceTest extends ProtectedResourceTest {

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/bundle", port);
	}
	@Test
	public void testURI() throws Exception {
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