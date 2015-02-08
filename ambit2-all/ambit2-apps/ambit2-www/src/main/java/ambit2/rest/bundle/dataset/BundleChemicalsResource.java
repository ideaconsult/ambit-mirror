package ambit2.rest.bundle.dataset;

import java.awt.Dimension;
import java.sql.Connection;
import java.util.Map;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.reporters.CMLReporter;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.reporters.SmilesReporter;
import ambit2.db.reporters.SmilesReporter.Mode;
import ambit2.db.update.bundle.chemicals.ReadChemicalsByBundle;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OpenTox;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RDFStaXConvertor;
import ambit2.rest.bundle.CallableCompoundBundle;
import ambit2.rest.dataset.ARFF3ColResourceReporter;
import ambit2.rest.dataset.ARFFResourceReporter;
import ambit2.rest.dataset.DatasetRDFReporter;
import ambit2.rest.dataset.DatasetRDFStaxReporter;
import ambit2.rest.property.ProfileReader;
import ambit2.rest.structure.CompoundJSONReporter;
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.user.rest.resource.AmbitDBQueryResource;

public class BundleChemicalsResource<Q extends IQueryRetrieval<IStructureRecord>> extends
	AmbitDBQueryResource<Q, IStructureRecord> {
    protected SubstanceEndpointsBundle bundle;
    protected boolean enableFeatures = false;

    public BundleChemicalsResource() {
	super();
	setHtmlbyTemplate(true);
    }

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	// adding chemistry specific formats
	customizeVariants(new MediaType[] { ChemicalMediaType.CHEMICAL_MDLSDF, ChemicalMediaType.CHEMICAL_MDLMOL,
		ChemicalMediaType.CHEMICAL_SMILES, ChemicalMediaType.CHEMICAL_INCHI, ChemicalMediaType.CHEMICAL_CML,
		MediaType.APPLICATION_PDF, ChemicalMediaType.WEKA_ARFF, ChemicalMediaType.THREECOL_ARFF,
		ChemicalMediaType.IMAGE_JSON });

    }

    @Override
    public String getTemplateName() {
	return "bundle_compound.ftl";
    }

    @Override
    protected String getObjectURI(Form queryForm) throws ResourceException {
	return null;
    }

    @Override
    protected CallableProtectedTask<String> createCallable(Method method, Form form, IStructureRecord item)
	    throws ResourceException {
	Object id = getRequest().getAttributes().get(OpenTox.URI.bundle.getKey());
	if ((id != null))
	    try {
		bundle = new SubstanceEndpointsBundle(new Integer(Reference.decode(id.toString())));
	    } catch (Exception x) {
	    }

	Connection conn = null;
	try {
	    CompoundURIReporter<IQueryRetrieval<IStructureRecord>> r = new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(
		    getRequest());
	    DBConnection dbc = new DBConnection(getApplication().getContext(), getConfigFile());
	    conn = dbc.getConnection();
	    return new CallableCompoundBundle(bundle, r, method, form, conn, getToken());
	} catch (Exception x) {
	    try {
		conn.close();
	    } catch (Exception xx) {
	    }
	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
	}
    }

    @Override
    protected boolean isAllowedMediaType(MediaType mediaType) throws ResourceException {
	return MediaType.APPLICATION_WWW_FORM.equals(mediaType);
    }

    @Override
    protected Q createQuery(Context context, Request request, Response response) throws ResourceException {

	Form form = getResourceRef(getRequest()).getQueryAsForm();
	try {
	    includeMol = "true".equals(form.getFirstValue("mol"));
	} catch (Exception x) {
	    includeMol = false;
	}

	try {
	    enableFeatures = "true".equals(form.getFirstValue("enableFeatures"));
	} catch (Exception x) {
	    enableFeatures = false;
	}

	Object idbundle = request.getAttributes().get(OpenTox.URI.bundle.getKey());
	if (idbundle != null)
	    try {
		Integer idnum = new Integer(Reference.decode(idbundle.toString()));
		ReadChemicalsByBundle q = new ReadChemicalsByBundle();
		q.setEnableFeatures(enableFeatures);
		bundle = new SubstanceEndpointsBundle();
		bundle.setID(idnum);
		q.setFieldname(bundle);

		setTemplate(createTemplate(context, request, response));
		setGroupProperties(context, request, response);

		return (Q) q;
	    } catch (NumberFormatException x) {
	    }

	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
    }

    @Override
    public String getConfigFile() {
	return "ambit2/rest/config/ambit2.pref";
    }

    @Override
    protected Q createUpdateQuery(Method method, Context context, Request request, Response response)
	    throws ResourceException {
	Object idbundle = request.getAttributes().get(OpenTox.URI.bundle.getKey());
	if (idbundle == null)
	    throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);

	Object idchemical = request.getAttributes().get(OpenTox.URI.compound.getKey());
	if (Method.POST.equals(method) || Method.PUT.equals(method)) {
	    if (idchemical == null)
		return null;// post/put allowed only on /bundle/{id}/compound
			    // level, not on /bundle/id/compound/{idcompound}
	} else {
	    if (idchemical != null) {
		try {
		    Integer idnum = new Integer(Reference.decode(idbundle.toString()));
		    SubstanceEndpointsBundle dataset = new SubstanceEndpointsBundle();
		    dataset.setID(idnum);
		    ReadChemicalsByBundle query = new ReadChemicalsByBundle();
		    query.setFieldname(dataset);
		    return (Q) query;
		} catch (NumberFormatException x) {
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	    }
	}
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
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
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
	    return new OutputWriterConvertor(new CompoundJSONReporter(getTemplate(), getGroupProperties(), folders,
		    new SubstanceEndpointsBundle[] { bundle }, getRequest(), getRequest().getRootRef().toString(),
		    includeMol, null), MediaType.APPLICATION_JSON, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
	    String jsonpcallback = getParams().getFirstValue("jsonp");
	    if (jsonpcallback == null)
		jsonpcallback = getParams().getFirstValue("callback");
	    return new OutputWriterConvertor(new CompoundJSONReporter(getTemplate(), getGroupProperties(), folders,
		    new SubstanceEndpointsBundle[] { bundle }, getRequest(), getRequest().getRootRef().toString(),
		    includeMol, jsonpcallback), MediaType.APPLICATION_JAVASCRIPT, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
	    return new OutputWriterConvertor<IStructureRecord, Q>(new SDFReporter<Q>(template, getGroupProperties(),
		    changeLineSeparators), ChemicalMediaType.CHEMICAL_MDLSDF, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLMOL)) {
	    return new OutputWriterConvertor<IStructureRecord, Q>(new SDFReporter<Q>(new Template(),
		    getGroupProperties(), true, changeLineSeparators), ChemicalMediaType.CHEMICAL_MDLMOL,
		    filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_CML)) {
	    return new OutputWriterConvertor<IStructureRecord, Q>(new CMLReporter<Q>(), ChemicalMediaType.CHEMICAL_CML,
		    filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_SMILES)) {
	    return new OutputWriterConvertor<IStructureRecord, Q>(new SmilesReporter<Q>(true, getTemplate()),
		    ChemicalMediaType.CHEMICAL_SMILES, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_INCHI)) {
	    return new OutputWriterConvertor<IStructureRecord, Q>(new SmilesReporter<Q>(false, Mode.InChI,
		    getTemplate()), ChemicalMediaType.CHEMICAL_INCHI, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.WEKA_ARFF)) {
	    return new OutputWriterConvertor<IStructureRecord, Q>(new ARFFResourceReporter(getTemplate(),
		    getGroupProperties(), getRequest(), String.format("%s%s", getRequest().getRootRef(), "")),
		    ChemicalMediaType.WEKA_ARFF, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.THREECOL_ARFF)) {
	    return new OutputWriterConvertor<IStructureRecord, Q>(new ARFF3ColResourceReporter(getTemplate(),
		    getGroupProperties(), getRequest(), String.format("%s%s", getRequest().getRootRef(), "")),
		    ChemicalMediaType.THREECOL_ARFF, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
	    return new OutputWriterConvertor<IStructureRecord, Q>(new CSVReporter(getRequest().getRootRef().toString(),
		    getTemplate(), groupProperties, String.format("%s%s", getRequest().getRootRef())),
		    MediaType.TEXT_CSV, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)) {
	    switch (rdfwriter) {
	    case stax: {
		return new RDFStaXConvertor<IStructureRecord, Q>(new DatasetRDFStaxReporter("", getRequest(),
			getTemplate(), getGroupProperties()), filenamePrefix);
	    }
	    default: { // jena
		return new RDFJenaConvertor<IStructureRecord, Q>(new DatasetRDFReporter("", getRequest(),
			variant.getMediaType(), getTemplate(), getGroupProperties()), variant.getMediaType(),
			filenamePrefix);

	    }
	    }
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX)) {
	    return new RDFJenaConvertor<IStructureRecord, Q>(new DatasetRDFReporter("", getRequest(),
		    variant.getMediaType(), getTemplate(), getGroupProperties()), variant.getMediaType(),
		    filenamePrefix);
	} else { // JSON
	    return new OutputWriterConvertor(new CompoundJSONReporter(getTemplate(), getGroupProperties(), folders,
		    new SubstanceEndpointsBundle[] { bundle }, getRequest(), getRequest().getRootRef().toString(),
		    includeMol, null), MediaType.APPLICATION_JSON, filenamePrefix);

	}
    }

    protected Template template;
    protected Profile groupProperties;
    protected boolean includeMol = false;
    protected String[] folders;

    public Profile getGroupProperties() {
	return groupProperties;
    }

    public void setGroupProperties(Profile groupProperties) {
	this.groupProperties = groupProperties;
    }

    public enum QueryType {
	smiles, url, mol
    };

    public Template getTemplate() {
	return template;
    }

    public void setTemplate(Template template) {
	this.template = (template == null) ? new Template(null) : template;
    }

    protected void setGroupProperties(Context context, Request request, Response response) throws ResourceException {
	groupProperties = new Profile();

	Form form = getParams();
	String[] gp = OpenTox.params.sameas.getValuesArray(form);
	if (gp != null) {
	    for (String g : gp) {
		Property p = new Property(g);
		p.setEnabled(true);
		p.setLabel(g);
		groupProperties.add(p);
	    }

	}
	if (enableFeatures) {
	    LiteratureEntry ref = LiteratureEntry.getBundleReference(bundle);
	    Property p = new Property("tag", ref);
	    p.setEnabled(true);
	    groupProperties.add(p);
	    p = new Property("remarks", ref);
	    p.setEnabled(true);
	    groupProperties.add(p);
	}
    }

    protected Template createTemplate(Context context, Request request, Response response) throws ResourceException {
	Form form = getParams();
	return createTemplate(form);
    }

    protected Template createTemplate(Form form) throws ResourceException {
	String[] featuresURI = OpenTox.params.feature_uris.getValuesArray(form);
	return createTemplate(getContext(), getRequest(), getResponse(), featuresURI);
    }

    protected Template createTemplate(Context context, Request request, Response response, String[] featuresURI)
	    throws ResourceException {

	try {
	    Template profile = new Template(null);
	    profile.setId(-1);

	    ProfileReader reader = new ProfileReader(getRequest().getRootRef(), profile, getApplication().getContext(),
		    getToken(), getRequest().getCookies(), getRequest().getClientInfo() == null ? null : getRequest()
			    .getClientInfo().getAgent());
	    reader.setCloseConnection(false);

	    DBConnection dbc = new DBConnection(getContext(), getConfigFile());
	    Connection conn = dbc.getConnection();
	    try {
		for (String featureURI : featuresURI) {
		    if (featureURI == null)
			continue;
		    reader.setConnection(conn);
		    profile = reader.process(new Reference(featureURI));
		    reader.setProfile(profile);

		}
		// readFeatures(featureURI, profile);
		if (profile.size() == 0) {
		    reader.setConnection(conn);
		    String templateuri = getDefaultTemplateURI(context, request, response);
		    if (templateuri != null)
			profile = reader.process(new Reference(templateuri));
		    reader.setProfile(profile);
		}
	    } catch (Exception x) {
		getLogger().log(Level.SEVERE, x.getMessage(), x);
	    } finally {
		// the reader closes the connection
		reader.setCloseConnection(true);
		try {
		    reader.close();
		} catch (Exception x) {
		}
		// try { conn.close();} catch (Exception x) {}
	    }
	    return profile;
	} catch (Exception x) {
	    getLogger().log(Level.SEVERE, x.getMessage(), x);
	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
	}

    }

    protected String getDefaultTemplateURI(Context context, Request request, Response response) {
	return null;
    }

    @Override
    public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app) {
	super.configureTemplateMap(map, request, app);
	if (bundle != null)
	    map.put("datasetid", bundle.getID());
    }
}
