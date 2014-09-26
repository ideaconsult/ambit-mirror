package ambit2.user.aa;

import java.util.HashMap;
import java.util.Map;

import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.aa.local.UserLoginFormResource;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import ambit2.base.config.AMBITConfig;
import ambit2.user.rest.resource.AMBITDBRoles;
import ambit2.user.rest.resource.DBRoles;


public class AMBITLoginFormResource extends UserLoginFormResource<User> {
	
	public AMBITLoginFormResource() {
		super();
		setHtmlbyTemplate(true);
	}
	
	@Override
	public String getTemplateName() {
		return "a/login.ftl";
	}
	
	@Override
	protected Reporter createHTMLReporter(boolean headles) {
		return null;
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			User user = getRequest().getClientInfo().getUser();
			if ((user!=null) && (user.getIdentifier()!=null)) {
				 this.getResponse().redirectSeeOther(String.format("%s%s",getRequest().getRootRef(),"/ui"));
				 return null;
			}	
		}
		return super.get(variant);
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
				}		        
		        try {
		        	map.put(AMBITConfig.ambit_version_short.name(),((IFreeMarkerApplication)getApplication()).getVersionShort());
			    	map.put(AMBITConfig.ambit_version_long.name(),((IFreeMarkerApplication)getApplication()).getVersionLong());
		        } catch (Exception x) {}
		        map.put(AMBITConfig.googleAnalytics.name(),((IFreeMarkerApplication)getApplication()).getGACode());
			    map.put(AMBITConfig.creator.name(),"IdeaConsult Ltd.");
			    map.put(AMBITConfig.ambit_root.name(),getRequest().getRootRef().toString());

		        getRequest().getResourceRef().addQueryParameter("media", Reference.encode(MediaType.APPLICATION_JSON.toString()));
		        map.put(AMBITConfig.ambit_request.name(),getRequest().getResourceRef().toString());
		        return toRepresentation(map, getTemplateName(), MediaType.TEXT_PLAIN);

	}
}
