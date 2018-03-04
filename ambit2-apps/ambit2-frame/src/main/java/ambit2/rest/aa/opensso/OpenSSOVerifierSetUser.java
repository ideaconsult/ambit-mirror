package ambit2.rest.aa.opensso;

import java.util.Hashtable;
import java.util.logging.Level;

import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Request;
import org.restlet.security.User;

public class OpenSSOVerifierSetUser extends OpenSSOVerifier {
	
	public OpenSSOVerifierSetUser(boolean enabled) {
		super(enabled);
	}
	public OpenSSOVerifierSetUser() {
		super();
	}
	@Override
	protected User createUser(OpenSSOToken ssoToken, Request request)
			throws Exception {

		User user = super.createUser(ssoToken, request);
		Hashtable<String,String> results = new Hashtable<String, String>();
				
		try {
			ssoToken.getAttributes(new String[] {"uid"}, results);

			user.setIdentifier(results.get("uid"));} 
		catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return user;
	}
}
