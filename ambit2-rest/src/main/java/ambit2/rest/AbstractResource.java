package ambit2.rest;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.FaultInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.WadlRepresentation;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.base.interfaces.IProcessor;
import ambit2.rest.task.dsl.ClientResourceWrapper;
import ambit2.rest.task.dsl.IAuthToken;

/**
 * Abstract class for resources
 * @author nina
 *
 * @param <Q>
 * @param <T>
 * @param <P>
 */
public abstract class AbstractResource<Q,T extends Serializable,P extends IProcessor<Q, Representation>> 
																	extends WadlServerResource implements IAuthToken {
	protected Q queryObject;
	protected Exception error = null;	
	protected Status response_status = Status.SUCCESS_OK;
	public final static String search_param = "search";
	public final static String property = "property";
	public final static String condition = "condition";
	public final static String caseSensitive = "casesens";
	public final static String returnProperties = "returnProperties";
	
	public final static String max_hits = "max";

	
	protected ResourceDoc documentation = new ResourceDoc();


	public ResourceDoc getDocumentation() {
		return documentation;
	}
	public void setDocumentation(ResourceDoc documentation) {
		this.documentation = documentation;
	}
	public AbstractResource() {
		super();
		setAutoDescribed(true);
	}
	public String[] URI_to_handle() {
		return null;
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		try {ClientResourceWrapper.setTokenFactory(this);} catch (Exception x){}
		BotsGuard.checkForBots(getRequest());
		response_status = Status.SUCCESS_OK;
		queryObject = createQuery(getContext(), getRequest(), getResponse());
		error = null;

	}
	
	@Override
	protected void doRelease() throws ResourceException {
		try {ClientResourceWrapper.setTokenFactory(null);} catch (Exception x){}
		super.doRelease();
	}
	

	protected void customizeVariants(MediaType[] mimeTypes) {
       // List<Variant> variants = new ArrayList<Variant>();
        for (MediaType m:mimeTypes) getVariants().add(new Variant(m));
        //getVariants().put(Method.GET, variants);
        //getVariants().put(Method.POST, variants);
	}
	public abstract P createConvertor(Variant variant) throws AmbitException, ResourceException;
	
	protected   abstract  Q createQuery(Context context, Request request, Response response) throws ResourceException;
	
	@Override
	public List<Variant> getVariants() {
		List<Variant> vars = super.getVariants();
		return vars;
	}
	
	protected void setTokenCookies(Variant variant) {
		CookieSetting cS = new CookieSetting(0, "subjectid", getToken());
		cS.setSecure(true);
		cS.setComment("OpenSSO token");
		cS.setPath("/");
        this.getResponse().getCookieSettings().add(cS);
	}
	@Override
	protected Representation get(Variant variant) throws ResourceException {
	try {
			setTokenCookies(variant);
	        // SEND RESPONSE
	        setStatus(Status.SUCCESS_OK);

			if (variant.getMediaType().equals(MediaType.APPLICATION_WADL)) {
				WadlRepresentation wadl =  new WadlRepresentation(describe());
				//wadl.setApplication(((WadlApplication)getApplication()).getApplicationInfo(getRequest(), getResponse()));

				return wadl;
			} else	
	    	if (MediaType.APPLICATION_JAVA_OBJECT.equals(variant.getMediaType())) {
	    		if ((queryObject!=null) && (queryObject instanceof Serializable))
	    		return new ObjectRepresentation((Serializable)queryObject,MediaType.APPLICATION_JAVA_OBJECT);
	    		else throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE);        		
	    	}
	        if (queryObject != null) {
	        	IProcessor<Q, Representation> convertor = null;

		        	try {
		        		getResponse().setStatus(response_status);
		        		convertor = createConvertor(variant);
			        	Representation r = convertor.process(queryObject);
			        	
			        	return r;
		        	} catch (NotFoundException x) {

		    			getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, new NotFoundException(x.getMessage()));
		    			return null;
		        	} catch (ResourceException x) {
		    			getResponse().setStatus(x.getStatus());
		    			return null;
		        	} catch (Exception x) {

		    			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
		    			return null;
		        	} finally {
		        		
		        	}

	        	
	        } else {
	        	getResponse().setStatus(response_status==null?Status.CLIENT_ERROR_BAD_REQUEST:response_status,error);
	        	return null;	        	
	        }
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
			return null;
		}
	}				
	/**
	 * Returns parameter value and throwsan exception if value is missing of mandatory parameter
	 * @param requestHeaders
	 * @param paramName
	 * @param mandatory
	 * @return
	 * @throws ResourceException
	 */
	protected String getParameter(Form requestHeaders,String paramName,String description, boolean mandatory) throws ResourceException {
		Object o = requestHeaders.getFirstValue(paramName);
		if (o == null)
			if (mandatory)	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Parameter %s [%s] is mandatory!", paramName,description));
			else return null;
		else return o.toString();
	}
	/**
	 * Calls {@link #getParameter(Form, String, boolean)} with false for the last argument
	 * @param requestHeaders
	 * @param paramName
	 * @return
	 * @throws ResourceException
	 */
	protected String getParameter(Form requestHeaders,String paramName,String description) throws ResourceException {
		return getParameter(requestHeaders, paramName,description, false);
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		return post(entity);
	}
	
	@Override
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

}
