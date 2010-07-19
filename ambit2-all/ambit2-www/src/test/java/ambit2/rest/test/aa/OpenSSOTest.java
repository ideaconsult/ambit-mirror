package ambit2.rest.test.aa;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.rest.aa.AAServicesConfig;
import ambit2.rest.aa.opensso.OpenSSOToken;
import ambit2.rest.aa.opensso.ProtectedTestResource;
import ambit2.rest.test.ResourceTest;

public class OpenSSOTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s", port,ProtectedTestResource.resource);
	}
	@Test
	public void testURI_notoken() throws Exception {
		testGet(String.format("http://localhost:%d%s/1", port,ProtectedTestResource.resource)
				,MediaType.TEXT_URI_LIST,Status.CLIENT_ERROR_UNAUTHORIZED);
	}
	
	@Test
	public void testURI_user_pass() throws Exception {
		String user = AAServicesConfig.getSingleton().getTestUser();
		String token  = OpenSSOToken.getTokenByUserPass(user, AAServicesConfig.getSingleton().getTestUserPass());
		Assert.assertTrue(OpenSSOToken.isValid(token));
		
		Reference ref = new Reference(String.format("http://localhost:%d%s", port,ProtectedTestResource.resource));
		
		String uri = ref.toString();
		ref = OpenSSOToken.addTokenToReference(ref, token);
		
		Status status = OpenSSOToken.createPolicy(String.format("%s",uri),uri,user,token);
		System.out.println(status);
		testGet(ref.toString(),MediaType.TEXT_URI_LIST,Status.SUCCESS_OK);
		OpenSSOToken.logout(token);
		
		Assert.assertFalse(OpenSSOToken.isValid(token));
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
		return count ==0;
	}	
	
}
