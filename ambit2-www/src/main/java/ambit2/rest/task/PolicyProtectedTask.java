package ambit2.rest.task;

import java.util.Hashtable;

import org.opentox.aa.OpenToxUser;
import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.data.Reference;

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
				Reference newUri = new Reference(getUri().getUri());
				newUri.setQuery(null);
				
				/*
				OpenToxUser user = new OpenToxUser();
				int code = policy.getURIOwner(ssoToken, newUri.toString(), user);
				if (200==code) {
					if ((user.getUsername()!=null) && !user.getUsername().equals("null")) throw new Exception("Has a policy");
				} 
				*/
				try {
					policy.createUserPolicy(results.get("uid"), ssoToken, newUri.toString(), new String[] {"GET","PUT","POST","DELETE"});
				} catch (Exception x ) {
					//TODO write smth in the db why policy creation failed
					//x.printStackTrace();
				}
			}
		} 
		
	}
}
