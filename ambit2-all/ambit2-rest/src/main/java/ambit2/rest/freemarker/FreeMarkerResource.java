package ambit2.rest.freemarker;

import java.util.HashMap;
import java.util.Map;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.ChemicalMediaType;
import ambit2.rest.ProtectedResource;

/**
 * Renders HTML by freemarker template, if htmlByTemplate is true. Otherwise, uses the old behaviour (htmlreporter).
 * @author nina
 *
 */
public class FreeMarkerResource extends ProtectedResource {
	protected boolean htmlbyTemplate = false;
	/*
	 *  HTML via Freemarker
	 */
	public boolean isHtmlbyTemplate() {
		return htmlbyTemplate;
	}
	/**
	 * 
	 * @param htmlbyTemplate
	 */
	public void setHtmlbyTemplate(boolean htmlbyTemplate) {
		this.htmlbyTemplate = htmlbyTemplate;
	}
	/**
	 * 
	 * @return
	 */
	public String getTemplateName() {
		return null;
	}
	
	protected Reference getSearchReference(Context context, Request request,Response response) throws ResourceException {
		return request.getResourceRef();
	}
	
	/**
	 * 
	 * @param map
	 */
	protected void configureTemplateMap(Map<String, Object> map) {
	
        Form query = getRequest().getResourceRef().getQueryAsForm();
        //query.removeAll("page");query.removeAll("pagesize");query.removeAll("max");
        query.removeAll("media");
        
		Reference r = getRequest().getResourceRef().clone();
        r.setQuery(query.getQueryString());
        map.put("ambit_request",r.toString()) ;
        if (query.size()>0)	map.put("ambit_query",query.getQueryString()) ;
        
        //json
        query.removeAll("media");query.add("media", MediaType.APPLICATION_JSON.toString());
        r.setQuery(query.getQueryString());
        map.put("ambit_request_json",r.toString());
        //jsonp
        query.removeAll("media");query.add("media", MediaType.APPLICATION_JAVASCRIPT.toString());
        r.setQuery(query.getQueryString());
        map.put("ambit_request_jsonp",r.toString());      
        //sdf
        query.removeAll("media");query.add("media", ChemicalMediaType.CHEMICAL_MDLSDF.toString());
        r.setQuery(query.getQueryString());
        map.put("ambit_request_sdf",r.toString());   
        //csv
        query.removeAll("media");query.add("media", MediaType.TEXT_CSV.toString());
        r.setQuery(query.getQueryString());
        map.put("ambit_request_csv",r.toString());
        

		map.put("creator","Ideaconsult Ltd.");
	    map.put("ambit_root",getRequest().getRootRef());
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
		        configureTemplateMap(map);
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
	        		((FreeMarkerApplication)getApplication()).getConfiguration(),
	        		map,
	        		MediaType.TEXT_HTML);
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		if (isHtmlbyTemplate() && MediaType.TEXT_HTML.equals(variant.getMediaType())) {
			CookieSetting cS = new CookieSetting(0, "subjectid", getToken());
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
