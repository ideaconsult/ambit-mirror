package ambit2.rest.aa.opensso;

import java.util.Hashtable;

import org.opentox.aa.OTAAParams;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Cookie;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.security.Authorizer;

public class OpenSSOAuthorizer extends Authorizer {

	@Override
	protected boolean authorize(Request request, Response response) {
		if (!isEnabled()) return true;
		String token = null;
		Form headers = (Form) request.getAttributes().get("org.restlet.http.headers");  
		if (headers !=null) 
			token = headers.getFirstValue(OTAAParams.subjectid.toString());
		
		if (token == null) token = getTokenFromCookies(request);
		
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
		if (ssoToken.authorize(ref.toString(),request.getMethod().toString()))  {
			if (!Method.GET.equals(request.getMethod())){
				try {retrieveUserAttributes(ssoToken, request);} 
				catch (Exception x) {}
			}
			return true;
		} else return false;
	}
	
	protected void retrieveUserAttributes(OpenSSOToken ssoToken, Request request) throws Exception {
		Hashtable<String,String> results = new Hashtable<String, String>();
		ssoToken.getAttributes(new String[] {"uid"}, results);
		request.getClientInfo().getUser().setIdentifier(results.get("uid"));
	}
	protected String getTokenFromCookies(Request request) {
		for (Cookie cookie : request.getCookies()) {
			if ("subjectid".equals(cookie.getName()))
				return cookie.getValue();
				/*	
		    System.out.println("name = " + cookie.getName());
		    System.out.println("value = " + cookie.getValue());
		    System.out.println("domain = " + cookie.getDomain());
		    System.out.println("path = " + cookie.getPath());
		    System.out.println("version = " + cookie.getVersion());
		    */
		}
		return null;
	}
}
