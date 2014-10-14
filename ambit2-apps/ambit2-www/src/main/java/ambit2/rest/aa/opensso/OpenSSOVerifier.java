package ambit2.rest.aa.opensso;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.opentox.aa.OTAAParams;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Cookie;
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
	protected static Logger logger = Logger.getLogger(OpenSSOVerifier.class.getName());
	protected boolean enabled = false;
	
	public OpenSSOVerifier() {
		this( isEnabled() );
	}
	public static boolean isEnabled() {
		try { return OpenSSOServicesConfig.getInstance().isEnabled();} catch (Exception x) {}
		return true;
	}
	public OpenSSOVerifier(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int verify(Request request, Response response) {
		
		
		Form headers = (Form) request.getAttributes().get("org.restlet.http.headers");
		String token = null;
		if (headers!=null) 
			token = headers.getFirstValue(OTAAParams.subjectid.toString());

		if (token == null) //backup, check cookies
			token = getTokenFromCookies(request);
		
		if (token==null) { //still nothing  
			request.getCookies().removeAll("subjectid");
			return enabled?Verifier.RESULT_MISSING:Verifier.RESULT_VALID;
	    } else token = token.trim();
		
		
		if ((token != null) && (!"".equals(token))) {

			try {
				OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
				ssoToken.setToken(token);				
				if (ssoToken.isTokenValid()) {
					setUser(ssoToken, request);
					return Verifier.RESULT_VALID;
				} else {
					request.getCookies().removeAll("subjectid");
					return enabled?Verifier.RESULT_INVALID:Verifier.RESULT_VALID;
				}
			} catch (Exception x) {
				logger.log(Level.WARNING,x.getMessage(),x);
				return enabled?Verifier.RESULT_MISSING:Verifier.RESULT_VALID;
			}
		} else {
			request.getCookies().removeAll("subjectid");
			return enabled?Verifier.RESULT_MISSING:Verifier.RESULT_VALID;
		}

	}
	
	protected void setUser(OpenSSOToken ssoToken,Request request) throws Exception {
		request.getClientInfo().setUser(createUser(ssoToken, request));
	}
	protected User createUser(OpenSSOToken ssoToken,Request request) throws Exception {
		OpenSSOUser user = new OpenSSOUser();
		user.setToken(ssoToken.getToken());
		user.setUseSecureCookie(useSecureCookie(request));
		request.getCookies().removeAll("subjectid");
		request.getCookies().add("subjectid",ssoToken.getToken());
		return user;
	}
	
	protected String getTokenFromCookies(Request request) {
		for (Cookie cookie : request.getCookies()) {
			if ("subjectid".equals(cookie.getName()))
				return cookie.getValue();

		}
		return null;
	}
	
	protected boolean useSecureCookie(Request request) {
		for (Cookie cookie : request.getCookies()) {
			if ("subjectid_secure".equals(cookie.getName())) try {
				return Boolean.parseBoolean(cookie.getValue());
			} catch (Exception x) {
			}
		}
		//secure cookie by default
		return true;
	}	
}
