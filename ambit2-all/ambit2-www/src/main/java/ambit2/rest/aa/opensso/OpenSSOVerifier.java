package ambit2.rest.aa.opensso;

import org.opentox.aa.OTAAParams;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.security.User;
import org.restlet.security.Verifier;

/**
 * subjectid=token , as header parameter 
 * 
 * @author nina
 *
 */
public class OpenSSOVerifier implements Verifier {
	protected boolean enabled = false;
	
	public OpenSSOVerifier() {
		this( OpenSSOServicesConfig.getInstance().isEnabled());
	}
	public OpenSSOVerifier(boolean enabled) {
		this.enabled = enabled;
	}
	public int verify(Request request, Response response) {
		
		
		Form headers = (Form) request.getAttributes().get("org.restlet.http.headers");  
		if (headers==null) 
			return enabled?Verifier.RESULT_MISSING:Verifier.RESULT_VALID;
		
		String token = headers.getFirstValue(OTAAParams.subjectid.toString());
		if (token != null) {
			OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
			ssoToken.setToken(token);
			try {
				if (ssoToken.isTokenValid()) {
					setUser(ssoToken, request);
					return Verifier.RESULT_VALID;
				} else 
					return enabled?Verifier.RESULT_INVALID:Verifier.RESULT_VALID;
			} catch (Exception x) {
				x.printStackTrace(); //TODO
				return enabled?Verifier.RESULT_MISSING:Verifier.RESULT_VALID;
			}
		} else
			return enabled?Verifier.RESULT_MISSING:Verifier.RESULT_VALID;

	}
	
	protected void setUser(OpenSSOToken ssoToken,Request request) throws Exception {
		request.getClientInfo().setUser(createUser(ssoToken, request));
	}
	protected User createUser(OpenSSOToken ssoToken,Request request) throws Exception {
		User user = new User();
		user.setSecret(ssoToken.getToken().toCharArray());
		return user;
	}
}
