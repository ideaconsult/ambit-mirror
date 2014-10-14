package ambit2.rest.test.aa;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;
import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;

import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.test.ResourceTest;

public class OpenSSOTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d%s", port,DatasetsResource.datasets);
	}
	@Test
	public void testURI_notoken() throws Exception {
		testGet(String.format("http://localhost:%d%s/1", port,DatasetsResource.datasets)
				,MediaType.TEXT_URI_LIST,Status.CLIENT_ERROR_UNAUTHORIZED);
	}
	
	@Test
	public void testURI_user_pass() throws Exception {
		String user = OpenSSOServicesConfig.getInstance().getTestUser();
		System.out.println(user);
		OpenSSOToken token = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
		token.login(user, OpenSSOServicesConfig.getInstance().getTestUserPass());
		Assert.assertTrue(token.isTokenValid());
		
		OpenSSOPolicy policy = new OpenSSOPolicy(OpenSSOServicesConfig.getInstance().getPolicyService());
		
		Reference ref = new Reference(String.format("http://localhost:%d%s", port,DatasetsResource.datasets));
		
		System.out.println(token.authorize(ref.toString(), "GET"));
		
		
		/*
		Status status = OpenSSOToken.createPolicy(String.format("%s",uri.replace("/", "_")),uri,user,token);
		System.out.println(status);
		testGet(ref.toString(),MediaType.TEXT_URI_LIST,Status.SUCCESS_OK);
		*/
		token.logout();
		
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
