package ambit2.rest.test;

import java.util.UUID;

import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.opentox.dsl.aa.IAuthToken;
import org.opentox.dsl.task.ClientResourceWrapper;

import ambit2.rest.aa.opensso.OpenSSOServicesConfig;


public abstract class ProtectedResourceTest extends ResourceTest implements IAuthToken  {

	protected String getCreator() {
		if ((ssoToken!=null) && (ssoToken.getToken()!=null)) 
			return	OpenSSOServicesConfig.getInstance().getTestUser();
		else return "test";
	}

	protected boolean isAAEnabled() {
		return (OpenSSOServicesConfig.getInstance().isEnabled());
	}
	@Override
	public void setUp() throws Exception {
		if (isAAEnabled()) {
			ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
			if (ssoToken.login(
					OpenSSOServicesConfig.getInstance().getTestUser(),
					OpenSSOServicesConfig.getInstance().getTestUserPass()
					)) {
				ClientResourceWrapper.setTokenFactory(this);
			} else
				throw new Exception(String.format("Error logging to SSO (%s)",OpenSSOServicesConfig.getInstance().getTestUser()));
		}
		super.setUp();
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		try {
			ClientResourceWrapper.setTokenFactory(null);
			if (ssoToken!= null) ssoToken.logout();
		} catch (Exception x) {
		}
	}
	@Override
	public String getToken() {
		return ssoToken==null?null:ssoToken.getToken();
	}
	
	protected String createPublicPolicy(String uri) throws Exception {
		OpenSSOPolicy policy = new OpenSSOPolicy(OpenSSOServicesConfig.getInstance().getPolicyService());
		StringBuffer b = new StringBuffer();
		b.append("member_");
		b.append(uri.replace(":","").replace("/",""));
	//	b.append("_");
	//	b.append(UUID.randomUUID());
		int httpcode = policy.createGroupPolicy("member", 
				ssoToken, uri, new String[] {"GET","DELETE","POST","PUT"},b.toString());
		if (httpcode == 200) return b.toString();
		else throw new Exception(String.format("Error creating policy %d",httpcode));
	}

	protected String createPolicy(String uri) throws Exception {
		OpenSSOPolicy policy = new OpenSSOPolicy(OpenSSOServicesConfig.getInstance().getPolicyService());
		StringBuffer b = new StringBuffer();
		b.append(uri.replace(":","").replace("/",""));
		b.append("_");
		b.append(UUID.randomUUID());
		int httpcode = policy.createUserPolicy(OpenSSOServicesConfig.getInstance().getTestUser(), 
				ssoToken, uri, new String[] {"GET","DELETE","POST","PUT"},b.toString());
		if (httpcode == 200) return b.toString();
		else throw new Exception(String.format("Error creating policy %d",httpcode));
		
	}
	
	
	protected int deletePolicy(String policyID) throws Exception {
		OpenSSOPolicy policy = new OpenSSOPolicy(OpenSSOServicesConfig.getInstance().getPolicyService());
		return policy.deletePolicy( ssoToken,policyID);
		
	}	
}
