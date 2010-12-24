package ambit2.rest.aa.opensso;

import org.opentox.aa.OTAAParams;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.security.Authorizer;

public class OpenSSOAuthorizer extends Authorizer {

	@Override
	protected boolean authorize(Request request, Response response) {
		if (!isEnabled()) return true;
		
		Form headers = (Form) request.getAttributes().get("org.restlet.http.headers");  
		if (headers==null) return false;
		
		String token = headers.getFirstValue(OTAAParams.subjectid.toString());
		if (token != null) {
			OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
			ssoToken.setToken(token);
			try {

				return authorize(ssoToken,request);
			} catch (Exception x) {
				x.printStackTrace(); //TODO
				return false;
			}
		} else return false;		

	}

	protected boolean isEnabled() {
		return OpenSSOServicesConfig.getInstance().isEnabled();
	}
	protected boolean authorize(OpenSSOToken ssoToken, Request request)  throws Exception {
		Reference ref = request.getResourceRef().clone();
		ref.setQuery(null);
		return ssoToken.authorize(ref.toString(),request.getMethod().toString());
	}

}
