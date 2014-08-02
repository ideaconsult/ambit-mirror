package ambit2.user.aa;

import java.util.Map;

import net.idea.restnet.aa.local.UserLoginPOSTResource;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Request;
import org.restlet.security.User;

import ambit2.base.config.AMBITConfig;

public class AMBITLoginPOSTResource<U extends User> extends UserLoginPOSTResource<U> {

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map,request, app);
        map.put(AMBITConfig.ambit_version_short.name(),app.getVersionShort());
	    map.put(AMBITConfig.ambit_version_long.name(),app.getVersionLong());
	    map.put(AMBITConfig.googleAnalytics.name(),app.getGACode());
	    map.put(AMBITConfig.menu_profile.name(),app.getProfile());
	}
}
