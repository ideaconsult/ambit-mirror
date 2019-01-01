package ambit2.user.aa;

import java.util.Map;

import net.idea.restnet.aa.local.UserLoginPOSTResource;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.owasp.encoder.Encode;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import ambit2.base.config.AMBITConfig;
import ambit2.user.rest.resource.AMBITDBRoles;
import ambit2.user.rest.resource.DBRoles;

public class AMBITLoginPOSTResource<U extends User> extends
		UserLoginPOSTResource<U> {

	public AMBITLoginPOSTResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "a/login.ftl";
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		//if (getClientInfo() != null && getClientInfo().getUser() != null)		doRedirect(getRequest().getResourceRef().getQueryAsForm());

		return super.get(variant);

	}

	protected void doRedirect(Form form) {
		String redirect = null;
		try {
			redirect = form.getFirstValue("targetUri", true);

		} catch (Exception x) {
			redirect = null;
		}
		if (redirect == null)
			this.getResponse().redirectSeeOther(
					String.format("%s/login", getRequest().getRootRef()));
		else
			this.getResponse().redirectSeeOther(redirect);
			
	}

	@Override
	protected Representation get() throws ResourceException {
		if (getClientInfo() != null && getClientInfo().getUser() != null)
			doRedirect(getRequest().getResourceRef().getQueryAsForm());
		return super.get();
	}

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {

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
				if (DBRoles.isUser(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_user.name(), Boolean.TRUE);
				if (DBRoles.isModeller(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_modeller.name(), Boolean.TRUE);
			}
		}

		map.put(AMBITConfig.creator.name(), "IdeaConsult Ltd.");
		map.put(AMBITConfig.ambit_root.name(), getRequest().getRootRef()
				.toString());
		map.put(AMBITConfig.ambit_version_short.name(), app.getVersionShort());
		map.put(AMBITConfig.ambit_version_long.name(), app.getVersionLong());
		map.put(AMBITConfig.googleAnalytics.name(), app.getGACode());
		map.put(AMBITConfig.menu_profile.name(), app.getProfile());

		// remove paging
		Form query = getRequest().getResourceRef().getQueryAsForm();
		// query.removeAll("page");query.removeAll("pagesize");query.removeAll("max");
		query.removeAll("media");
		Reference r = cleanedResourceRef(getRequest().getResourceRef());
		r.setQuery(query.getQueryString());

		map.put(AMBITConfig.ambit_request.name(), r.toString());
		if (query.size() > 0)
			map.put(AMBITConfig.ambit_query.name(), query.getQueryString());
		// json
		query.removeAll("media");
		query.add("media", MediaType.APPLICATION_JSON.toString());
		r.setQuery(query.getQueryString());
		map.put(AMBITConfig.ambit_request_json.name(), r.toString());
		// csv
		query.removeAll("media");
		query.add("media", MediaType.TEXT_CSV.toString());
		r.setQuery(query.getQueryString());
		map.put(AMBITConfig.ambit_request_csv.name(), r.toString());

	}

	protected Reference cleanedResourceRef(Reference ref) {
		return new Reference(Encode.forJavaScriptSource(ref.toString()));
	}
}
