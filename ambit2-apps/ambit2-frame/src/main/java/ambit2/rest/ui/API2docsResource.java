package ambit2.rest.ui;

import java.util.Map;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.rest.AmbitFreeMarkerApplication;
import ambit2.rest.freemarker.FreeMarkerResource;
import ambit2.user.rest.resource.AMBITDBRoles;
import ambit2.user.rest.resource.DBRoles;
import freemarker.template.Configuration;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

/**
 * Swagger2
 * 
 * @author nina
 *
 */
public class API2docsResource extends FreeMarkerResource {
	final static private MediaType media_yaml = new MediaType("text/vnd.yaml");

	public API2docsResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "apidocs2/api.ftl";
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		getVariants().clear();
		getVariants().add(new Variant(media_yaml));

	}

	@Override
	protected Representation getHTMLByTemplate(Variant variant) throws ResourceException {

		return super.getHTMLByTemplate(variant);
	}

	@Override
	protected Representation toRepresentation(Map<String, Object> map, String templateName, MediaType mediaType) {
		return new TemplateRepresentation(templateName,
				(Configuration) ((IFreeMarkerApplication) getApplication()).getConfiguration(), map, media_yaml);
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		setFrameOptions("SAMEORIGIN");
		return getHTMLByTemplate(variant);
	}

	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {

		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation put(Representation representation, Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		try {
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

		} catch (Exception x) {

		}
		try {
			map.put(AMBITConfig.ajaxtimeout.name(), ((AmbitFreeMarkerApplication) getApplication()).getAjaxTimeout());
		} catch (Exception x) {
			map.put(AMBITConfig.ajaxtimeout.name(), "10000");
		}
		try {
			map.put(AMBITConfig.service_search.name(),
					((AmbitFreeMarkerApplication) getApplication()).getProperties().getSearchServiceURI(getRequest().getRootRef().toString()));
		} catch (Exception x) {
		}
		try {
			map.put(AMBITConfig.custom_query.name(), ((AmbitFreeMarkerApplication) getApplication()).getProperties().getCustomQuery());
		} catch (Exception x) {
		}
		try {
			map.put(AMBITConfig.custom_structurequery.name(),
					((AmbitFreeMarkerApplication) getApplication()).getProperties().getCustomStructureQuery());
		} catch (Exception x) {
		}

		try {
			map.put(AMBITConfig.custom_title.name(), ((AmbitFreeMarkerApplication) getApplication()).getProperties().getCustomTitle());
		} catch (Exception x) {
		}
		try {
			map.put(AMBITConfig.custom_logo.name(), ((AmbitFreeMarkerApplication) getApplication()).getProperties().getCustomLogo());
		} catch (Exception x) {
		}
		try {
			map.put(AMBITConfig.custom_description.name(),
					((AmbitFreeMarkerApplication) getApplication()).getProperties().getCustomDescription());
		} catch (Exception x) {
		}
		try {
			map.put(AMBITConfig.custom_license.name(),
					((AmbitFreeMarkerApplication) getApplication()).getProperties().getCustomLicense());
		} catch (Exception x) {
		}
	}
}
