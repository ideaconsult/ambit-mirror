package ambit2.rest.test;

import java.util.UUID;

import net.idea.restnet.c.task.ClientResourceWrapper;
import net.idea.restnet.i.aa.IAuthToken;

import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;

import ambit2.rest.aa.opensso.OpenSSOServicesConfig;


public abstract class ProtectedResourceTest extends ResourceTest implements IAuthToken  {

	protected String getCreator() {
		if ((ssoToken!=null) && (ssoToken.getToken()!=null)) 
			try { return OpenSSOServicesConfig.getInstance().getTestUser();} catch (Exception x) {return null;}
		else return "test";
	}

	protected boolean isAAEnabled() {
		 try {return (OpenSSOServicesConfig.getInstance().isEnabled()); } catch (Exception x) {return true;}
	}
	@Override
	public void setUp() throws Exception {
		setUpAA();
		super.setUp();
	}
	
	public void setUpAA() throws Exception {
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
	
	protected String createPartnerReadOnlyPolicy(String uri) throws Exception {
		OpenSSOPolicy policy = new OpenSSOPolicy(OpenSSOServicesConfig.getInstance().getPolicyService());
		StringBuffer b = new StringBuffer();
		b.append("partner_ro_");
		b.append(uri.replace(":","").replace("/",""));
	//	b.append("_");
	//	b.append(UUID.randomUUID());
		int httpcode = policy.createGroupPolicy("partner", 
				ssoToken, uri, new String[] {"GET"},b.toString());
		if (httpcode == 200) return b.toString();
		else throw new Exception(String.format("Error creating policy %d",httpcode));
	}			
	
	protected String createPartnerRWPolicy(String uri) throws Exception {
		OpenSSOPolicy policy = new OpenSSOPolicy(OpenSSOServicesConfig.getInstance().getPolicyService());
		StringBuffer b = new StringBuffer();
		b.append("partner_rw_");
		b.append(uri.replace(":","").replace("/",""));
	//	b.append("_");
	//	b.append(UUID.randomUUID());
		int httpcode = policy.createGroupPolicy("partner", 
				ssoToken, uri, new String[] {"GET","POST","PUT"},b.toString());
		if (httpcode == 200) return b.toString();
		else throw new Exception(String.format("Error creating policy %d",httpcode));
	}		
	protected String createPublicPOSTPolicy(String uri) throws Exception {
		OpenSSOPolicy policy = new OpenSSOPolicy(OpenSSOServicesConfig.getInstance().getPolicyService());
		StringBuffer b = new StringBuffer();
		b.append("member_");
		b.append(uri.replace(":","").replace("/",""));
	//	b.append("_");
	//	b.append(UUID.randomUUID());
		int httpcode = policy.createGroupPolicy("member", 
				ssoToken, uri, new String[] {"GET","POST"},b.toString());
		if (httpcode == 200) return b.toString();
		else throw new Exception(String.format("Error creating policy %d",httpcode));
	}	
	
	protected String createPublicROPolicy(String uri) throws Exception {
		OpenSSOPolicy policy = new OpenSSOPolicy(OpenSSOServicesConfig.getInstance().getPolicyService());
		StringBuffer b = new StringBuffer();
		b.append("member_ro");
		b.append(uri.replace(":","").replace("/",""));
	//	b.append("_");
	//	b.append(UUID.randomUUID());
		int httpcode = policy.createGroupPolicy("member", 
				ssoToken, uri, new String[] {"GET"},b.toString());
		if (httpcode == 200) return b.toString();
		else throw new Exception(String.format("Error creating policy %d",httpcode));
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
