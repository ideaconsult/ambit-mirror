package ambit2.rest.task;

import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.data.Reference;

import ambit2.rest.aa.opensso.OpenSSOServicesConfig;

public class PolicyProtectedTask extends Task<Reference, String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5631652362392618557L;

	public PolicyProtectedTask(String user) {
		super(user);	
	}
	@Override
	public synchronized void setPolicy() throws Exception {
		
		OpenSSOServicesConfig config = OpenSSOServicesConfig.getInstance();
		if (config.isEnabled()) {
			if (getUserid()==null)  return;
			OpenSSOToken ssoToken = new OpenSSOToken(config.getOpenSSOService());
			ssoToken.setToken(getUserid());
			OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
			policy.createGroupPolicy("opentox", 
					ssoToken, 
					getUri().toString(), 
					new String[] {"GET","PUT","POST","DELETE"});
		}
	}
}
