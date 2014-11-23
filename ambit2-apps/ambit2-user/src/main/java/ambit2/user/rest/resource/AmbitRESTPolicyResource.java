package ambit2.user.rest.resource;

import java.util.HashMap;
import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.aa.opensso.OpenSSOUser;
import net.idea.restnet.db.aalocal.policy.RESTPolicyResource;
import net.idea.restnet.i.aa.IRESTPolicy;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;

public class AmbitRESTPolicyResource<Q extends IQueryRetrieval<IRESTPolicy<Integer>>> extends RESTPolicyResource<Q> {

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_URI_LIST,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVASCRIPT,
				MediaType.APPLICATION_JAVA_OBJECT
				});	
	}
	public String getConfigFile() {
		return "ambit2/rest/config/config.prop";
	}
	@Override
	public boolean isHtmlbyTemplate() {
		return true;
	}
	@Override
	public String getTemplateName() {
		return "a/restpolicy.ftl";
	}
	@Override
	protected Representation getHTMLByTemplate(Variant variant) throws ResourceException {
        Map<String, Object> map = new HashMap<String, Object>();

    	map.put(AMBITDBRoles.ambit_admin.name(), Boolean.FALSE);
		map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.FALSE);
		if (getClientInfo()!=null) {
			if (getClientInfo().getUser()!=null)
				map.put("username", getClientInfo().getUser().getIdentifier());
			if (getClientInfo().getRoles()!=null) {
				if (DBRoles.isAdmin(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_admin.name(),Boolean.TRUE);
				if (DBRoles.isDatasetManager(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.TRUE);
				if (DBRoles.isUser(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_user.name(), Boolean.TRUE);	
			}
		}  else {
			OpenSSOUser ou = new OpenSSOUser();
			ou.setUseSecureCookie(useSecureCookie(getRequest()));
			getClientInfo().setUser(ou);			
		}
        setTokenCookies(variant, useSecureCookie(getRequest()));
        configureTemplateMap(map,getRequest(),(IFreeMarkerApplication)getApplication());
        return toRepresentation(map, getTemplateName(), MediaType.TEXT_PLAIN);
	}
	
	
	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
        if (getClientInfo().getUser()!=null) 
        	map.put("username", getClientInfo().getUser().getIdentifier());
        map.put(AMBITConfig.creator.name(),"IdeaConsult Ltd.");
        map.put(AMBITConfig.ambit_root.name(),getRequest().getRootRef().toString());
        map.put(AMBITConfig.ambit_version_short.name(),app.getVersionShort());
	    map.put(AMBITConfig.ambit_version_long.name(),app.getVersionLong());
	    map.put(AMBITConfig.googleAnalytics.name(),app.getGACode());
	    map.put(AMBITConfig.menu_profile.name(),app.getProfile());

        //remove paging
        Form query = getRequest().getResourceRef().getQueryAsForm();
        //query.removeAll("page");query.removeAll("pagesize");query.removeAll("max");
        query.removeAll("media");
        Reference r = cleanedResourceRef(getRequest().getResourceRef());
        r.setQuery(query.getQueryString());
        
        map.put(AMBITConfig.ambit_request.name(),r.toString()) ;
        if (query.size()>0)
        	map.put(AMBITConfig.ambit_query.name(),query.getQueryString()) ;
        //json
        query.removeAll("media");query.add("media", MediaType.APPLICATION_JSON.toString());
        r.setQuery(query.getQueryString());
        map.put(AMBITConfig.ambit_request_json.name(),r.toString());
        //csv
        query.removeAll("media");query.add("media", MediaType.TEXT_CSV.toString());
        r.setQuery(query.getQueryString());
        map.put(AMBITConfig.ambit_request_csv.name(),r.toString());
 
	}
}
