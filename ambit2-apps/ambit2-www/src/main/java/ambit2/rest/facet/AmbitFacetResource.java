package ambit2.rest.facet;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.db.facet.FacetResource;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.property.ProfileReader;
import ambit2.user.rest.resource.AMBITDBRoles;
import ambit2.user.rest.resource.DBRoles;

public abstract class AmbitFacetResource<FACET extends IFacet<String>, Q extends IQueryRetrieval<FACET>>
		extends FacetResource<FACET, Q> {

	public AmbitFacetResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "facet_body.ftl";
	}

	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/*
		 * if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
		 * 
		 * return new StringConvertor(new
		 * PropertyValueReporter(),MediaType.TEXT_PLAIN); } else if
		 * (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) { return new
		 * StringConvertor( new
		 * ReferenceURIReporter<IQueryRetrieval<ILiteratureEntry>>(getRequest())
		 * {
		 * 
		 * @Override public Object processItem(ILiteratureEntry dataset) throws
		 * AmbitException { super.processItem(dataset); try {
		 * output.write('\n'); } catch (Exception x) {} return null; }
		 * },MediaType.TEXT_URI_LIST); } else if
		 * (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
		 * variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
		 * variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
		 * variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
		 * variant.getMediaType().equals(MediaType.APPLICATION_JSON) ) { return
		 * new RDFJenaConvertor<ILiteratureEntry,
		 * IQueryRetrieval<ILiteratureEntry>>( new
		 * ReferenceRDFReporter<IQueryRetrieval<ILiteratureEntry>>(
		 * getRequest(),variant.getMediaType(),getDocumentation())
		 * ,variant.getMediaType()); } else
		 */
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return new OutputWriterConvertor(
					new FacetCSVReporter(getRequest()), MediaType.TEXT_CSV);
		} else if (variant.getMediaType().equals(
				MediaType.APPLICATION_JAVASCRIPT)) {
			String jsonpcallback = getParams().getFirstValue("jsonp");
			if (jsonpcallback == null)
				jsonpcallback = getParams().getFirstValue("callback");
			return new OutputWriterConvertor(createJSONReporter(getRequest(),
					jsonpcallback), MediaType.APPLICATION_JSON, filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			return new OutputWriterConvertor(createJSONReporter(getRequest(),
					null), MediaType.APPLICATION_JSON, filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new OutputWriterConvertor(
					new FacetURIReporter(getRequest()),
					MediaType.TEXT_URI_LIST, filenamePrefix);
		} else
			return new OutputWriterConvertor(createJSONReporter(getRequest(),
					null), MediaType.APPLICATION_JSON, filenamePrefix);
	}

	protected QueryReporter createJSONReporter(Request request, String jsonp) {
		return new FacetJSONReporter(request, jsonp);
	}

	protected Template getProperty(String[] propertyURI, int max)
			throws ResourceException {
		if (propertyURI == null)
			return null;
		Template profile = new Template();
		Connection connection = null;
		try {

			DBConnection dbc = new DBConnection(getContext());
			connection = dbc.getConnection();

			ProfileReader reader = new ProfileReader(getRequest().getRootRef(),
					profile, getApplication().getContext(), getToken(),
					getRequest().getCookies(),
					getRequest().getClientInfo() == null ? null : getRequest()
							.getClientInfo().getAgent());
			reader.setCloseConnection(false);
			reader.setConnection(connection);
			for (int i = 0; i < propertyURI.length; i++) {
				reader.process(new Reference(propertyURI[i]));
				if ((i + 1) >= max)
					break;
			}
			return profile;

		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					x.getMessage(), x);
		} finally {
			try {
				connection.close();
			} catch (Exception x) {
			}
		}

	}

	protected IStructureRecord getStructure() {
		String uri = getParams().getFirstValue(
				OpenTox.params.compound_uri.toString());
		Reference base = getRequest().getRootRef();

		int index = uri == null ? -1 : uri.indexOf("/dataset/");
		if (index > 0) {
			index = uri.indexOf("/", index + 10);
			base = new Reference(uri.substring(0, index));
		}

		int compoundid = -1;
		int conformerid = -1;
		if (uri != null) {
			Object id = OpenTox.URI.compound.getId(uri, base);
			if (id == null) {
				Object[] ids;
				ids = OpenTox.URI.conformer.getIds(uri, base);
				compoundid = ((Integer) ids[0]).intValue();
				conformerid = ((Integer) ids[1]).intValue();
			} else
				compoundid = ((Integer) id).intValue();
		}

		if ((compoundid > 0) || (conformerid > 0))
			return new StructureRecord(compoundid, conformerid, null, null);
		else
			return null;
	}

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("facet_title", "Summary");
		map.put("facet_tooltip", "");
		map.put("facet_group", "");
		map.put("facet_subgroup", "");
		map.put("facet_count", "count");
	}

	@Override
	public String getConfigFile() {
		return "ambit2/rest/config/ambit2.pref";
	}

	@Override
	protected Representation getHTMLByTemplate(Variant variant)
			throws ResourceException {
		Map map = getMap(variant);
		configureTemplateMap(map, getRequest(),
				(IFreeMarkerApplication) getApplication());
		return toRepresentation(map, getTemplateName(), MediaType.TEXT_PLAIN);
	}

	protected Map<String, Object> getMap(Variant variant)
			throws ResourceException {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			map.put(AMBITConfig.ambit_version_short.name(),
					((IFreeMarkerApplication) getApplication())
							.getVersionShort());
			map.put(AMBITConfig.ambit_version_long.name(),
					((IFreeMarkerApplication) getApplication())
							.getVersionLong());
			map.put(AMBITConfig.googleAnalytics.name(),
					((IFreeMarkerApplication) getApplication()).getGACode());
			map.put(AMBITConfig.menu_profile.name(),
					((IFreeMarkerApplication) getApplication()).getProfile());
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
		map.put(AMBITConfig.ambit_root.name(), getRequest().getRootRef()
				.toString());
		getRequest().getResourceRef().addQueryParameter("media",
				MediaType.APPLICATION_JSON.toString());
		map.put(AMBITConfig.ambit_request.name(), getRequest().getResourceRef()
				.toString());

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
		return map;
	}

	protected Integer getIdBundle(Object bundleURI, Request request) {
		if (bundleURI != null) {
			Object id = OpenTox.URI.bundle.getId(bundleURI.toString(),
					request.getRootRef());
			if (id != null && (id instanceof Integer))
				return (Integer) id;
		}
		return null;
	}

}