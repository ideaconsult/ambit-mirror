package ambit2.rest.substance;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.i5.io.IQASettings;
import net.idea.i5.io.QASettings;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.db.QueryResource;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskStorage;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.processors.ProcessorException;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.UpdateExecutor;
import ambit2.db.processors.CallableSubstanceI5Query;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.substance.DeleteSubstance;
import ambit2.db.substance.FacetedSearchSubstance;
import ambit2.db.substance.ReadByReliabilityFlags;
import ambit2.db.substance.ReadSubstance;
import ambit2.db.substance.ReadSubstanceByExternalIDentifier;
import ambit2.db.substance.ReadSubstanceByName;
import ambit2.db.substance.ReadSubstanceByOwner;
import ambit2.db.substance.ReadSubstanceByStudy;
import ambit2.db.update.bundle.substance.ReadSubstancesByBundleCompounds;
import ambit2.rest.DBConnection;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.query.AmbitDBResource;
import ambit2.rest.task.CallableFileUpload;
import ambit2.rest.task.FactoryTaskConvertor;

/**
 * Substances (in the sense of IUCLID5)
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class SubstanceResource<Q extends IQueryRetrieval<SubstanceRecord>, T extends SubstanceRecord> extends
	AmbitDBResource<Q, SubstanceRecord> {
    public final static String substance = OpenTox.URI.substance.getURI();
    public final static String idsubstance = OpenTox.URI.substance.getKey();
    public final static String substanceID = OpenTox.URI.substance.getResourceID();
    protected SubstanceEndpointsBundle[] bundles;

    enum search_mode {
	reference, related
    }

    public SubstanceResource() {
	super();
	setHtmlbyTemplate(true);
    }

    @Override
    public String getTemplateName() {
	return "substance.ftl";
    }

    @Override
    public String getConfigFile() {
	return "ambit2/rest/config/ambit2.pref";
    }

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	customizeVariants(new MediaType[] { MediaType.TEXT_HTML, MediaType.TEXT_URI_LIST, MediaType.APPLICATION_JSON,
		MediaType.APPLICATION_JAVA_OBJECT, MediaType.APPLICATION_JAVASCRIPT, MediaType.IMAGE_PNG });

    }

    @Override
    public IProcessor<Q, Representation> createConvertor(Variant variant) throws AmbitException, ResourceException {
	/* workaround for clients not being able to set accept headers */
	Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
	Dimension d = new Dimension(250, 250);
	try {
	    d.width = Integer.parseInt(acceptform.getFirstValue("w").toString());
	} catch (Exception x) {
	}
	try {
	    d.height = Integer.parseInt(acceptform.getFirstValue("h").toString());
	} catch (Exception x) {
	}

	String media = acceptform.getFirstValue("accept-header");
	if (media != null)
	    variant.setMediaType(new MediaType(media));

	String filenamePrefix = getRequest().getResourceRef().getPath();
	if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
	    QueryURIReporter r = (QueryURIReporter) getURIReporter(getRequest());
	    return new StringConvertor(r, MediaType.TEXT_URI_LIST, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
	    return new ImageConvertor(new ImageReporter(variant.getMediaType().getMainType(), variant.getMediaType()
		    .getSubType(), d), variant.getMediaType());
	} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
	    SubstanceCSVReporter csvreporter = new SubstanceCSVReporter(getRequest(), bundles);
	    return new OutputWriterConvertor<SubstanceRecord, Q>(csvreporter, MediaType.TEXT_CSV,   filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
	    String jsonpcallback = getParams().getFirstValue("jsonp");
	    if (jsonpcallback == null)
		jsonpcallback = getParams().getFirstValue("callback");
	    SubstanceJSONReporter cmpreporter = new SubstanceJSONReporter(getRequest(), jsonpcallback, bundles);
	    return new OutputWriterConvertor<SubstanceRecord, Q>(cmpreporter, MediaType.APPLICATION_JAVASCRIPT,
		    filenamePrefix);
	} else { // json by default
	    // else if
	    // (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
	    SubstanceJSONReporter cmpreporter = new SubstanceJSONReporter(getRequest(), null, bundles);
	    return new OutputWriterConvertor<SubstanceRecord, Q>(cmpreporter, MediaType.APPLICATION_JSON,
		    filenamePrefix);
	}
    }

    @Override
    protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
	Object key = request.getAttributes().get(idsubstance);
	if (key == null) {
	    Form form = getRequest().getResourceRef().getQueryAsForm();

	    try {
		Object bundleURI = OpenTox.params.bundle_uri.getFirstValue(form);
		Integer idbundle = bundleURI == null ? null : getIdBundle(bundleURI, request);
		SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(idbundle);
		bundles = new SubstanceEndpointsBundle[1];
		bundles[0] = bundle;
	    } catch (Exception x) {
		bundles = null;
	    }

	    // ?compound_uri=
	    Object cmpURI = OpenTox.params.compound_uri.getFirstValue(form);
	    if (cmpURI != null) {
		Integer idchemical = getIdChemical(cmpURI, request);
		if (idchemical != null) {
		    search_mode mode = search_mode.reference;
		    try {
			mode = search_mode.valueOf(form.getFirstValue("type"));
		    } catch (Exception x) {
		    }
		    switch (mode) {
		    case reference: {
			SubstanceRecord substance = new SubstanceRecord();
			substance.setIdchemical(idchemical);
			return (Q) new ReadSubstance(substance);
		    }
		    case related: {
			CompositionRelation composition = new CompositionRelation(null, new StructureRecord(idchemical,
				-1, null, null), null);
			return (Q) new ReadSubstance(composition);
		    }
		    }
		} else
		    throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
	    }
	    // ?bundle_uri=
	    Object bundleURI = form.getFirstValue("filterbybundle");
	    if (bundleURI != null) {
		Integer idbundle = getIdBundle(bundleURI, request);
		if (idbundle != null) {
		    search_mode mode = search_mode.related;
		    try {
			mode = search_mode.valueOf(form.getFirstValue("type"));
		    } catch (Exception x) {
		    }
		    switch (mode) {
		    case reference: {
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(idbundle);
			bundles = new SubstanceEndpointsBundle[] { bundle };
			return (Q) new ReadSubstancesByBundleCompounds(bundle);
		    }
		    case related: {
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(idbundle);
			bundles = new SubstanceEndpointsBundle[] { bundle };
			return (Q) new ReadSubstancesByBundleCompounds(bundle);
		    }
		    }
		} else
		    throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
	    }

	    String type = form.getFirstValue("type");
	    if ("facet".equals(type)) {
		List<ProtocolApplication<Protocol, Params, String, Params, String>> protocols = new ArrayList<ProtocolApplication<Protocol, Params, String, Params, String>>();
		for (String value : form.getValuesArray("category"))
		    try {
			String[] categories = value.split("\\.");
			Protocol protocol = new Protocol(null);
			protocol.setCategory(Protocol._categories.valueOf(categories[1]).name());
			protocol.setTopCategory(categories[0]);
			ProtocolApplication<Protocol, Params, String, Params, String> papp = new ProtocolApplication<Protocol, Params, String, Params, String>(
				protocol);
			protocols.add(papp);
			String effectEndpoint = form.getFirstValue("endpoint." + value);
			String effectloValue = form.getFirstValue("lovalue." + value);
			String effectupValue = form.getFirstValue("upvalue." + value);
			String effectloQualifier = form.getFirstValue("loqlf." + value);
			String effectupQualifier = form.getFirstValue("upqlf." + value);
			String units = form.getFirstValue("unit." + value);
			String interpretationResult = form.getFirstValue("iresult." + value);

			final String[] qlfs = new String[] { "<=", "<", ">=", ">", "=" };
			EffectRecord<String, Params, String> effect = null;
			if (effectEndpoint != null) {
			    effect = new EffectRecord<String, Params, String>();
			    papp.addEffect(effect);
			    effect.setEndpoint(effectEndpoint);
			}
			if (effectloValue != null) {
			    if (effect == null) {
				effect = new EffectRecord<String, Params, String>();
				papp.addEffect(effect);
			    }
			    try {
				effect.setLoValue(Double.parseDouble(effectloValue));
			    } catch (Exception x) {
				effect.setTextValue(effectloValue);
			    }
			    effect.setLoQualifier(">=");
			    for (String qlf : qlfs) {
				if (qlf.equals(effectloQualifier)) {
				    effect.setLoQualifier(effectloQualifier);
				    break;
				}
			    }

			}
			if (effectupValue != null) {
			    if (effect == null) {
				effect = new EffectRecord<String, Params, String>();
				papp.addEffect(effect);
			    }
			    try {
				effect.setUpValue(Double.parseDouble(effectupValue));
			    } catch (Exception x) {
			    }
			    effect.setUpQualifier("<=");
			    for (String qlf : qlfs) {
				if (qlf.equals(effectupQualifier)) {
				    effect.setUpQualifier(effectupQualifier);
				    break;
				}
			    }
			}
			if (units != null) {
			    if (effect == null) {
				effect = new EffectRecord<String, Params, String>();
				papp.addEffect(effect);
			    }
			    effect.setUnit(units);
			}

			if (interpretationResult != null) {
			    papp.setInterpretationResult(interpretationResult);
			}
		    } catch (IllegalArgumentException x) {
			// invalid category, ignoring
		    } catch (Exception x) {
			// ignoring
		    }
		if (protocols.size() > 0) {
		    FacetedSearchSubstance q = new FacetedSearchSubstance();
		    q.setFieldname(protocols);
		    return (Q) q;
		} else
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    }

	    String search = form.getFirstValue(QueryResource.search_param);
	    if (search != null) {

		// search by study
		try {
		    ReadSubstanceByStudy._studysearchmode byStudy = null;
		    byStudy = ReadSubstanceByStudy._studysearchmode.valueOf(type);
		    return (Q) new ReadSubstanceByStudy(byStudy, search);
		} catch (Exception x) {
		}
		// search by owner
		try {
		    ReadSubstanceByOwner._ownersearchmode byOwner = null;
		    byOwner = ReadSubstanceByOwner._ownersearchmode.valueOf(type);
		    return (Q) new ReadSubstanceByOwner(byOwner, search);
		} catch (Exception x) {
		}

		if ("uuid".equals(type)) {
		    SubstanceRecord record = new SubstanceRecord();
		    record.setSubstanceUUID(search.trim());
		    return (Q) new ReadSubstance(record);
		} else if ("name".equals(type)) {
		    return (Q) new ReadSubstanceByName("startswith", search);
		} else if ("like".equals(type)) {
		    return (Q) new ReadSubstanceByName("like", search);
		} else if ("regexp".equals(type)) {
		    return (Q) new ReadSubstanceByName("regexp", search);		    
		} else if ("reliability".equals(type)) {
		    return (Q) new ReadByReliabilityFlags(type, search);
		} else if ("purposeFlag".equals(type)) {
		    return (Q) new ReadByReliabilityFlags(type, search);
		} else if ("studyResultType".equals(type)) {
		    return (Q) new ReadByReliabilityFlags(type, search);
		} else if ("isRobustStudy".equals(type)) {
		    return (Q) new ReadByReliabilityFlags(type, search);
		} else {
		    return (Q) new ReadSubstanceByExternalIDentifier(type, search);
		}
	    } else
		return (Q) new ReadSubstance();

	} else
	    try {
		return (Q) new ReadSubstance(new SubstanceRecord(Integer.parseInt(key.toString())));
	    } catch (Exception x) {
		int len = key.toString().trim().length();
		if ((len > 40) && (len <= 45)) {
		    SubstanceRecord record = new SubstanceRecord();
		    record.setSubstanceUUID(key.toString());
		    return (Q) new ReadSubstance(record);
		}
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    }
    }

    protected Integer getIdChemical(Object cmpURI, Request request) {
	if (cmpURI != null) {
	    Object id = OpenTox.URI.compound.getId(cmpURI.toString(), request.getRootRef());
	    if (id != null && (id instanceof Integer))
		return (Integer) id;
	    else {
		Object[] ids = OpenTox.URI.conformer.getIds(cmpURI.toString(), request.getRootRef());
		if (ids != null && (ids.length > 1) && (ids[0] instanceof Integer))
		    return (Integer) ids[0];
	    }
	}
	return null;
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
    protected QueryURIReporter<SubstanceRecord, Q> getURIReporter(Request baseReference) throws ResourceException {
	return new SubstanceURIReporter<Q>(baseReference);
    }

    @Override
    protected Representation post(Representation entity, Variant variant) throws ResourceException {

	if ((entity == null) || !entity.isAvailable())
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Empty content");

	if (entity.getMediaType() != null)
	    if (MediaType.MULTIPART_FORM_DATA.getName().equals(entity.getMediaType().getName())) {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		RestletFileUpload upload = new RestletFileUpload(factory);
		try {
		    List<FileItem> items = upload.parseRequest(getRequest());
		    String token = getToken();
		    QASettings qa = new QASettings();
		    qa.clear(); // sets enabled to false and clears all flags
		    boolean clearMeasurements = false;
		    boolean clearComposition = false;
		    for (FileItem file : items) {
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
					    f.addOption(
						    qa,
						    "null".equals(value) ? null : value == null ? null : value
							    .toString());
					} catch (Exception x) {
					}
			} else {
			    String ext = file.getName().toLowerCase().trim();
			    if ("".equals(ext) || ext.endsWith(".i5z") || ext.endsWith(".csv") || ext.endsWith(".rdf")
				    || ext.endsWith(".json")  || ext.endsWith(".xlsx")) {
			    } else
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Unsupported format "
					+ ext);
			}
		    }
		    CallableSubstanceImporter<String> callable = new CallableSubstanceImporter<String>(items,
			    CallableFileUpload.field_files, CallableFileUpload.field_config, getRootRef(), getContext(), new SubstanceURIReporter(getRequest().getRootRef()),
			    new DatasetURIReporter(getRequest().getRootRef()), token);
		    callable.setClearComposition(clearComposition);
		    callable.setClearMeasurements(clearMeasurements);
		    callable.setQASettings(qa);
		    ITask<Reference, Object> task = ((ITaskApplication) getApplication()).addTask("Substance import",
			    callable, getRequest().getRootRef(), token);

		    ITaskStorage storage = ((ITaskApplication) getApplication()).getTaskStorage();
		    FactoryTaskConvertor<Object> tc = new FactoryTaskConvertor<Object>(storage);
		    task.update();
		    getResponse().setStatus(task.isDone() ? Status.SUCCESS_OK : Status.SUCCESS_ACCEPTED);
		    return tc.createTaskRepresentation(task.getUuid(), variant, getRequest(), getResponse(), null);
		} catch (ResourceException x) {
		    throw x;
		} catch (Exception x) {
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x);
		}
	    } else if (MediaType.APPLICATION_WWW_FORM.getName().equals(entity.getMediaType().getName())) {
		/*
		 * web form to update substances from IUCLID5 server Expected
		 * web form fields : substance: UUID or query URL type : UUID or
		 * query or URL (ambit substance URL) qa options query : one of
		 * {@link QueryToolClient.PredefinedQuery} query parameters:
		 * depend on the query type iuclid5 server; credentials -
		 * optional, use preconfigured if not submitted [(option,UUID),
		 * (uuid,ZZZZZZZZZZ), (extidtype,CompTox), (extidvalue,Ambit
		 * Transfer), (i5server,null), (i5user,null), (i5pass,null)]
		 */

		Form form = new Form(entity);
		String token = getToken();
		CallableSubstanceI5Query<String> callable = new CallableSubstanceI5Query<String>(getRootRef(), form,
			getContext(), new SubstanceURIReporter(getRequest().getRootRef()), new DatasetURIReporter(
				getRequest().getRootRef()), token);
		ITask<Reference, Object> task = ((ITaskApplication) getApplication()).addTask(
			"Retrieve substance from IUCLID5 server", callable, getRequest().getRootRef(), token);

		ITaskStorage storage = ((ITaskApplication) getApplication()).getTaskStorage();
		FactoryTaskConvertor<Object> tc = new FactoryTaskConvertor<Object>(storage);
		task.update();
		getResponse().setStatus(task.isDone() ? Status.SUCCESS_OK : Status.SUCCESS_ACCEPTED);
		return tc.createTaskRepresentation(task.getUuid(), variant, getRequest(), getResponse(), null);

	    }
	throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    protected Representation delete(Variant variant) throws ResourceException {

	try {
	    Object key = getRequest().getAttributes().get(idsubstance);
	    if (key == null)
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

	    Form form = getResourceRef(getRequest()).getQueryAsForm();
	    String uri = getRequest().getRootRef() + "/substance/" + key.toString();
	    if (!uri.toString().equals(form.getFirstValue("substance_uri")))
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

	    SubstanceRecord record = new SubstanceRecord();
	    record.setSubstanceUUID(key.toString());
	    executeUpdate(getRequestEntity(), null, createDeleteObject(record));
	    getResponse().setStatus(Status.SUCCESS_OK);
	    return new StringRepresentation(String.format("%s/dataset", getRequest().getRootRef()),
		    MediaType.TEXT_URI_LIST);
	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	}
    }

    protected AbstractUpdate createDeleteObject(SubstanceRecord entry) throws ResourceException {
	DeleteSubstance c = new DeleteSubstance();
	c.setObject(entry);
	return c;
    }

    /**
     * POST - create entity based on parameters in http header, creates a new
     * entry in the databaseand returns an url to it
     */
    public void executeUpdate(Representation entity, T entry, AbstractUpdate updateObject) throws ResourceException {

	Connection c = null;
	// TODO it is inefficient to instantiate executor in all classes
	UpdateExecutor executor = new UpdateExecutor();
	try {
	    DBConnection dbc = new DBConnection(getContext());
	    c = dbc.getConnection(30,true,5);

	    executor.setConnection(c);
	    executor.open();
	    executor.process(updateObject);

	    customizeEntry(entry, c);

	    QueryURIReporter uriReporter = getURIReporter(getRequest());
	    if (uriReporter != null) {
		getResponse().setLocationRef(uriReporter.getURI(entry));
		getResponse().setEntity(uriReporter.getURI(entry), MediaType.TEXT_HTML);
	    }
	    getResponse().setStatus(Status.SUCCESS_OK);
	    onUpdateSuccess();
	} catch (SQLException x) {
	    Context.getCurrentLogger().severe(x.getMessage());
	    getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, x, x.getMessage());
	    getResponse().setEntity(null);
	} catch (ProcessorException x) {
	    Context.getCurrentLogger().severe(x.getMessage());
	    getResponse().setStatus(
		    (x.getCause() instanceof SQLException) ? Status.CLIENT_ERROR_FORBIDDEN
			    : Status.SERVER_ERROR_INTERNAL, x, x.getMessage());
	    getResponse().setEntity(null);
	} catch (Exception x) {
	    Context.getCurrentLogger().severe(x.getMessage());
	    getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, x, x.getMessage());
	    getResponse().setEntity(null);
	} finally {
	    try {
		executor.close();
	    } catch (Exception x) {
	    }
	    try {
		if (c != null)
		    c.close();
	    } catch (Exception x) {
	    }
	}
    }

    public void onUpdateSuccess() throws Exception {

    }

    protected void customizeEntry(T entry, Connection conection) throws ResourceException {

    }

}
