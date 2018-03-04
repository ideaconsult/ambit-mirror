package ambit2.rest.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.rendering.StructureEditorProcessor;
import ambit2.rest.AmbitFreeMarkerApplication;
import ambit2.rest.OpenTox;
import ambit2.rest.aa.opensso.OpenSSOUser;
import ambit2.rest.freemarker.FreeMarkerResource;
import ambit2.user.rest.resource.AMBITDBRoles;
import ambit2.user.rest.resource.DBRoles;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

public class UIResourceBase extends FreeMarkerResource {
	private static Logger logger = Logger.getLogger(UIResourceBase.class.getName());
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
		uploadsubstance1, updatesubstancei5, updatesubstancei6, predict, login {
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
		toxtree, taskpage, vega {
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
		},
		datatemplate {

			@Override
			public boolean enablePOST() {
				return true;
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

	public UIResourceBase() {
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
				return String.format("menu/profile/%s/index.ftl",
						((AmbitFreeMarkerApplication) getApplication()).getProfile());
			}
			default: {
				return String.format("%s.ftl", page.getTemplateName());
			}
			}

		} catch (Exception x) {
			return String.format("menu/profile/%s/index.ftl",
					((AmbitFreeMarkerApplication) getApplication()).getProfile());
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

	@Override
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
				if (DBRoles.isModeller(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_modeller.name(), Boolean.TRUE);
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
			Object bundleURI = OpenTox.params.bundle_uri.getFirstValue(request.getResourceRef().getQueryAsForm());
			if (bundleURI != null)
				map.put("bundleid", getIdBundle(bundleURI, request));
		} catch (Exception x) {
		}

		try {
			map.put(AMBITConfig.service_search.name(),
					((AmbitFreeMarkerApplication) getApplication()).getSearchServiceURI());
		} catch (Exception x) {
		}
		try {
			map.put(AMBITConfig.custom_query.name(), ((AmbitFreeMarkerApplication) getApplication()).getCustomQuery());
		} catch (Exception x) {
		}
		try {
			map.put(AMBITConfig.custom_structurequery.name(),
					((AmbitFreeMarkerApplication) getApplication()).getCustomStructureQuery());
		} catch (Exception x) {
		}

		try {
			map.put(AMBITConfig.custom_title.name(), ((AmbitFreeMarkerApplication) getApplication()).getCustomTitle());
		} catch (Exception x) {
		}
		try {
			map.put(AMBITConfig.custom_logo.name(), ((AmbitFreeMarkerApplication) getApplication()).getCustomLogo());
		} catch (Exception x) {
		}
		try {
			map.put(AMBITConfig.custom_description.name(),
					((AmbitFreeMarkerApplication) getApplication()).getCustomDescription());
		} catch (Exception x) {
		}
		try {
			map.put(AMBITConfig.custom_license.name(),
					((AmbitFreeMarkerApplication) getApplication()).getCustomLicense());
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

		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
}
