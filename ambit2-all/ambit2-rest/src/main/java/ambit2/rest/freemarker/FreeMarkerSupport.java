package ambit2.rest.freemarker;

import java.util.Map;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.rest.ChemicalMediaType;

public class FreeMarkerSupport implements IFreeMarkerSupport {

	protected boolean htmlbyTemplate = false;
	protected String templateName = null;

	public FreeMarkerSupport() {
		this(null);
	}
	public FreeMarkerSupport(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public boolean isHtmlbyTemplate() {
		return htmlbyTemplate;
	}

	public void setHtmlbyTemplate(boolean htmlbyTemplate) {
		this.htmlbyTemplate = htmlbyTemplate;
	}
	
	public void configureTemplateMap(Map<String, Object> map, Request request, FreeMarkerApplication app) {

		
        Form query = request.getResourceRef().getQueryAsForm();
        //query.removeAll("page");query.removeAll("pagesize");query.removeAll("max");
        query.removeAll("media");
        
		Reference r = request.getResourceRef().clone();
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
	    map.put("ambit_root",request.getRootRef());

	    map.put("ambit_version_short",app.getVersionShort());
	    map.put("ambit_version_long",app.getVersionLong());
	}
		
}