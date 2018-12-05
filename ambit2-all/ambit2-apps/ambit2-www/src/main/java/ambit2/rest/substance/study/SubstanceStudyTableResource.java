package ambit2.rest.substance.study;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.db.substance.study.SubstanceStudyFlatQuery;
import ambit2.db.substance.study.SubstanceStudyFlatQuery._QUERY_TYPE;
import ambit2.rest.OpenTox;
import ambit2.user.rest.resource.AmbitDBQueryResource;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.bucket.Bucket;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.db.reporter.BucketCSVReporter;
import net.idea.restnet.db.reporter.BucketJSONReporter;

public class SubstanceStudyTableResource<Q extends IQueryRetrieval<Bucket>> extends AmbitDBQueryResource<Q, Bucket> {
	public final static String investigation = OpenTox.URI.investigation.getURI();

	@Override
	public String getTemplateName() {
		return "investigation.ftl";
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(
				new MediaType[] { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JAVA_OBJECT });
	}

	public SubstanceStudyTableResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant) throws AmbitException, ResourceException {

		/* workaround for clients not being able to set accept headers */
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null)
			variant.setMediaType(new MediaType(media));

		String filenamePrefix = getRequest().getResourceRef().getPath();

		if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			BucketCSVReporter cmpreporter = new BucketCSVReporter();
			return new OutputWriterConvertor(cmpreporter, MediaType.TEXT_CSV);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			String jsonpcallback = getParams().getFirstValue("jsonp");
			if (jsonpcallback == null)
				jsonpcallback = getParams().getFirstValue("callback");
			return new OutputWriterConvertor(createJSONReporter(), MediaType.APPLICATION_JAVASCRIPT);
		} else { // json by default
			return new OutputWriterConvertor(createJSONReporter(), MediaType.APPLICATION_JSON, filenamePrefix);
		}

	}

	protected BucketJSONReporter createJSONReporter() {
		String command = "results";
		try {
			if (Boolean.parseBoolean(getParams().getFirstValue("array").toString()))
				command = null;
		} catch (Exception x) {
		}
		return new BucketJSONReporter(command, null, null);
	}

	@Override
	protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
		try {
			SubstanceStudyFlatQuery q = null;
			Form form = getRequest().getResourceRef().getQueryAsForm();
			String search = null;
			String[] inchikey = null;
			String[] smiles = null;
			try {
				search = form.getFirstValue("search");
				if (search == null)
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			} catch (Exception x) {
			}

			try {
				inchikey = form.getValuesArray("inchikey");
			} catch (Exception x) {
			}
			try {
				smiles = form.getValuesArray("smiles");
				//tbd convert ot inchikeys
			} catch (Exception x) {
			}			
			
			_QUERY_TYPE qtype = _QUERY_TYPE.bysubstance;
			try {
				qtype = _QUERY_TYPE.valueOf(form.getFirstValue("type"));
			} catch (Exception x) {
				qtype = null;
			}
			if (qtype == null)
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			switch (qtype) {
			case byinvestigation: {
				ProtocolApplication papp = new ProtocolApplication(null);
				papp.setInvestigationUUID(search);
				q = new SubstanceStudyFlatQuery(papp);
				break;
			}
			case byprovider: {
				q = new SubstanceStudyFlatQuery(qtype, search);
				break;
			}
			case bycitation: {
				q = new SubstanceStudyFlatQuery(qtype, search);
				break;
			}
			case bystudytype: {
				Protocol p = new Protocol(null);
				p.setCategory(search);
				q = new SubstanceStudyFlatQuery(p);
				break;
			}
			case bysubstance: {
				SubstanceRecord record = new SubstanceRecord();
				record.setSubstanceUUID(search);
				q = new SubstanceStudyFlatQuery(record);
				break;
			}
			case bystructure: {
				if (inchikey != null)
					try {
						Protocol._categories category = Protocol._categories.valueOf(search);
						Protocol p = new Protocol(null);
						p.setCategory(category.name());
						p.setTopCategory(category.getTopCategory());
						q = new SubstanceStudyFlatQuery(p, inchikey);
						break;
					} catch (Exception x) {
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
					}

			}
			default: {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
			}
			return (Q) q;
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
		}
	}

	@Override
	protected String getExtension(MediaType mediaType) {

		if (MediaType.APPLICATION_JSON.equals(mediaType))
			return ".json";
		else
			return super.getExtension(mediaType);

	}

	@Override
	public String getConfigFile() {
		return "ambit2/rest/config/ambit2.pref";
	}
}
