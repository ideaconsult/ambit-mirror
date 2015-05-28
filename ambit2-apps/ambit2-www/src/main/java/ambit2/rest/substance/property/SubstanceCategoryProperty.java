package ambit2.rest.substance.property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.idea.i5.io.I5_ROOT_OBJECTS;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.resource.CatalogResource;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.user.rest.resource.AMBITDBRoles;
import ambit2.user.rest.resource.DBRoles;

public class SubstanceCategoryProperty extends CatalogResource<EffectRecord<String, IParams, String>> {
    protected List<EffectRecord<String, IParams, String>> effects;

    public SubstanceCategoryProperty() {
	super();
	effects = null;
	setHtmlbyTemplate(true);
    }

    @Override
    public String getTemplateName() {
	return "jsonplaceholder.ftl";
    }

    @Override
    protected Iterator<EffectRecord<String, IParams, String>> createQuery(Context context, Request request,
	    Response response) throws ResourceException {
	try {
	    Object topcategory = Reference.decode(request.getAttributes().get(SubstancePropertyResource.topcategory)
		    .toString());
	    Object endpointcategory = Reference.decode(request.getAttributes()
		    .get(SubstancePropertyResource.endpointcategory).toString());
	    if (topcategory == null || endpointcategory == null)
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    I5_ROOT_OBJECTS category;
	    try {
		category = I5_ROOT_OBJECTS.valueOf(endpointcategory.toString().replace("_SECTION", ""));
	    } catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage());
	    }
	    if (!category.getTopCategory().equals(topcategory.toString())) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, String.format("Expected /property/%s/%s",
			category.getTopCategory(), category.name()));
	    }
	    effects = new ArrayList<EffectRecord<String, IParams, String>>();

	    if (category.getEndpoints() != null && category.getEndpoints().length > 0)
		for (String endpoint : category.getEndpoints()) {
		    EffectRecord record = category.createEffectRecord();
		    record.setEndpoint(endpoint);
		    effects.add(record);
		}
	    else
		effects.add(category.createEffectRecord());
	    return effects.iterator();
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage());
	}
    }

    protected Form getParams() {
	return getResourceRef(getRequest()).getQueryAsForm();
    }

    @Override
    public IProcessor<Iterator<EffectRecord<String, IParams, String>>, Representation> createJSONConvertor(
	    Variant variant, String filenamePrefix) throws AmbitException, ResourceException {
	if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
	    String jsonpcallback = getParams().getFirstValue("jsonp");
	    if (jsonpcallback == null)
		jsonpcallback = getParams().getFirstValue("callback");
	    EffectRecordJSONReporter cmpreporter = new EffectRecordJSONReporter(getRequest(), jsonpcallback);
	    return new StringConvertor(cmpreporter, MediaType.APPLICATION_JAVASCRIPT, filenamePrefix);
	} else { // json by default
	    EffectRecordJSONReporter cmpreporter = new EffectRecordJSONReporter(getRequest(), null);
	    return new StringConvertor(cmpreporter, MediaType.APPLICATION_JSON, filenamePrefix);
	}
    }
    
    @Override
    public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app) {
	super.configureTemplateMap(map, request, app);
	try {
	    map.put(AMBITConfig.ambit_version_short.name(),
		    ((IFreeMarkerApplication) getApplication()).getVersionShort());
	    map.put(AMBITConfig.ambit_version_long.name(), ((IFreeMarkerApplication) getApplication()).getVersionLong());
	    map.put(AMBITConfig.googleAnalytics.name(), ((IFreeMarkerApplication) getApplication()).getGACode());
	    map.put(AMBITConfig.menu_profile.name(), app.getProfile());
	} catch (Exception x) {
	}
	map.put(AMBITDBRoles.ambit_admin.name(), Boolean.FALSE);
	map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.FALSE);
	map.put(AMBITConfig.ambit_request.name(),getRequest().getResourceRef().toString());
	
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
	    }
	}
	map.put(AMBITConfig.creator.name(), "IdeaConsult Ltd.");
	map.put(AMBITConfig.ambit_root.name(), getRequest().getRootRef());

    }
}
