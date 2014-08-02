package ambit2.user.aa;

import java.util.Map;

import net.idea.restnet.aa.local.UserLogoutPOSTResource;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Request;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import ambit2.base.config.AMBITConfig;

public class AMBITLogoutPOSTResource<U extends User> extends UserLogoutPOSTResource<U> {

	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
			
	     this.getResponse().redirectSeeOther(String.format("%s/",getRequest().getRootRef()));
	     return null;
	}
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
        map.put(AMBITConfig.ambit_version_short.name(),app.getVersionShort());
	    map.put(AMBITConfig.ambit_version_long.name(),app.getVersionLong());
	    map.put(AMBITConfig.googleAnalytics.name(),app.getGACode());
	    map.put(AMBITConfig.menu_profile.name(),app.getProfile());
	}
}
