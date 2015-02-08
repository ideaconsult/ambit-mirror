package ambit2.rest.structure;

import java.awt.Dimension;
import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskStorage;

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

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.CASProcessor;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.data.EINECS;
import ambit2.core.processors.structure.key.ExactStructureSearchMode;
import ambit2.db.reporters.CMLReporter;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.reporters.ImageAreaReporter;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.reporters.PDFReporter;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.reporters.SmilesReporter;
import ambit2.db.reporters.SmilesReporter.Mode;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryField;
import ambit2.db.search.structure.QueryFieldNumeric;
import ambit2.db.search.structure.QueryStructure;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.db.update.chemical.DeleteChemical;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DisplayMode;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OpenTox;
import ambit2.rest.PDFConvertor;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.dataset.ARFF3ColResourceReporter;
import ambit2.rest.dataset.ARFFResourceReporter;
import ambit2.rest.dataset.DatasetRDFReporter;
import ambit2.rest.dataset.FileUpload;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.StructureQueryResource;
import ambit2.rest.rdf.RDFObjectIterator;
import ambit2.rest.rdf.RDFStructuresIterator;
import ambit2.rest.task.CallableStructureEntry;
import ambit2.rest.task.FactoryTaskConvertor;

/**
 * Chemical compound resource as in
 * http://opentox.org/development/wiki/structure REST Operations:
 * <ul>
 * <li>GET /compound/{id}</li>
 * </ul>
 * Content-Type:
 * 
 * <pre>
 * application/pdf
 * text/xml
 * chemical/x-cml
 * chemical/x-mdl-molfile
 * chemical/x-mdl-sdfile
 * chemical/x-daylight-smiles
 * image/png
 * </pre>
 * 
 * @author nina
 */
public class CompoundResource extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {
    protected FileUpload upload;
    public final static String compound = OpenTox.URI.compound.getURI();
    public final static String idcompound = OpenTox.URI.compound.getKey();
    public final static String compoundID = OpenTox.URI.compound.getResourceID();
    protected boolean chemicalsOnly = true;
    protected DisplayMode _dmode = DisplayMode.singleitem;

    public CompoundResource() {
	super();
	setHtmlbyTemplate(true);
    }

    @Override
    public String getTemplateName() {
	// TODO Auto-generated method stub
	return DisplayMode.singleitem.equals(_dmode) ? "compound.ftl" : "_compound.ftl";
    }

    @Override
    protected void doInit() throws ResourceException {
	// TODO Auto-generated method stub
	super.doInit();
	getVariants().add(new Variant(MediaType.IMAGE_PNG));
	getVariants().add(new Variant(MediaType.IMAGE_GIF));

	/*
	 * MediaType.IMAGE_BMP, MediaType.IMAGE_JPEG, MediaType.IMAGE_TIFF,
	 * MediaType.IMAGE_GIF,
	 */
    }

    @Override
    protected String getDefaultTemplateURI(Context context, Request request, Response response) {
	/*
	 * Object id =
	 * request.getAttributes().get(OpenTox.URI.compound.getKey()); if (id !=
	 * null) //return
	 * String.format("riap://application/dataset/%s%s",id,PropertyResource
	 * .featuredef); return String.format("%s%s/%s%s",
	 * getRequest().getRootRef
	 * (),OpenTox.URI.compound.getURI(),id,PropertyResource.featuredef);
	 * else return super.getDefaultTemplateURI(context,request,response);
	 */
	return null;
    }

    protected Template createTemplate(Form form) throws ResourceException {
	String[] featuresURI = OpenTox.params.feature_uris.getValuesArray(form);
	if ((featuresURI != null) && (featuresURI.length > 0))
	    return super.createTemplate(form);
	else
	    return null;
    }

    @Override
    protected Representation get(Variant variant) throws ResourceException {
	setFrameOptions("SAMEORIGIN");
	if (queryObject == null)
	    try {
		IProcessor<Object, Representation> convertor = createConvertor(variant);
		Representation r = convertor.process(null);
		return r;
	    } catch (Exception x) {
		getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, x);
		return null;
	    }
	else
	    return super.get(variant);
    }

    @Override
    public RepresentationConvertor createConvertor(Variant variant) throws AmbitException, ResourceException {
	/* workaround for clients not being able to set accept headers */
	if (!variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
	    setTemplate(createTemplate(getContext(), getRequest(), getResponse()));
	    setGroupProperties(getContext(), getRequest(), getResponse());
	}
	Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
	String media = acceptform.getFirstValue("accept-header");
	if (media != null) {
	    variant.setMediaType(new MediaType(media));
	}
	String filenamePrefix = getRequest().getResourceRef().getPath();
	if ((queryObject == null) && !(variant.getMediaType().equals(MediaType.TEXT_HTML)))
	    throw new NotFoundException();
	if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_CML))
	    // return new DocumentConvertor<IStructureRecord,
	    // QueryStructureByID>(new
	    // StructureReporter((getRequest()==null)?null:getRequest().getRootRef()));
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
		    new CMLReporter<QueryStructureByID>(), ChemicalMediaType.CHEMICAL_CML, filenamePrefix);
	else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new SDFReporter<QueryStructureByID>(
		    getTemplate(), getGroupProperties(), changeLineSeparators), ChemicalMediaType.CHEMICAL_MDLSDF,
		    filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLMOL)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new SDFReporter<QueryStructureByID>(
		    new Template(), getGroupProperties(), true, changeLineSeparators),
		    ChemicalMediaType.CHEMICAL_MDLMOL, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_SMILES)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
		    new SmilesReporter<QueryStructureByID>(), ChemicalMediaType.CHEMICAL_SMILES, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_INCHI)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
		    new SmilesReporter<QueryStructureByID>(false, Mode.InChI, getTemplate()),
		    ChemicalMediaType.CHEMICAL_INCHI, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
	    return new StringConvertor(new SmilesReporter<QueryStructureByID>(true, getTemplate()),
		    MediaType.TEXT_PLAIN);
	} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG)
		|| variant.getMediaType().equals(MediaType.IMAGE_BMP)
		|| variant.getMediaType().equals(MediaType.IMAGE_JPEG)
		|| variant.getMediaType().equals(MediaType.IMAGE_TIFF)
		|| variant.getMediaType().equals(MediaType.IMAGE_GIF)) {
	    return createImageConvertor(variant);
	} else if (variant.getMediaType().equals(ChemicalMediaType.IMAGE_JSON)) {
	    return createImageStringConvertor(variant);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_PDF)) {
	    return new PDFConvertor<IStructureRecord, QueryStructureByID, PDFReporter<QueryStructureByID>>(
		    new PDFReporter<QueryStructureByID>(getTemplate(), getGroupProperties()));

	} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
	    Dimension d = _dmode.isCollapsed() ? new Dimension(150, 150) : new Dimension(250, 250);
	    Form form = getResourceRef(getRequest()).getQueryAsForm();
	    try {

		d.width = Integer.parseInt(form.getFirstValue("w").toString());
	    } catch (Exception x) {
	    }
	    try {
		d.height = Integer.parseInt(form.getFirstValue("h").toString());
	    } catch (Exception x) {
	    }
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new CompoundHTMLReporter(
		    getCompoundInDatasetPrefix(), getRequest(), _dmode, getURIReporter(), getTemplate(),
		    getGroupProperties(), d, headless), MediaType.TEXT_HTML);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
	    QueryURIReporter r = (QueryURIReporter) getURIReporter();
	    return new StringConvertor(r, MediaType.TEXT_URI_LIST, filenamePrefix);

	} else if (variant.getMediaType().equals(ChemicalMediaType.WEKA_ARFF)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new ARFFResourceReporter(
		    getTemplate(), getGroupProperties(), getRequest(), getRequest().getRootRef().toString()
			    + getCompoundInDatasetPrefix()), ChemicalMediaType.WEKA_ARFF, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.THREECOL_ARFF)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new ARFF3ColResourceReporter(
		    getTemplate(), getGroupProperties(), getRequest(), getRequest().getRootRef().toString()
			    + getCompoundInDatasetPrefix()), ChemicalMediaType.THREECOL_ARFF, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
	    CompoundJSONReporter cmpreporter = new CompoundJSONReporter(getTemplate(), getGroupProperties(), folders,
		    bundles, getRequest(), getRequest().getRootRef().toString() + getCompoundInDatasetPrefix(),
		    includeMol, null);
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(cmpreporter,
		    MediaType.APPLICATION_JSON, filenamePrefix);

	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
	    String jsonpcallback = getParams().getFirstValue("jsonp");
	    if (jsonpcallback == null)
		jsonpcallback = getParams().getFirstValue("callback");
	    CompoundJSONReporter cmpreporter = new CompoundJSONReporter(getTemplate(), getGroupProperties(), folders,
		    bundles, getRequest(), getRequest().getRootRef().toString() + getCompoundInDatasetPrefix(),
		    includeMol, jsonpcallback);
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(cmpreporter,
		    MediaType.APPLICATION_JAVASCRIPT, filenamePrefix);

	} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new CSVReporter(getRequest()
		    .getRootRef().toString(), getTemplate(), getGroupProperties(), getRequest().getRootRef().toString()
		    + getCompoundInDatasetPrefix()), MediaType.TEXT_CSV, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.NANO_CML)) {
	    // return new DocumentConvertor<IStructureRecord,
	    // QueryStructureByID>(new
	    // StructureReporter((getRequest()==null)?null:getRequest().getRootRef()));
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
		    new CMLReporter<QueryStructureByID>(), ChemicalMediaType.NANO_CML, filenamePrefix);

	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX)) {
	    return new RDFJenaConvertor<IStructureRecord, IQueryRetrieval<IStructureRecord>>(new DatasetRDFReporter(
		    getCompoundInDatasetPrefix(), getRequest(), variant.getMediaType(), getTemplate(),
		    getGroupProperties()), variant.getMediaType(), filenamePrefix);
	} else
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
		    new SmilesReporter<QueryStructureByID>(), ChemicalMediaType.CHEMICAL_SMILES, filenamePrefix);

    }

    protected ImageConvertor<IStructureRecord, QueryStructureByID> createImageConvertor(Variant variant)
	    throws ResourceException {

	Dimension d = new Dimension(250, 250);
	Form form = getResourceRef(getRequest()).getQueryAsForm();
	try {

	    d.width = Integer.parseInt(form.getFirstValue("w").toString());
	} catch (Exception x) {
	}
	try {
	    d.height = Integer.parseInt(form.getFirstValue("h").toString());
	} catch (Exception x) {
	}
	return new ImageConvertor<IStructureRecord, QueryStructureByID>(new ImageReporter<QueryStructureByID>(variant
		.getMediaType().getMainType(), variant.getMediaType().getSubType(), d), variant.getMediaType());
    }

    protected StringConvertor createImageStringConvertor(Variant variant) throws ResourceException {
	String jsonpcallback = getParams().getFirstValue("jsonp");
	if (jsonpcallback == null)
	    jsonpcallback = getParams().getFirstValue("callback");
	Dimension d = new Dimension(250, 250);
	Form form = getResourceRef(getRequest()).getQueryAsForm();
	try {

	    d.width = Integer.parseInt(form.getFirstValue("w").toString());
	} catch (Exception x) {
	}
	try {
	    d.height = Integer.parseInt(form.getFirstValue("h").toString());
	} catch (Exception x) {
	}
	return new StringConvertor(new ImageAreaReporter<QueryStructureByID>(variant.getMediaType().getMainType(),
		variant.getMediaType().getSubType(), d, jsonpcallback), variant.getMediaType());
    }

    protected QueryURIReporter getURIReporter() {
	return new CompoundURIReporter<QueryStructureByID>(getCompoundInDatasetPrefix(), getRequest(),false);
    }

    protected IQueryRetrieval<IStructureRecord> createSingleQuery(String property, String cond, String key,
	    boolean chemicalsOnly, boolean byAlias, boolean caseSens) {
	AbstractStructureQuery query;

	try {
	    key = Reference.decode(key.toString().trim());

	    query = new QueryFieldNumeric(key, cond, byAlias, chemicalsOnly, (property == null) ? null : new Property(
		    property, null));

	} catch (Exception x) {
	    QueryField q_by_name = new QueryField();
	    q_by_name.setCaseSensitive(caseSens);
	    q_by_name.setRetrieveProperties(true);
	    q_by_name.setSearchByAlias(byAlias);
	    q_by_name.setNameCondition(StringCondition.getInstance(StringCondition.C_EQ));
	    q_by_name.setChemicalsOnly(chemicalsOnly);
	    // q_by_name.setChemicalsOnly(true);
	    StringCondition condition = StringCondition.getInstance(StringCondition.C_EQ);
	    try {
		condition = (cond == null) || ("".equals(cond)) ? StringCondition.getInstance(StringCondition.C_EQ)
			: StringCondition.getInstance(cond);
	    } catch (Exception xx) {
		condition = StringCondition.getInstance(StringCondition.C_EQ);
	    } finally {
		q_by_name.setCondition(condition);
		q_by_name.setValue(String.format("%s%s", Reference.decode(key.toString()),
			condition.toString().equals(StringCondition.C_LIKE) ? "%" : ""));
		if ((property != null) && (!"".equals(property)))
		    q_by_name.setFieldname(new Property(String.format("%s%s", property,
			    condition.toString().equals(StringCondition.C_LIKE) ? "%" : ""), null));
	    }
	    query = q_by_name;
	}
	return query;
    }

    @Override
    protected IQueryRetrieval<IStructureRecord> createQuery(Context context, Request request, Response response)
	    throws ResourceException {
	media = getMediaParameter(request);
	Object key = null;
	try {
	    Form form = request.getResourceRef().getQueryAsForm();
	    try {
		includeMol = "true".equals(form.getFirstValue("mol"));
	    } catch (Exception x) {
		includeMol = false;
	    }

	    DisplayMode defaultMode = null;
	    try {
		defaultMode = DisplayMode.valueOf(form.getFirstValue("mode"));
	    } catch (Exception x) {
	    }
	    _dmode = defaultMode == null ? DisplayMode.singleitem : defaultMode;

	    try {
		headless = Boolean.parseBoolean(form.getFirstValue("headless"));
	    } catch (Exception x) {
		headless = false;
	    }

	    key = request.getAttributes().get(OpenTox.URI.compound.getKey());
	    if (key == null) {
		boolean byAlias = true;
		String condition = form.getFirstValue(QueryResource.condition);
		String casesens = form.getFirstValue(QueryResource.caseSensitive);
		String[] keys = form.getValuesArray(QueryResource.search_param);
		String[] properties = form.getValuesArray(OpenTox.params.sameas.toString());
		if ((properties == null) || (properties.length == 0)) {
		    properties = form.getValuesArray(QueryResource.property);
		    condition = (condition == null) ? "=" : condition;
		    byAlias = false;
		} else
		    condition = (condition == null) ? "=" : condition;

		if (keys != null) {
		    _dmode = defaultMode == null ? DisplayMode.table : defaultMode;
		    /*
		     * QueryCombinedStructure qcombined = new
		     * QueryCombinedStructure();
		     * qcombined.setCombine_as_and(true);
		     * qcombined.setChemicalsOnly(true);
		     * 
		     * IQueryRetrieval<IStructureRecord> query = qcombined;
		     */
		    IQueryRetrieval<IStructureRecord> query = null;
		    for (int i = 0; i < keys.length; i++) {
			String theKey = Reference.decode(keys[i].trim());
			String property = null;
			try {
			    property = ((properties == null) || (i >= properties.length) || (properties[i] == null)) ? ""
				    : Reference.decode(properties[i].trim());
			} catch (Exception x) {

			    property = null;
			}
			casesens = CASProcessor.isValidFormat(theKey) ? "true" : casesens;
			casesens = EINECS.isValidFormat(theKey) ? "true" : casesens;
			casesens = theKey.startsWith(AmbitCONSTANTS.INCHI) ? "true" : casesens;
			casesens = theKey.startsWith("AuxInfo=") ? "true" : casesens;

			// check for smiles will be more time consuming, skip
			// for now
			IQueryRetrieval<IStructureRecord> q = createSingleQuery(property, condition, theKey,
				chemicalsOnly, byAlias,
				casesens == null ? false : "true".equals(casesens.toLowerCase()));
			// keys.length==1);
			query = q;
			break;
			/*
			 * if (keys.length>1) qcombined.add(q); else query = q;
			 */
		    }
		    return query;
		} else
		    return null;
	    } else {
		try {
		    IStructureRecord record = new StructureRecord();
		    record.setIdchemical(Integer.parseInt(Reference.decode(key.toString())));
		    return createQueryByID(record);
		} catch (NumberFormatException x) {
		    String inchikey = key.toString().trim();
		    if (inchikey.length() == 27) { // assume InChIKey
			QueryStructure q = new QueryStructure();
			q.setChemicalsOnly(true);
			q.setFieldname(ExactStructureSearchMode.inchikey);
			q.setValue(inchikey.trim());
			return q;
		    }
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid resource id", x);
		}
	    }

	} catch (Exception x) {
	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x.getMessage(), x);
	}

    }

    protected QueryStructureByID createQueryByID(IStructureRecord record) {
	QueryStructureByID query = new QueryStructureByID();
	query.setPageSize(1);
	query.setChemicalsOnly(true);
	query.setValue(record);
	return query;
    }

    /*
     * @Override protected Representation post(Representation entity) throws
     * ResourceException { if
     * (getRequest().getAttributes().get(idcompound)==null)
     * createNewObject(entity); else throw new
     * ResourceException(Status.CLIENT_ERROR_BAD_REQUEST); return
     * getResponse().getEntity(); }
     */
    @Override
    protected RDFObjectIterator<IStructureRecord> createObjectIterator(Representation entity) throws ResourceException {
	return new RDFStructuresIterator(entity, entity.getMediaType());
    }

    @Override
    protected AbstractUpdate createUpdateObject(IStructureRecord entry) throws ResourceException {
	return super.createUpdateObject(entry);
    }

    @Override
    protected IStructureRecord onError(String sourceURI) {
	return null;
    }

    @Override
    protected IQueryRetrieval<IStructureRecord> returnQueryObject() {
	IQueryRetrieval<IStructureRecord> q = super.returnQueryObject();
	if (q instanceof QueryField)
	    ((QueryField) q).setRetrieveProperties(false);
	return q;
    }

    @Override
    protected AbstractUpdate createDeleteObject(IStructureRecord record) throws ResourceException {
	record = record == null ? new StructureRecord() : record;
	Object key = getRequest().getAttributes().get(OpenTox.URI.compound.getKey());
	try {
	    record.setIdchemical(Integer.parseInt(Reference.decode(key.toString())));
	    if (record.getIdchemical() <= 0)
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	    else {
		DeleteChemical c = new DeleteChemical();
		c.setObject(record);
		return c;
	    }
	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}

    }

    @Override
    protected Representation delete(Variant variant) throws ResourceException {
	Representation entity = getRequestEntity();
	try {
	    executeUpdate(entity, null, createDeleteObject(null));
	    return getResponseEntity();
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN, x.getMessage(), x);
	}
    }

    @Override
    protected QueryURIReporter<IStructureRecord, IQueryRetrieval<IStructureRecord>> getURUReporter(Request baseReference)
	    throws ResourceException {

	return null;
    }

    /**
     * POST as in the dataset resource
     */
    @Override
    protected Representation post(Representation entity, Variant variant) throws ResourceException {

	if ((entity == null) || !entity.isAvailable())
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Empty content");

	if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
	    String token = getToken();
	    CallableStructureEntry callable = new CallableStructureEntry<String>(new Form(entity), getRootRef(),
		    getRecord(), getContext(), null, token);
	    callable.setPropertyOnly(false);
	    ITask<Reference, Object> task = ((ITaskApplication) getApplication()).addTask(
		    "New structure from web form", callable, getRequest().getRootRef(), token);

	    ITaskStorage storage = ((ITaskApplication) getApplication()).getTaskStorage();
	    FactoryTaskConvertor<Object> tc = new FactoryTaskConvertor<Object>(storage);
	    task.update();
	    getResponse().setStatus(task.isDone() ? Status.SUCCESS_OK : Status.SUCCESS_ACCEPTED);
	    return tc.createTaskRepresentation(task.getUuid(), variant, getRequest(), getResponse(), null);

	    /*
	     * Form form = new Form(entity); String cmpname =
	     * form.getFirstValue("identifier"); String uri =
	     * form.getFirstValue(OpenTox.params.compound_uri.toString());
	     * 
	     * if (cmpname != null) try { OTDatasets datasets =
	     * OTDatasets.datasets(); OTContainers<OTDataset> result =
	     * datasets.read
	     * (String.format("%s/query/compound/%s",getRequest().getRootRef
	     * (),Reference.encode(cmpname))); if (result.size()==0) { //not
	     * found uri = String.format("%s/query%s/%s",
	     * getRequest().getRootRef(), CSLSResource.resource,
	     * Reference.encode(cmpname)); } else return new
	     * StringRepresentation
	     * (result.getItem(0).getUri().toString(),MediaType.TEXT_URI_LIST);
	     * } catch (Exception x) {
	     * 
	     * }
	     * 
	     * 
	     * 
	     * if (uri == null) if ((entity == null) || !entity.isAvailable())
	     * throw new
	     * ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,OpenTox
	     * .params.compound_uris.toString() + " empty.");
	     * 
	     * String sdf = getSDFFromURI(uri); if (sdf == null) throw new
	     * ResourceException(Status.CLIENT_ERROR_NOT_FOUND); if (cmpname !=
	     * null) {
	     * 
	     * if (CASNumber.isValid(cmpname)) sdf =
	     * sdf.replace("$$$$",String.format
	     * ("\n> <CAS>\n%s\n\n$$$$",cmpname)).trim(); else if
	     * (cmpname.startsWith("InChI")) sdf =
	     * sdf.replace("$$$$",String.format
	     * ("\n> <InChI>\n%s\n\n$$$$",cmpname)).trim(); else sdf =
	     * sdf.replace
	     * ("$$$$",String.format("\n> <Name>\n%s\n\n$$$$",cmpname)).trim();
	     * } if(upload == null) upload = createFileUpload(); String source =
	     * uri; String name = "Copied from URL"; if
	     * (uri.startsWith(CSLSRequest.CSLS_URL)) { source =
	     * CSLSRequest.CSLS_URL; name = "CIR"; uri = name; }
	     * 
	     * if
	     * (uri.startsWith(String.format("%s/query%s",getRequest().getRootRef
	     * (),CSLSResource.resource))) { source = CSLSRequest.CSLS_URL;name
	     * = "Chemical Identifier Resolver (CIR)"; uri = "CIR"; } if
	     * (uri.startsWith
	     * (String.format("%s/query/pubchem",getRequest().getRootRef()))) {
	     * source = "http://www.ncbi.nlm.nih.gov/entrez/eutils"; name =
	     * "PUBCHEM"; uri = "PUBCHEM"; }
	     * 
	     * SourceDataset dataset = new
	     * SourceDataset(uri,LiteratureEntry.getInstance(name,source));
	     * upload.setDataset(dataset); StringRepresentation representation =
	     * new StringRepresentation(sdf,ChemicalMediaType.CHEMICAL_MDLSDF);
	     * //representation.setDownloadName(dataset.getName());
	     * Representation r = upload.upload(representation,
	     * variant,true,false, getToken() ); return r; //return
	     * copyDatasetToQueryResultsTable(new Form(entity),true); //throw
	     * new
	     * ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE,entity
	     * .getMediaType().toString());
	     */
	} else {
	    if (upload == null)
		upload = createFileUpload();
	    upload.setDataset(new SourceDataset("User uploaded", LiteratureEntry.getInstance("User uploaded",
		    getResourceRef(getRequest()).toString())));

	    return upload.upload(entity, variant, true, false, getToken());
	}
    }

    protected IStructureRecord getRecord() {
	Object key = getRequest().getAttributes().get(OpenTox.URI.compound.getKey());
	IStructureRecord record = new StructureRecord();
	if (key != null)
	    record.setIdchemical(Integer.parseInt(Reference.decode(key.toString())));
	return record;
    }

    /**
     * POST as in the dataset resource
     */
    @Override
    protected Representation put(Representation entity, Variant variant) throws ResourceException {

	if ((entity == null) || !entity.isAvailable())
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Empty content");

	if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
	    String token = getToken();
	    CallableStructureEntry callable = new CallableStructureEntry<String>(new Form(entity), getRootRef(),
		    getRecord(), getContext(), null, token);
	    callable.setPropertyOnly(true);
	    ITask<Reference, Object> task = ((ITaskApplication) getApplication()).addTask("Properties from web form",
		    callable, getRequest().getRootRef(), token);

	    ITaskStorage storage = ((ITaskApplication) getApplication()).getTaskStorage();
	    FactoryTaskConvertor<Object> tc = new FactoryTaskConvertor<Object>(storage);
	    task.update();
	    getResponse().setStatus(task.isDone() ? Status.SUCCESS_OK : Status.SUCCESS_ACCEPTED);
	    return tc.createTaskRepresentation(task.getUuid(), variant, getRequest(), getResponse(), null);

	} else {
	    if (upload == null)
		upload = createFileUpload();
	    upload.setDataset(new SourceDataset("User uploaded", LiteratureEntry.getInstance("User uploaded",
		    getResourceRef(getRequest()).toString())));

	    return upload.upload(entity, variant, true, true, getToken());
	}
    }

    protected FileUpload createFileUpload() {
	FileUpload upload = new FileUpload();
	upload.setFirstCompoundOnly(true);
	upload.setRequest(getRequest());
	upload.setResponse(getResponse());
	upload.setContext(getContext());
	upload.setApplication(getApplication());
	return upload;
    }

    @Override
    public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app) {
	super.configureTemplateMap(map, request, app);
	Object key = getRequest().getAttributes().get(OpenTox.URI.compound.getKey());
	if (key != null)
	    map.put("cmpid", key.toString());
	Object idconformer = getRequest().getAttributes().get(ConformerResource.idconformer);
	if (idconformer != null)
	    map.put("strucid", idconformer.toString());
    }
}
