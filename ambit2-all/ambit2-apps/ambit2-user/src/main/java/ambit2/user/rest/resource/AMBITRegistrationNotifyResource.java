package ambit2.user.rest.resource;

import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.user.resource.RegistrationNotifyResource;

import org.restlet.Request;

import ambit2.base.config.AMBITConfig;

public class AMBITRegistrationNotifyResource extends RegistrationNotifyResource {

	public AMBITRegistrationNotifyResource() {
		super();
	}

	@Override
	public String getTemplateName() {
		return "a/register_notify.ftl";
	}

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("searchURI", Resources.register);
		map.put(AMBITConfig.ambit_root.name(), getRequest().getRootRef()
				.toString());
		try {
			map.put(AMBITConfig.ambit_version_short.name(),
					((IFreeMarkerApplication) getApplication())
							.getVersionShort());
			map.put(AMBITConfig.ambit_version_long.name(),
					((IFreeMarkerApplication) getApplication())
							.getVersionLong());
			map.put(AMBITConfig.googleAnalytics.name(),
					((IFreeMarkerApplication) getApplication()).getGACode());
			map.put(AMBITConfig.menu_profile.name(), app.getProfile());
		} catch (Exception x) {
		}

		map.put(AMBITDBRoles.ambit_admin.name(), Boolean.FALSE);
		map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.FALSE);
		map.put(AMBITDBRoles.ambit_modeller.name(), Boolean.FALSE);

		if (getClientInfo() != null) {
			if (getClientInfo().getUser() != null)
				map.put("username", getClientInfo().getUser().getIdentifier());
			if (getClientInfo().getRoles() != null) {
				if (DBRoles.isAdmin(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_admin.name(), Boolean.TRUE);
				if (DBRoles.isDatasetManager(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.TRUE);
				if (DBRoles.isModeller(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_modeller.name(), Boolean.TRUE);
				if (DBRoles.isUser(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_user.name(), Boolean.TRUE);
			}
		}
		map.put(AMBITConfig.creator.name(), "IdeaConsult Ltd.");

	}

	public String getConfigFile() {
		return "ambit2/rest/config/config.prop";
	}

}
