package ambit2.rest.task;

import java.util.Hashtable;

import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;

import ambit2.rest.aa.opensso.OpenSSOServicesConfig;

public class PolicyProtectedTask extends Task<TaskResult, String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5631652362392618557L;

	public PolicyProtectedTask(String user) {
		super(user);	
	}
	@Override
	public synchronized void setPolicy() throws Exception {
		if (!getUri().isNewResource()) return;
		OpenSSOServicesConfig config = OpenSSOServicesConfig.getInstance();
		
		if (config.isEnabled()) {
			if (getUserid()==null) { //policy for everybody
				OpenSSOToken ssoToken = new OpenSSOToken(config.getOpenSSOService());
				try {
					if (ssoToken.login("guest","guest")) {
						OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
						
						policy.createGroupPolicy(
								"member", 
								ssoToken, 
								getUri().toString(), 
								new String[] {"GET","PUT","POST","DELETE"});
					}
				} catch (Exception x) {
					x.printStackTrace();
				} finally {
					try {ssoToken.logout(); } catch (Exception x) {}
				}
				
			}  else { //policy for the user only
				OpenSSOToken ssoToken = new OpenSSOToken(config.getOpenSSOService());
				ssoToken.setToken(getUserid());
				Hashtable<String, String> results = new Hashtable<String, String>();
				ssoToken.getAttributes(new String[] {"uid"}, results);
				OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
				//user policy
				policy.createUserPolicy(results.get("uid"), ssoToken, getUri().toString(), new String[] {"GET","PUT","POST","DELETE"});
			}
		} 
		
	}
}
