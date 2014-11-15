package ambit2.rest.admin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.idea.restnet.c.task.ClientResourceWrapper;

import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

import ambit2.rest.test.ResourceTest;

public class AdminTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/admin/database", port);
	}
	
	@Test
	public void testTXT() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_PLAIN);
	}
	@Override
	public boolean verifyResponseTXT(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		boolean ok = false;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			if ("Version: 4.3".equals(line)) ok = true;
			count++;
		}
		return ok;
	}		
	
	@Test
	public void testCreateExistingDB() throws Exception {
		Form form = new Form();
		
		form.add("dbname", getDatabase());
		form.add("user", getAdminUser());
		form.add("pass",getAdminPWD());
		//should fail
		ClientResourceWrapper c = new ClientResourceWrapper(getTestURI());
		Representation r = null;
		try {
			r = c.post(form.getWebRepresentation(),MediaType.APPLICATION_WWW_FORM);
			System.out.println(c.getStatus());
			Assert.assertTrue(false);
		} catch (Exception x) {
			//System.out.println(x.getMessage());
			Assert.assertTrue(true);
		} finally {
			c.release();
			if (r != null) r.release();
		}
	}	
}
