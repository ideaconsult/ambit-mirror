package ambit2.rest.bundle;

import java.awt.Dimension;
import java.sql.Connection;

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

import com.hp.hpl.jena.ontology.OntModel;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.reporters.ImageReporter;
import ambit2.db.reporters.xlsx.SubstanceRecordXLSXReporter;
import ambit2.db.update.bundle.substance.ReadSubstancesByBundle;
import ambit2.export.isa.base.ISAConst;
import ambit2.rest.ImageConvertor;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.query.AmbitDBResource;
import ambit2.rest.substance.SubstanceCSVReporter;
import ambit2.rest.substance.SubstanceJSONReporter;
import ambit2.rest.substance.SubstanceRDFReporter;
import ambit2.rest.substance.SubstanceURIReporter;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.c.ChemicalMediaType;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.rdf.ns.OT;

/**
 * Substances per /bundle/{id}
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class BundleSubstanceResource<Q extends IQueryRetrieval<SubstanceRecord>>
		extends AmbitDBResource<Q, SubstanceRecord> {
	
	public static MediaType ISAJSON = new MediaType(ISAConst.ISAFormat.JSON.getMediaType());
	protected SubstanceEndpointsBundle[] bundles;
	protected boolean enableFeatures = false;

	public BundleSubstanceResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "substance.ftl";
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] { MediaType.TEXT_HTML,
				MediaType.TEXT_PLAIN, MediaType.TEXT_URI_LIST,
				MediaType.TEXT_CSV, MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE, MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVASCRIPT,
				MediaType.APPLICATION_JAVA_OBJECT, MediaType.APPLICATION_EXCEL,
				MediaType.APPLICATION_MSOFFICE_XLSX,
				ChemicalMediaType.APPLICATION_JSONLD, ISAJSON

		});
	}

	@Override
	protected String getObjectURI(Form queryForm) throws ResourceException {
		return null;
	}

	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			Form form, SubstanceRecord item) throws ResourceException {
		SubstanceEndpointsBundle bundle = null;
		Object id = getRequest().getAttributes().get(
				OpenTox.URI.bundle.getKey());
		if ((id != null))
			try {
				bundle = new SubstanceEndpointsBundle(new Integer(
						Reference.decode(id.toString())));
			} catch (Exception x) {
			}

		Connection conn = null;
		try {
			SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> r = new SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>>(
					getRequest());
			DBConnection dbc = new DBConnection(getApplication().getContext(),
					getConfigFile());
			conn = dbc.getConnection(30, true, 8);
			return new CallableSubstanceBundle(bundle, r, method, form, conn,
					getToken());
		} catch (Exception x) {
			try {
				conn.close();
			} catch (Exception xx) {
			}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, x);
		}
	}

	@Override
	protected boolean isAllowedMediaType(MediaType mediaType)
			throws ResourceException {
		return MediaType.APPLICATION_WWW_FORM.equals(mediaType)
				|| MediaType.MULTIPART_FORM_DATA.equals(mediaType);
	}

	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		try {
			enableFeatures = "true"
					.equals(form.getFirstValue("enableFeatures"));
		} catch (Exception x) {
			enableFeatures = false;
		}

		Object idbundle = request.getAttributes().get(
				OpenTox.URI.bundle.getKey());
		if (idbundle != null)
			try {
				Integer idnum = new Integer(Reference.decode(idbundle
						.toString()));
				ReadSubstancesByBundle q = new ReadSubstancesByBundle();
				q.setEnableFeatures(enableFeatures);
				SubstanceEndpointsBundle b = new SubstanceEndpointsBundle();
				if (bundles == null)
					bundles = new SubstanceEndpointsBundle[] { b };
				b.setID(idnum);
				q.setFieldname(b);
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
	protected Q createUpdateQuery(Method method, Context context,
			Request request, Response response) throws ResourceException {
		Object idbundle = request.getAttributes().get(
				OpenTox.URI.bundle.getKey());
		if (idbundle == null)
			throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);

		Object idsubstance = request.getAttributes().get(
				OpenTox.URI.substance.getKey());
		if (Method.POST.equals(method) || Method.PUT.equals(method)) {
			if (idsubstance == null)
				return null;// post allowed only on /bundle/{id}/substance
			// level, not on /bundle/id/substance/{idsubstance}
		} else {
			if (idsubstance != null) {
				try {
					Integer idnum = new Integer(Reference.decode(idbundle
							.toString()));
					SubstanceEndpointsBundle dataset = new SubstanceEndpointsBundle();
					dataset.setID(idnum);
					ReadSubstancesByBundle query = new ReadSubstancesByBundle();
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
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();

		String media = acceptform.getFirstValue("accept-header");
		if (media != null)
			variant.setMediaType(new MediaType(media));

		String filenamePrefix = getRequest().getResourceRef().getPath();

		if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			SubstanceJSONReporter cmpreporter = new SubstanceJSONReporter(
					getRequest(), null, bundles, null, false);
			return new OutputWriterConvertor<SubstanceRecord, Q>(cmpreporter,
					MediaType.APPLICATION_JSON, filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
			Dimension d = new Dimension(250, 250);
			try {
				d.width = Integer.parseInt(acceptform.getFirstValue("w")
						.toString());
			} catch (Exception x) {
			}
			try {
				d.height = Integer.parseInt(acceptform.getFirstValue("h")
						.toString());
			} catch (Exception x) {
			}
			return new ImageConvertor(new ImageReporter(variant.getMediaType()
					.getMainType(), variant.getMediaType().getSubType(), d),
					variant.getMediaType());
		} else if (variant.getMediaType().equals(
				MediaType.APPLICATION_MSOFFICE_XLSX)) {
			return new OutputStreamConvertor<SubstanceRecord, Q>(
					createXLSXReporter(getRequest(), false),
					MediaType.APPLICATION_MSOFFICE_XLSX, filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_EXCEL)) {
			return new OutputStreamConvertor<SubstanceRecord, Q>(
					createXLSXReporter(getRequest(), false),
					MediaType.APPLICATION_EXCEL, filenamePrefix);
		} else if (isRDFMediaType(variant.getMediaType())) {
			return new RDFJenaConvertor(new SubstanceRDFReporter(getRequest(),
					variant.getMediaType()), variant.getMediaType(),
					filenamePrefix) {
				@Override
				protected OntModel createOutput(IQueryRetrieval query)
						throws AmbitException {
					try {
						OntModel jenaModel = OT.createModel();
						jenaModel.setNsPrefix("sio",
								"http://semanticscience.org/resource/");
						jenaModel.setNsPrefix("obo",
								"http://purl.obolibrary.org/obo/");
						jenaModel.setNsPrefix("bao",
								"http://www.bioassayontology.org/bao#");
						jenaModel.setNsPrefix("npo",
								"http://purl.bioontology.org/ontology/npo/");
						jenaModel.setNsPrefix("enm",
								"http://purl.enanomapper.org/onto/");
						return jenaModel;
					} catch (Exception x) {
						throw new AmbitException(x);
					}
				}
			};
		} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return new OutputWriterConvertor<SubstanceRecord, Q>(
					new SubstanceCSVReporter(getRequest(), bundles),
					MediaType.TEXT_CSV, filenamePrefix);

		} else if (variant.getMediaType().equals(
				MediaType.APPLICATION_JAVASCRIPT)) {
			String jsonpcallback = getParams().getFirstValue("jsonp");
			if (jsonpcallback == null)
				jsonpcallback = getParams().getFirstValue("callback");

			return new OutputWriterConvertor<SubstanceRecord, Q>(
					createJSONReporter(getRequest(), jsonpcallback),
					MediaType.APPLICATION_JAVASCRIPT, filenamePrefix);

		} else if (variant.getMediaType().equals(ISAJSON)) {
			return new OutputStreamConvertor<SubstanceRecord, Q>(
					createISAReporter(getRequest()),
					MediaType.APPLICATION_JAVASCRIPT, filenamePrefix);
		} else { // json by default
			return new OutputWriterConvertor<SubstanceRecord, Q>(
					createJSONReporter(getRequest(), null),
					MediaType.APPLICATION_JSON, filenamePrefix);
		}
	}

	protected QueryReporter createXLSXReporter(Request request, boolean hssf) {
		
		String configResource = String.format("config-%s.js",((IFreeMarkerApplication) getApplication()).getProfile());
		return new SubstanceRecordXLSXReporter(request.getRootRef().toString(),
				hssf, bundles, configResource);

	}

	protected QueryReporter createJSONReporter(Request request,
			String jsonpcallback) {
		return new SubstanceJSONReporter(getRequest(), jsonpcallback, bundles,
				null, false);

	}

	protected QueryReporter createISAReporter(Request request) {
		return new BundleISA_JSONReporter(getRequest().getRootRef().toString(), bundles);

	}

	protected boolean isRDFMediaType(MediaType mediaType) {
		return mediaType.equals(MediaType.APPLICATION_RDF_XML)
				|| mediaType.equals(
						MediaType.APPLICATION_RDF_TURTLE)
				|| mediaType.equals(MediaType.TEXT_RDF_NTRIPLES)
				|| mediaType.equals(MediaType.TEXT_RDF_N3)
				|| mediaType.equals(
						ChemicalMediaType.APPLICATION_JSONLD);
	}

	@Override
	protected String getExtension(MediaType mediaType) {

		return super.getExtension(mediaType);
	}
	@Override
	protected Representation get() throws ResourceException {

		return super.get();
	}
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		return super.get(variant);
	}
}
