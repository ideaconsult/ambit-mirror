package ambit2.rest.aa.opensso;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	protected static Logger logger = Logger.getLogger(OpenSSOAuthorizer.class.getName());
	protected String prefix = null;
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String u) {
		this.prefix = u;//==null?null:u.lastIndexOf("/")==u.length()-1?u:String.format("%s/",u);
	}

	@Override
	protected boolean authorize(Request request, Response response) {
		if (!isEnabled()) return true;
		String token = null;
		Form headers = (Form) request.getAttributes().get("org.restlet.http.headers");  
		if (headers !=null) 
			token = headers.getFirstValue(OTAAParams.subjectid.toString());
		
		if (token == null) token = getTokenFromCookies(request);
		
		if (token != null) {
			OpenSSOToken ssoToken = null;
			try {
				ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
				ssoToken.setToken(token);

				return authorize(ssoToken,request);
			} catch (Exception x) {
				logger.log(Level.WARNING,x.getMessage(),x);
				return false;
			}
		} else return false;		

	}

	protected boolean isEnabled() {
		try {
		return OpenSSOServicesConfig.getInstance().isEnabled();
		} catch (Exception x) {return true;}
	}
	
	public String uri2check(Reference root,Reference ref) throws Exception {
		if (prefix==null) return ref==null?null:ref.toString();
	    if (ref == null) return null;
	    
	    String u = root.toString();
		Reference fullPrefix = new Reference(String.format("%s%s%s/", 
					u,
					u.lastIndexOf("/")==u.length()-1?"":"/",
					prefix));
		
		u = ref.toString();
		Reference uri = new Reference(String.format("%s%s", 
				u,
				u.lastIndexOf("/")==u.length()-1?"":"/"
				));
		u = ref.toString();
		Reference uri2check = new Reference(u==null?null:
										u.lastIndexOf("/")==u.length()-1?u:String.format("%s/",u)); //add trailing slash
		int prefix_len = fullPrefix.toString().length();
		while (!fullPrefix.equals(uri)) {
			uri2check = uri;
			uri = uri.getParentRef();
			if (uri.toString().length()<prefix_len) return null; //smth wrong
		}
		u = uri2check.toString();
		if (u.lastIndexOf("/")==(u.length()-1))
			return u.substring(0,u.length()-1);
		else return u;
	}
	
	protected boolean authorize(OpenSSOToken ssoToken, Request request)  throws Exception {
		Reference ref = request.getResourceRef().clone();
		ref.setQuery(null);
		String uri = uri2check(request.getRootRef(),ref);
		if (uri==null) return false;
		if (ssoToken.authorize(uri,request.getMethod().toString()))  {
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

		}
		return null;
	}
}
