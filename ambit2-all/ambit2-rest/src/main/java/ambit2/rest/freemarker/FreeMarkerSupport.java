package ambit2.rest.freemarker;

import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.freemarker.IFreeMarkerSupport;

import org.owasp.encoder.Encode;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.config.AMBITConfig;
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
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app) {
//	public void configureTemplateMap(Map<String, Object> map, Request request, FreeMarkerApplication app) {

		
        Form query = request.getResourceRef().getQueryAsForm();
        //query.removeAll("page");query.removeAll("pagesize");query.removeAll("max");
        query.removeAll("media");
        
		Reference r = cleanedResourceRef(request.getResourceRef());
        r.setQuery(query.getQueryString());
        map.put(AMBITConfig.ambit_request.name(),r.toString()) ;
        if (query.size()>0)	map.put("ambit_query",query.getQueryString()) ;
        
        //json
        query.removeAll("media");query.add("media", MediaType.APPLICATION_JSON.toString());
        r.setQuery(query.getQueryString());
        map.put(AMBITConfig.ambit_request_json.name(),r.toString());
        //jsonp
        query.removeAll("media");query.add("media", MediaType.APPLICATION_JAVASCRIPT.toString());
        r.setQuery(query.getQueryString());
        map.put(AMBITConfig.ambit_request_jsonp.name(),r.toString());      
        //sdf
        query.removeAll("media");query.add("media", ChemicalMediaType.CHEMICAL_MDLSDF.toString());
        r.setQuery(query.getQueryString());
        map.put(AMBITConfig.ambit_request_sdf.name(),r.toString());   
        //csv
        query.removeAll("media");query.add("media", MediaType.TEXT_CSV.toString());
        r.setQuery(query.getQueryString());
        map.put(AMBITConfig.ambit_request_csv.name(),r.toString());
        

		map.put("creator","Ideaconsult Ltd.");
        map.put(AMBITConfig.ambit_root.name(),request.getRootRef());
	    map.put(AMBITConfig.ambit_version_short.name(),app.getVersionShort());
	    map.put(AMBITConfig.ambit_version_long.name(),app.getVersionLong());
	    map.put(AMBITConfig.menu_profile.name(),app.getProfile());
	}
	
	protected Reference cleanedResourceRef(Reference ref) {
		return new Reference(Encode.forJavaScriptSource(ref.toString()));
	}	
		
}