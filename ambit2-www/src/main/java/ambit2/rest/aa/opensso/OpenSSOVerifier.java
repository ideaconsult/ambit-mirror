package ambit2.rest.aa.opensso;

import org.opentox.aa.OTAAParams;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.security.User;
import org.restlet.security.Verifier;

/**
 * subjectid=token , as form parameter - !!! query parameter, not form parameter !!!
 * 
 * @author nina
 *
 */
public class OpenSSOVerifier implements Verifier {

	public OpenSSOVerifier() {

	}
	public int verify(Request request, Response response) {
		if (!OpenSSOServicesConfig.getInstance().isEnabled()) return Verifier.RESULT_VALID;
		
		Form headers = (Form) request.getAttributes().get("org.restlet.http.headers");  
		if (headers==null) return Verifier.RESULT_MISSING;
		
		String token = headers.getFirstValue(OTAAParams.subjectid.toString());
		if (token != null) {
			OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
			ssoToken.setToken(token);
			try {
				if (ssoToken.isTokenValid()) {
					User user = new User();
					user.setSecret(ssoToken.getToken().toCharArray());
					request.getClientInfo().setUser(user);
					return Verifier.RESULT_VALID;
				} else 
					return Verifier.RESULT_INVALID;
			} catch (Exception x) {
				x.printStackTrace(); //TODO
				return  Verifier.RESULT_MISSING;
			}
		} else return Verifier.RESULT_MISSING;

	}

}
