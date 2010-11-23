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
		if (!OpenSSOServicesConfig.getInstance().isEnabled()) return true;
		
		Form headers = (Form) request.getAttributes().get("org.restlet.http.headers");  
		if (headers==null) return false;
		
		String token = headers.getFirstValue(OTAAParams.subjectid.toString());
		if (token != null) {
			OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getPolicyService());
			ssoToken.setToken(token);
			try {
				Reference ref = request.getResourceRef().clone();
				ref.setQuery(null);
				return ssoToken.authorize(ref.toString(),request.getMethod().toString());
			} catch (Exception x) {
				x.printStackTrace(); //TODO
				return false;
			}
		} else return false;		

	}

}
