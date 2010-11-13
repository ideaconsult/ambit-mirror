package ambit2.rest.aa.opensso;

import org.opentox.aa.OTAAParams;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.Verifier;

/**
 * subjectid=token , as form parameter - !!! query parameter, not form parameter !!!
 * 
 * @author nina
 *
 */
public class OpenSSOVerifier implements Verifier {

	public int verify(Request request, Response response) {
		if (!OpenSSOServicesConfig.getInstance().isEnabled()) return Verifier.RESULT_VALID;
		String token = request.getResourceRef().getQueryAsForm().getFirstValue(OTAAParams.subjectid.toString());
		if (token != null) {
			OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
			ssoToken.setToken(token);
			try {
				return ssoToken.isTokenValid()?Verifier.RESULT_VALID:Verifier.RESULT_INVALID;
			} catch (Exception x) {
				x.printStackTrace(); //TODO
				return Verifier.RESULT_MISSING;
			}
		} else return Verifier.RESULT_MISSING;

	}

}
