package ambit2.rest.freemarker;

import java.util.HashMap;
import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.freemarker.IFreeMarkerSupport;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CookieSetting;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.ProtectedResource;
import freemarker.template.Configuration;

/**
 * Renders HTML by freemarker template, if htmlByTemplate is true. Otherwise, uses the old behaviour (htmlreporter).
 * @author nina
 *
 */
public class FreeMarkerResource extends ProtectedResource implements IFreeMarkerSupport{
	
	protected IFreeMarkerSupport fmSupport = new FreeMarkerSupport();
	/*
	 *  HTML via Freemarker
	 */
	public boolean isHtmlbyTemplate() {
		return fmSupport.isHtmlbyTemplate();
	}
	/**
	 * 
	 * @param htmlbyTemplate
	 */
	public void setHtmlbyTemplate(boolean htmlbyTemplate) {
		fmSupport.setHtmlbyTemplate(htmlbyTemplate);
	}
	/**
	 * 
	 * @return
	 */
	public String getTemplateName() {
		return fmSupport.getTemplateName();
	}
	
	protected Reference getSearchReference(Context context, Request request,Response response) throws ResourceException {
		return request.getResourceRef();
	}
	
	public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app) {
		fmSupport.configureTemplateMap(map, getRequest(), ((IFreeMarkerApplication)getApplication()));
	}

	
	
	/**
	 * @param variant
	 * @return
	 * @throws ResourceException
	 */
	protected Representation getHTMLByTemplate(Variant variant) throws ResourceException {
		        Map<String, Object> map = new HashMap<String, Object>();
		        if (getClientInfo().getUser()!=null) 
		        	map.put("username", getClientInfo().getUser().getIdentifier());
		        
		        setTokenCookies(variant, useSecureCookie(getRequest()));
		        configureTemplateMap(map, null, null);
		        return toRepresentation(map, getTemplateName(), MediaType.TEXT_PLAIN);
		}
		
	/**
	 * 
	 * @param map
	 * @param templateName
	 * @param mediaType
	 * @return
	 */
	protected Representation toRepresentation(Map<String, Object> map,
	            String templateName, MediaType mediaType) {
	        
	        return new TemplateRepresentation(
	        		templateName,
	        		(Configuration)((IFreeMarkerApplication)getApplication()).getConfiguration(),
	        		map,
	        		MediaType.TEXT_HTML);
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		setFrameOptions("SAMEORIGIN");
		if (isHtmlbyTemplate() && MediaType.TEXT_HTML.equals(variant.getMediaType())) {
			CookieSetting cS = new CookieSetting(0, "subjectid", getToken());
			cS.setAccessRestricted(true);
			cS.setPath("/");
	        this.getResponse().getCookieSettings().add(cS);
	        return getHTMLByTemplate(variant);
    	} else				
    		return getRepresentation(variant);
	}
	
	protected Representation getRepresentation(Variant variant) throws ResourceException {
		return super.get(variant);
	}
}
