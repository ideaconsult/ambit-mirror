package ambit2.rest.test.algorithm;

import org.junit.Assert;
import org.junit.Test;
import org.opentox.aa.opensso.OpenSSOPolicy;
import org.restlet.data.Form;
import org.restlet.data.Status;

import ambit2.rest.OpenTox;
import ambit2.rest.aa.opensso.OpenSSOServicesConfig;
import ambit2.rest.test.ProtectedResourceTest;

public class ProtectedModelBuilder extends ProtectedResourceTest {
	@Override
	protected void setDatabase() throws Exception {
		setUpDatabase("src/test/resources/num-datasets.xml");
	}
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/algorithm", port);
	}
	
	
	@Test
	public void testCalculateBCUT() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,
						"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted"
						//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTw-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTw-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTc-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTc-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTp-1lorg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor&feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Ffeature%2FBCUTp-1horg.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor"
						));		
				//"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FBCUT%2Bdescriptors%2Fpredicted"));
	}	
	
	@Test
	public void testDistanceMahalanobis() throws Exception {
		if (OpenSSOServicesConfig.getInstance().isEnabled()) try {
			OpenSSOPolicy policy = new OpenSSOPolicy(OpenSSOServicesConfig.getInstance().getPolicyService());
			int resultcode = policy.deletePolicy(ssoToken, "httplocalhost8181model3GETPUTPOSTDELETE");
			System.out.println(resultcode);
		} catch (Exception x) {}
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/distanceMahalanobis", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));

		Assert.assertTrue(ssoToken.authorize(String.format("http://localhost:%d/model/3", port), "GET"));
		Assert.assertTrue(ssoToken.authorize(String.format("http://localhost:%d/model/3", port), "POST"));
		
		testAsyncTask(
				String.format("http://localhost:%d/model/3", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/1%s", port,
						String.format("%s","?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2F3%2Fpredicted")));

		
	}	
}
