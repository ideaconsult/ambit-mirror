package ambit2.rest.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.idea.i5.io.IQASettings;
import net.idea.i5.io.QASettings;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ITaskResult;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.json.JSONUtils;
import ambit2.rendering.StructureEditorProcessor;
import ambit2.rest.AmbitApplication;
import ambit2.rest.OpenTox;
import ambit2.rest.aa.opensso.OpenSSOUser;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.freemarker.FreeMarkerResource;
import ambit2.rest.substance.CallableSubstanceImporter;
import ambit2.rest.substance.SubstanceURIReporter;
import ambit2.rest.task.CallableFileUpload;
import ambit2.user.rest.resource.AMBITDBRoles;
import ambit2.user.rest.resource.DBRoles;

public class UIResource extends FreeMarkerResource {
    private static Logger logger = Logger.getLogger(UIResource.class.getName());
    private static final String key = "key";
    protected pages page = pages.index;

    private enum pages {
	index {
	    @Override
	    public void setCacheHeaders(Response response) {
		response.getCacheDirectives().add(CacheDirective.publicInfo());
	    }
	},
	query, uploadstruc, uploadprops, uploadsubstance {
	    @Override
	    public boolean enablePOST() {
		return true;
	    }
	},
	uploadsubstance1, updatesubstancei5, predict, login {
	    @Override
	    public void setCacheHeaders(Response response) {
		response.getCacheDirectives().add(CacheDirective.noCache());
	    }
	},
	register {
	    @Override
	    public void setCacheHeaders(Response response) {
		response.getCacheDirectives().add(CacheDirective.noCache());
	    }
	},
	myprofile {
	    @Override
	    public void setCacheHeaders(Response response) {
		response.getCacheDirectives().add(CacheDirective.noCache());
	    }
	},
	uploadstruc_register, uploadprops_batch, uploadprops_biodata, createstruc, dataprep, _dataset, _search, editor {
	    @Override
	    public void setCacheHeaders(Response response) {
		response.getCacheDirectives().add(CacheDirective.publicInfo());
	    }
	},
	knocknock {
	    @Override
	    public boolean editorServices() {
		return true;
	    };
	},
	layout {
	    @Override
	    public boolean editorServices() {
		return true;
	    };
	},
	aromatize {
	    @Override
	    public boolean editorServices() {
		return true;
	    };
	},
	dearomatize {
	    @Override
	    public boolean editorServices() {
		return true;
	    };
	},
	toxtree, taskpage {
	    @Override
	    public void setCacheHeaders(Response response) {
		response.getCacheDirectives().add(CacheDirective.noCache());
	    }
	},
	dataset_comparison, assessment_new {
	    @Override
	    public String getTemplateName() {
		return "ra/ui-matrix";
	    }
	},
	assessment {
	    @Override
	    public String getTemplateName() {
		return "ra/ui-matrix";
	    }
	},
	assessment_report {
	    @Override
	    public String getTemplateName() {
		return "ra/ui-report";
	    }
	},	
	assessment_copy {
	    @Override
	    public String getTemplateName() {
		return "ra/bundles";
	    }
	}; 
	public String getTemplateName() {
	    return name();
	}

	public boolean enablePOST() {
	    return false;
	}

	public boolean editorServices() {
	    return false;
	}

	public void setCacheHeaders(Response response) {
	    response.getCacheDirectives().add(CacheDirective.privateInfo());
	    response.getCacheDirectives().add(CacheDirective.maxAge(2700));
	}
    }

    public UIResource() {
	super();
	setHtmlbyTemplate(true);
    }

    @Override
    public boolean isHtmlbyTemplate() {
	return pages.layout.equals(page) ? false : super.isHtmlbyTemplate();
    }

    @Override
    public String getTemplateName() {
	// Object ui = getRequest().getAttributes().get(key);
	try {
	    // page = pages.valueOf(ui.toString());
	    // return ui==null?"index.ftl":String.format("%s.ftl", page.name());
	    switch (page) {
	    case index: {
		return String.format("menu/profile/%s/index.ftl", ((AmbitApplication) getApplication()).getProfile());
	    }
	    default: {
		return String.format("%s.ftl", page.getTemplateName());
	    }
	    }

	} catch (Exception x) {
	    return String.format("menu/profile/%s/index.ftl", ((AmbitApplication) getApplication()).getProfile());
	}
    }

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	getVariants().add(new Variant(MediaType.TEXT_HTML));
	getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	try {
	    Object ui = getRequest().getAttributes().get(key);
	    page = pages.valueOf(ui.toString());
	} catch (Exception x) {
	    page = pages.index;
	}

    }

    protected Representation getHTMLByTemplate(Variant variant) throws ResourceException {
	Map<String, Object> map = new HashMap<String, Object>();

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

	if (getClientInfo().getUser() == null) {
	    OpenSSOUser ou = new OpenSSOUser();
	    ou.setUseSecureCookie(useSecureCookie(getRequest()));
	    getClientInfo().setUser(ou);
	}
	setTokenCookies(variant, useSecureCookie(getRequest()));
	configureTemplateMap(map, getRequest(), (IFreeMarkerApplication) getApplication());
	return toRepresentation(map, getTemplateName(), MediaType.TEXT_PLAIN);
    }

    @Override
    public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app) {
	super.configureTemplateMap(map, request, app);
	try {
	    map.put(AMBITConfig.ajaxtimeout.name(), ((AmbitApplication) getApplication()).getAjaxTimeout());
	} catch (Exception x) {
	    map.put(AMBITConfig.ajaxtimeout.name(), "10000");
	}

	try {
	    Object bundleURI = OpenTox.params.bundle_uri.getFirstValue(request.getResourceRef().getQueryAsForm());
	    if (bundleURI != null)
		map.put("bundleid", getIdBundle(bundleURI, request));
	} catch (Exception x) {
	}

    }

    protected Integer getIdBundle(Object bundleURI, Request request) {
	if (bundleURI != null) {
	    Object id = OpenTox.URI.bundle.getId(bundleURI.toString(), request.getRootRef());
	    if (id != null && (id instanceof Integer))
		return (Integer) id;
	}
	return null;
    }

    @Override
    protected void setCacheHeaders() {
	if (page != null)
	    page.setCacheHeaders(getResponse());
	else
	    super.setCacheHeaders();

    }

    @Override
    protected Representation getRepresentation(Variant variant) throws ResourceException {
	switch (page) {
	case layout: { // ketcher specifics
	    String smiles = getRequest().getResourceRef().getQueryAsForm().getFirstValue("smiles");
	    if ((smiles != null) && !"".equals(smiles.trim())) {
		IStructureRecord record = new StructureRecord();
		record.setContent(smiles);
		record.setFormat(MOL_TYPE.CSV.name());
		StructureEditorProcessor processor = new StructureEditorProcessor(page.name());
		try {
		    return new StringRepresentation("Ok.\n" + processor.process(record), MediaType.TEXT_PLAIN);
		} catch (Exception x) {
		    return new StringRepresentation("Error.\n", MediaType.TEXT_PLAIN);
		}
	    }
	}
	default: {
	    break;
	}
	}
	return super.getRepresentation(variant);
    }

    @Override
    protected Representation post(Representation entity, Variant variant) throws ResourceException {
	Object ui = getRequest().getAttributes().get(key);
	try {
	    pages page = pages.valueOf(ui.toString());
	    if (page.enablePOST()) {
		return uploadsubstance(entity, variant);
	    } else if (page.editorServices()) {
		Form form = new Form(entity);
		String moldata = form.getFirstValue("moldata");
		IStructureRecord record = new StructureRecord();
		if (moldata != null && !"".equals(moldata))
		    try {
			record.setContent(moldata);
			record.setFormat(MOL_TYPE.SDF.name());
		    } catch (Exception x) {
			return new StringRepresentation("Error.\n" + x.getMessage(), MediaType.TEXT_PLAIN);
		    }

		String smiles = getRequest().getResourceRef().getQueryAsForm().getFirstValue("smiles");
		if ((smiles != null) && !"".equals(smiles)) {
		    record.setContent(smiles);
		    record.setFormat(MOL_TYPE.CSV.name());
		}
		StructureEditorProcessor processor = new StructureEditorProcessor(page.name());
		try {
		    return new StringRepresentation("Ok.\n" + processor.process(record), MediaType.TEXT_PLAIN);
		} catch (Exception x) {
		    return new StringRepresentation("Error.\n", MediaType.TEXT_PLAIN);
		}
	    }
	} catch (Exception x) {
	    logger.fine(x.getMessage());
	}
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);

    }

    protected Representation uploadsubstance(Representation entity, Variant variant) throws ResourceException {
	if ((entity == null) || !entity.isAvailable())
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Empty content");

	if (entity.getMediaType() != null
		&& MediaType.MULTIPART_FORM_DATA.getName().equals(entity.getMediaType().getName())) {
	    DiskFileItemFactory factory = new DiskFileItemFactory();
	    RestletFileUpload upload = new RestletFileUpload(factory);
	    try {
		List<FileItem> items = upload.parseRequest(getRequest());
		StringBuilder json = new StringBuilder();
		json.append("{\"files\": [");
		String delimiter = "";
		QASettings qa = new QASettings();
		qa.clear(); // sets enabled to false and clears all flags
		boolean clearMeasurements = false;
		boolean clearComposition = false;
		for (FileItem file : items)
		    if (file.isFormField()) {
			if ("qaenabled".equals(file.getFieldName()))
			    try {
				if ("on".equals(file.getString()))
				    qa.setEnabled(true);
				if ("yes".equals(file.getString()))
				    qa.setEnabled(true);
				if ("checked".equals(file.getString()))
				    qa.setEnabled(true);
			    } catch (Exception x) {
				qa.setEnabled(true);
			    }
			else if ("clearMeasurements".equals(file.getFieldName())) {
			    try {
				clearMeasurements = false;
				String cm = file.getString();
				if ("on".equals(cm))
				    clearMeasurements = true;
				else if ("yes".equals(cm))
				    clearMeasurements = true;
				else if ("checked".equals(cm))
				    clearMeasurements = true;
			    } catch (Exception x) {
				clearMeasurements = false;
			    }

			} else if ("clearComposition".equals(file.getFieldName())) {
			    try {
				clearComposition = false;
				String cm = file.getString();
				if ("on".equals(cm))
				    clearComposition = true;
				else if ("yes".equals(cm))
				    clearComposition = true;
				else if ("checked".equals(cm))
				    clearComposition = true;
			    } catch (Exception x) {
				clearComposition = false;
			    }
			} else
			    for (IQASettings.qa_field f : IQASettings.qa_field.values())
				if (f.name().equals(file.getFieldName()))
				    try {
					String value = file.getString("UTF-8");
					f.addOption(qa,
						"null".equals(value) ? null : value == null ? null : value.toString());
				    } catch (Exception x) {
				    }
		    }

		for (FileItem file : items) {
		    if (file.isFormField())
			continue;

		    json.append(delimiter);
		    json.append("\n{\n");
		    json.append("\"name\":");
		    json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(file.getName())));
		    json.append(",\n\"size\":");
		    json.append(file.getSize());
		    String ext = file.getName().toLowerCase();
		    if (ext.endsWith(".i5z") || ext.endsWith(".csv") || ext.endsWith(".rdf") || ext.endsWith(".json")) {
			String img = "i5z.png";
			if (ext.endsWith(".csv"))
			    img = "csv64.png";
			if (ext.endsWith(".rdf"))
			    img = "rdf64.png";
			if (ext.endsWith(".json"))
			    img = "json64.png";
			try {
			    List<FileItem> item = new ArrayList<FileItem>();
			    item.add(file);
			    CallableSubstanceImporter<String> callable = new CallableSubstanceImporter<String>(item,
				    CallableFileUpload.field_files, CallableFileUpload.field_config, getRootRef(),
				    getContext(), new SubstanceURIReporter(getRequest().getRootRef()),
				    new DatasetURIReporter(getRequest().getRootRef()), null);
			    callable.setClearComposition(clearComposition);
			    callable.setClearMeasurements(clearMeasurements);
			    callable.setQASettings(qa);
			    ITaskResult result = callable.call();
			    json.append(",\n\"url\":");
			    json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(result.getReference().toString())));
			    json.append(",\n\"thumbnailUrl\":");
			    json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(String.format("%s/images/%s",
				    getRequest().getRootRef(), img))));
			    json.append(",\n\"deleteUrl\":");
			    json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(result.getReference().toString())));
			    json.append(",\n\"deleteType\":");
			    json.append("\"Delete\"");
			} catch (Exception x) {
			    json.append(",\n\"error\":");
			    json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(x.getMessage())));
			} finally {
			}

		    } else {
			json.append(",\n\"error\":");
			json.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape("File type not allowed!")));
		    }
		    json.append("\n}");
		}
		json.append("\n]}");
		getResponse().setStatus(Status.SUCCESS_OK);
		return new StringRepresentation(json.toString(), MediaType.APPLICATION_JSON);
	    } catch (FileUploadException x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x);
	    }

	} else
	    throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
    }
}
