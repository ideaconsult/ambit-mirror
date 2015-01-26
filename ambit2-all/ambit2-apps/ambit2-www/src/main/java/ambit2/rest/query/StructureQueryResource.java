package ambit2.rest.query;

import java.awt.Dimension;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.r.QueryAbstractReporter;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.FixBondOrdersTool;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.util.Base64;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.db.reporters.CMLReporter;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.reporters.PDFReporter;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.reporters.SmilesReporter;
import ambit2.db.reporters.SmilesReporter.Mode;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.db.update.dataset.ReadDatasetLicense;
import ambit2.namestructure.Name2StructureProcessor;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DBConnection;
import ambit2.rest.DisplayMode;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OpenTox;
import ambit2.rest.PDFConvertor;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RDFStaXConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.dataset.ARFF3ColResourceReporter;
import ambit2.rest.dataset.ARFFResourceReporter;
import ambit2.rest.dataset.DatasetRDFReporter;
import ambit2.rest.dataset.DatasetRDFStaxReporter;
import ambit2.rest.structure.CompoundHTMLbyJSONReporter;
import ambit2.rest.structure.CompoundJSONReporter;
import ambit2.rest.structure.ConformerURIReporter;

/**
 * Abstract parent class for all resources that retrieve compounds/conformers
 * from database
 * 
 * @author nina
 * 
 * @param <Q>
 */
public abstract class StructureQueryResource<Q extends IQueryRetrieval<IStructureRecord>> extends
	QueryResource<Q, IStructureRecord> {

    protected String media;
    protected Template template;
    protected Profile groupProperties;
    protected boolean includeMol = false;
    protected String[] folders;
    protected SubstanceEndpointsBundle[] bundles;

    public boolean isIncludeMol() {
	return includeMol;
    }

    public StructureQueryResource() {
	super();
	setDocumentation(null);
	setHtmlbyTemplate(true);
    }

    @Override
    public String getTemplateName() {
	return "_dataset.ftl";
    }

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

    public String getCompoundInDatasetPrefix() {
	return "";
    }

    protected String getDefaultTemplateURI(Context context, Request request, Response response) {
	return null;// return
		    // String.format("riap://application%s/All/Identifiers/view/tree",OntologyResource.resource);
    }

    protected void setGroupProperties(Context context, Request request, Response response) throws ResourceException {
	Form form = getParams();
	String[] gp = OpenTox.params.sameas.getValuesArray(form);
	if (gp != null) {
	    groupProperties = new Profile();
	    for (String g : gp) {
		Property p = new Property(g);
		p.setEnabled(true);
		p.setLabel(g);
		groupProperties.add(p);
	    }

	}
    }

    protected Template createTemplate(Context context, Request request, Response response) throws ResourceException {
	Form form = getParams();
	return createTemplate(form);
    }

    @Override
    protected String getLicenseURI() {
	return null;
    }

    protected String retrieveLicense(Context context, Request request, Integer id_srcdataset) throws ResourceException {
	if (id_srcdataset == null)
	    return null;
	if (id_srcdataset.intValue() <= 0)
	    return null;
	String licenseURI = null;
	try {
	    ReadDatasetLicense license = new ReadDatasetLicense();
	    license.setFieldname(null);
	    license.setValue(id_srcdataset);

	    ResultSet rs = null;
	    QueryExecutor ex = new QueryExecutor();

	    DBConnection dbc = new DBConnection(getContext());
	    Connection conn = dbc.getConnection();

	    try {
		ex.setConnection(conn);
		rs = ex.process(license);
		while (rs.next()) {
		    licenseURI = license.getObject(rs);
		    break;
		}
	    } catch (Exception x) {
		getLogger().log(Level.SEVERE, x.getMessage(), x);
	    } finally {
		// the reader closes the connection
		try {
		    if (rs != null)
			rs.close();
		} catch (Exception x) {
		}
		ex.setCloseConnection(true);
		try {
		    ex.close();
		} catch (Exception x) {
		}
		// try { conn.close();} catch (Exception x) {}
	    }

	} catch (Exception x) {
	    getLogger().log(Level.SEVERE, x.getMessage(), x);
	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
	}
	return licenseURI;
    }

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	customizeVariants(new MediaType[] { MediaType.TEXT_HTML, ChemicalMediaType.CHEMICAL_MDLSDF,
		ChemicalMediaType.CHEMICAL_MDLMOL, ChemicalMediaType.CHEMICAL_SMILES, ChemicalMediaType.CHEMICAL_INCHI,
		ChemicalMediaType.CHEMICAL_CML, MediaType.APPLICATION_PDF, MediaType.TEXT_URI_LIST,
		MediaType.TEXT_PLAIN, ChemicalMediaType.TEXT_YAML, ChemicalMediaType.WEKA_ARFF,
		ChemicalMediaType.THREECOL_ARFF, MediaType.APPLICATION_JSON, MediaType.TEXT_CSV,
		MediaType.APPLICATION_RDF_XML, MediaType.APPLICATION_RDF_TURTLE, MediaType.APPLICATION_RDF_TRIG,
		MediaType.APPLICATION_RDF_TRIX, MediaType.TEXT_RDF_N3, MediaType.TEXT_RDF_NTRIPLES,
		MediaType.APPLICATION_JAVA_OBJECT, MediaType.APPLICATION_JAVASCRIPT, ChemicalMediaType.IMAGE_JSON

	});

    }

    protected CSVReporter createTXTReporter() {
	CSVReporter csvreporter = new CSVReporter(getRequest().getRootRef().toString(), getTemplate(), groupProperties,
		String.format("%s%s", getRequest().getRootRef(), getCompoundInDatasetPrefix()));
	csvreporter.setSeparator("\t");
	csvreporter.setNumberofHeaderLines(0);
	csvreporter.setWriteCompoundURI(false);
	return csvreporter;
    }

    protected CSVReporter createCSVReporter() {
	return new CSVReporter(getRequest().getRootRef().toString(), getTemplate(), groupProperties, String.format(
		"%s%s", getRequest().getRootRef(), getCompoundInDatasetPrefix()));
    }

    @Override
    public RepresentationConvertor createConvertor(Variant variant) throws AmbitException, ResourceException {
	/* workaround for clients not being able to set accept headers */
	if ((queryObject == null) && !(variant.getMediaType().equals(MediaType.TEXT_HTML)))
	    throw new NotFoundException();

	setTemplate(template);
	Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
	String media = acceptform.getFirstValue("accept-header");
	if (media != null) {
	    variant.setMediaType(new MediaType(media));
	}
	String filenamePrefix = getRequest().getResourceRef().getPath();
	if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLSDF)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new SDFReporter<QueryStructureByID>(
		    template, getGroupProperties(), changeLineSeparators), ChemicalMediaType.CHEMICAL_MDLSDF,
		    filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_MDLMOL)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new SDFReporter<QueryStructureByID>(
		    new Template(), getGroupProperties(), true, changeLineSeparators),
		    ChemicalMediaType.CHEMICAL_MDLMOL, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_CML)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
		    new CMLReporter<QueryStructureByID>(), ChemicalMediaType.CHEMICAL_CML, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_SMILES)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
		    new SmilesReporter<QueryStructureByID>(true, getTemplate()), ChemicalMediaType.CHEMICAL_SMILES,
		    filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.CHEMICAL_INCHI)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(
		    new SmilesReporter<QueryStructureByID>(false, Mode.InChI, getTemplate()),
		    ChemicalMediaType.CHEMICAL_INCHI, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_PDF)) {
	    return new PDFConvertor<IStructureRecord, QueryStructureByID, PDFReporter<QueryStructureByID>>(
		    new PDFReporter<QueryStructureByID>(getTemplate(), getGroupProperties()), filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(createTXTReporter(),
		    MediaType.TEXT_PLAIN);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
	    ConformerURIReporter<QueryStructureByID> reporter = new ConformerURIReporter<QueryStructureByID>(
		    getCompoundInDatasetPrefix(), getRequest(), queryObject.isPrescreen(), getDocumentation());
	    return new StringConvertor(reporter, MediaType.TEXT_URI_LIST, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
	    return new ImageConvertor<IStructureRecord, QueryStructureByID>(new ImageReporter<QueryStructureByID>(
		    MediaType.IMAGE_PNG.getMainType(), MediaType.IMAGE_PNG.getSubType()), MediaType.IMAGE_PNG);
	} else if (variant.getMediaType().equals(MediaType.IMAGE_GIF)) {
	    return new ImageConvertor<IStructureRecord, QueryStructureByID>(new ImageReporter<QueryStructureByID>(
		    MediaType.IMAGE_GIF.getMainType(), MediaType.IMAGE_GIF.getSubType()), MediaType.IMAGE_GIF);
	} else if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
	    Dimension d = new Dimension(150, 150);
	    Form form = getResourceRef(getRequest()).getQueryAsForm();
	    try {

		d.width = Integer.parseInt(form.getFirstValue("w").toString());
	    } catch (Exception x) {
	    }
	    try {
		d.height = Integer.parseInt(form.getFirstValue("h").toString());
	    } catch (Exception x) {
	    }
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(createHTMLReporter(d),
		    MediaType.TEXT_HTML);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new CompoundJSONReporter(
		    getTemplate(), getGroupProperties(), folders, bundles, getRequest(), getRequest().getRootRef()
			    .toString() + getCompoundInDatasetPrefix(), includeMol, null), MediaType.APPLICATION_JSON,
		    filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
	    String jsonpcallback = getParams().getFirstValue("jsonp");
	    if (jsonpcallback == null)
		jsonpcallback = getParams().getFirstValue("callback");
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new CompoundJSONReporter(
		    getTemplate(), getGroupProperties(), folders, bundles, getRequest(), getRequest().getRootRef()
			    .toString() + getCompoundInDatasetPrefix(), includeMol, jsonpcallback),
		    MediaType.APPLICATION_JAVASCRIPT, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.WEKA_ARFF)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new ARFFResourceReporter(
		    getTemplate(), getGroupProperties(), getRequest(), String.format("%s%s", getRequest().getRootRef(),
			    getCompoundInDatasetPrefix())), ChemicalMediaType.WEKA_ARFF, filenamePrefix);
	} else if (variant.getMediaType().equals(ChemicalMediaType.THREECOL_ARFF)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new ARFF3ColResourceReporter(
		    getTemplate(), getGroupProperties(), getRequest(), String.format("%s%s", getRequest().getRootRef(),
			    getCompoundInDatasetPrefix())), ChemicalMediaType.THREECOL_ARFF, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(createCSVReporter(),
		    MediaType.TEXT_CSV, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)) {
	    switch (rdfwriter) {
	    case stax: {
		return new RDFStaXConvertor<IStructureRecord, IQueryRetrieval<IStructureRecord>>(
			new DatasetRDFStaxReporter(getCompoundInDatasetPrefix(), getRequest(), getDocumentation(),
				getTemplate(), getGroupProperties()), filenamePrefix);
	    }
	    default: { // jena
		return new RDFJenaConvertor<IStructureRecord, IQueryRetrieval<IStructureRecord>>(
			new DatasetRDFReporter(getCompoundInDatasetPrefix(), getRequest(), getDocumentation(),
				variant.getMediaType(), getTemplate(), getGroupProperties()), variant.getMediaType(),
			filenamePrefix);

	    }
	    }
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX)) {
	    return new RDFJenaConvertor<IStructureRecord, IQueryRetrieval<IStructureRecord>>(new DatasetRDFReporter(
		    getCompoundInDatasetPrefix(), getRequest(), getDocumentation(), variant.getMediaType(),
		    getTemplate(), getGroupProperties()), variant.getMediaType(), filenamePrefix);
	} else
	    return new OutputWriterConvertor<IStructureRecord, QueryStructureByID>(new SDFReporter<QueryStructureByID>(
		    template, getGroupProperties(), changeLineSeparators), ChemicalMediaType.CHEMICAL_MDLSDF,
		    filenamePrefix);
    }

    protected QueryAbstractReporter createHTMLReporter(Dimension d) {
	return new CompoundHTMLbyJSONReporter(getCompoundInDatasetPrefix(), getRequest(), DisplayMode.table, null,
		getTemplate(), getGroupProperties(), d, getRequest().getRootRef().toString()
			+ getCompoundInDatasetPrefix());
	/*
	 * return new
	 * CompoundHTMLReporter(getCompoundInDatasetPrefix(),getRequest
	 * (),getDocumentation(),
	 * DisplayMode.table,null,getTemplate(),getGroupProperties
	 * (),d,headless);
	 */
    }

    protected String getMediaParameter(Request request) {
	try {
	    Object m = request.getAttributes().get("media");
	    return m == null ? null : Reference.decode(m.toString());
	} catch (Exception x) {
	    return null;
	}
    }

    protected String getSDFFromURI(String uri) throws ResourceException {
	Request rq = null;
	Response resp = null;
	long now = System.currentTimeMillis();
	try {
	    rq = new Request();
	    Client client = new Client(Protocol.HTTP);
	    client.setConnectTimeout(5 * 60 * 1000); // 5 min
	    rq.setResourceRef(uri);
	    rq.setMethod(Method.GET);
	    // rq.getClientInfo().getAcceptedMediaTypes().clear();
	    // rq.getClientInfo().getAcceptedMediaTypes().add(new
	    // Preference<MediaType>(ChemicalMediaType.CHEMICAL_MDLSDF));
	    resp = client.handle(rq);
	    if (resp.getStatus().equals(Status.SUCCESS_OK)) {
		String content = resp.getEntity().getText();
		return content;

	    } else
		return null;

	} catch (Exception x) {
	    if ((System.currentTimeMillis() - now) > (5 * 60 * 1000))
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Timeout 5 min exceeded");
	    else
		throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x.getMessage(), x);
	} finally {
	    try {
		resp.release();
	    } catch (Exception x) {
	    }
	    ;
	    try {
		rq.release();
	    } catch (Exception x) {
	    }
	    ;
	}
    }

    protected IMolecule getFromURI(String uri) throws ResourceException {
	Reader reader = null;
	try {
	    Request rq = new Request();
	    Client client = new Client(Protocol.HTTP);
	    rq.setResourceRef(uri);
	    rq.setMethod(Method.GET);
	    rq.getClientInfo().getAcceptedMediaTypes().clear();
	    rq.getClientInfo().getAcceptedMediaTypes()
		    .add(new Preference<MediaType>(ChemicalMediaType.CHEMICAL_MDLSDF));
	    Response resp = client.handle(rq);
	    if (resp.getStatus().equals(Status.SUCCESS_OK)) {
		reader = new InputStreamReader(resp.getEntity().getStream());
		return MoleculeTools.readMolfile(reader);
	    } else
		return null;
	} catch (InvalidSmilesException x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, String.format("Invalid smiles %s",
		    getRequest().getAttributes().get("smiles")), x);

	} catch (Exception x) {
	    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x.getMessage(), x);
	} finally {
	    try {
		reader.close();
	    } catch (Exception x) {
	    }
	    ;
	}
    }

    protected String getSMILES(Form form, boolean aromatic) throws ResourceException {
	QueryType query_type;
	String query_smiles;
	try {
	    query_type = QueryType.valueOf(form.getFirstValue("type").trim());

	} catch (Exception x) {
	    query_type = QueryType.smiles;
	}
	switch (query_type) {
	case mol: {
	    query_smiles = form.getFirstValue(QueryResource.b64search_param);
	    if (query_smiles != null) {
		query_smiles = new String(Base64.decode(query_smiles));
	    } else
		query_smiles = form.getFirstValue(QueryResource.search_param);
	    try {
		IAtomContainer ac = MoleculeTools.readMolfile(query_smiles);
		for (IBond b : ac.bonds())
		    if (b.getFlag(CDKConstants.ISAROMATIC)) {
			FixBondOrdersTool fbt = new FixBondOrdersTool();
			ac = fbt.kekuliseAromaticRings((IMolecule) ac);
			break;
		    }
		SmilesGenerator g = new SmilesGenerator();
		return g.createSMILES(ac);
	    } catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	    }
	}
	case smiles: {
	    query_smiles = form.getFirstValue(QueryResource.b64search_param);
	    if (query_smiles != null) {
		query_smiles = new String(Base64.decode(query_smiles));
	    } else
		query_smiles = form.getFirstValue(QueryResource.search_param);

	    return query_smiles == null ? null : query_smiles.trim();

	}
	case url:
	    try {
		if (form.getFirstValue(QueryResource.search_param) == null)
		    return null;
		query_smiles = form.getFirstValue(QueryResource.search_param).trim();
		if (query_smiles == null)
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Empty query");
		IAtomContainer mol = getFromURI(query_smiles.trim());
		AtomConfigurator c = new AtomConfigurator();
		mol = c.process(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		SmilesGenerator g = new SmilesGenerator(true);
		g.setUseAromaticityFlag(aromatic);
		return g.createSMILES((IMolecule) mol);
	    } catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	    }
	default:
	    return null;
	}

    }

    protected IAtomContainer getMolecule(Form form) throws ResourceException {
	QueryType query_type;
	String query_smiles = "";
	try {
	    query_type = QueryType.valueOf(form.getFirstValue("type"));

	} catch (Exception x) {
	    query_type = QueryType.smiles;
	}
	switch (query_type) {
	case mol: {
	    try {
		query_smiles = form.getFirstValue(QueryResource.b64search_param);
		if (query_smiles != null) {
		    query_smiles = new String(Base64.decode(query_smiles));
		} else
		    query_smiles = form.getFirstValue(QueryResource.search_param);
		return MoleculeTools.readMolfile(query_smiles);
	    } catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	    }
	}
	case smiles:
	    try {
		query_smiles = form.getFirstValue(QueryResource.b64search_param);
		if (query_smiles != null) {
		    query_smiles = new String(Base64.decode(query_smiles));
		} else
		    query_smiles = form.getFirstValue(QueryResource.search_param);

		if (query_smiles == null)
		    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Empty smiles");
		if (query_smiles.startsWith(AmbitCONSTANTS.INCHI)) {
		    InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
		    InChIToStructure c = f.getInChIToStructure(query_smiles, SilentChemObjectBuilder.getInstance());

		    if ((c == null) || (c.getAtomContainer() == null) || (c.getAtomContainer().getAtomCount() == 0))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Empty molecule");
		    else
			return c.getAtomContainer();

		} else {

		    IAtomContainer molecule = null;
		    try {
			return MoleculeTools.getMolecule(query_smiles);
		    } catch (InvalidSmilesException x) {
			Name2StructureProcessor processor = new Name2StructureProcessor();
			try {
			    molecule = processor.process(query_smiles);
			    return molecule;
			} catch (Exception xx) {
			    molecule = null;
			    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, String.format(
				    "Invalid smiles %s", query_smiles), x);
			}
		    }

		}
	    } catch (ResourceException x) {
		throw x;
	    } catch (Exception x) {
		throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x.getMessage(), x);
	    }

	case url: {
	    query_smiles = form.getFirstValue(QueryResource.search_param);
	    if (query_smiles == null)
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Empty query");
	    return getFromURI(query_smiles);

	}
	default:
	    return null;
	}

    }

}
