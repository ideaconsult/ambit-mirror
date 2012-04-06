package ambit2.rest;

import java.util.Iterator;

import org.opentox.dsl.aa.IAuthToken;
import org.opentox.dsl.task.ClientResourceWrapper;
import org.restlet.Request;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.FaultInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public abstract class ProtectedResource extends ServerResource implements IAuthToken {

		
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		try {ClientResourceWrapper.setTokenFactory(this);} catch (Exception x){}
		BotsGuard.checkForBots(getRequest());
	}
	
	@Override
	protected void doRelease() throws ResourceException {
		try {ClientResourceWrapper.setTokenFactory(null);} catch (Exception x){}
		super.doRelease();
	}
	

	protected String getTokenFromCookies(Request request) {
		for (Cookie cookie : request.getCookies()) {
			if ("subjectid".equals(cookie.getName()))
				return cookie.getValue();
		}
		return null;
	}
	@Override
	public String getToken() {
		String token = getHeaderValue("subjectid");
		
		if (token == null) token = getTokenFromCookies(getRequest());
		return token== null?null:token;
		 
	}

	
	protected String getUserName() {
		return getHeaderValue("user");
	}
	protected String getPassword() {
		return getHeaderValue("password");
	}	
	private String getHeaderValue(String tag) {
		try {
			Form headers = (Form) getRequest().getAttributes().get("org.restlet.http.headers");  
			if (headers==null) return null;
			return headers.getFirstValue(tag);
		} catch (Exception x) {
			return null;
		}
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
		
	protected Reference getResourceRef(Request request) {
		//return request.getOriginalRef()==null?request.getResourceRef():request.getResourceRef();
		return request.getResourceRef();
	}	
	
	protected void setTokenCookies(Variant variant, boolean secure) {
		CookieSetting cS = new CookieSetting(0, "subjectid", getToken());
		cS.setSecure(secure);
		cS.setComment("OpenSSO token");
		cS.setPath("/");
        this.getResponse().getCookieSettings().add(cS);
        //
		cS = new CookieSetting(0, "subjectid_secure", Boolean.toString(secure));
		cS.setSecure(false);
		cS.setComment("Whether to transfer OpenSSO in secure token");
		cS.setPath("/");
        this.getResponse().getCookieSettings().add(cS);
       
	}	

	/*
	protected void describeGet(MethodInfo info) {
        info.setIdentifier("item");
        info.setDocumentation("To retrieve details of a specific item");

        Iterator<Variant> vars = getVariants().iterator();
        while (vars.hasNext()) {
        	Variant var = vars.next();
            RepresentationInfo repInfo = new RepresentationInfo(var.getMediaType());
            //repInfo.setXmlElement("item");
            repInfo.setDocumentation(String.format("%s representation",var.getMediaType()));
            info.getResponse().getRepresentations().add(repInfo);        	
        }


        FaultInfo faultInfo = new FaultInfo(Status.CLIENT_ERROR_NOT_FOUND,"Not found");
        faultInfo.setIdentifier("itemError");
        faultInfo.setMediaType(MediaType.TEXT_HTML);
        info.getResponse().getFaults().add(faultInfo);

	}
	*/
}
