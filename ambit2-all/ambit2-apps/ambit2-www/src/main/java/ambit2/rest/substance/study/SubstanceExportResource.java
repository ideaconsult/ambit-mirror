package ambit2.rest.substance.study;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.bucket.Bucket;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.r.QueryAbstractReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.db.reporter.AbstractBucketJsonReporter;

import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.core.io.json.SubstanceStudyParser;
import ambit2.db.substance.ReadSubstance;
import ambit2.db.substance.study.SubstanceStudyDetailsProcessor;
import ambit2.rest.substance.SubstanceResource;

/**
 * /admin/export/substance?media=application%2Fjson
 * 
 * @author nina
 * 
 * @param <Q>
 * @param <T>
 */
public class SubstanceExportResource<Q extends IQueryRetrieval<SubstanceRecord>, T extends SubstanceRecord>
		extends SubstanceResource<Q, T> {

	@Override
	public String getTemplateName() {
		return "jsonplaceholder.ftl";
	}

	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		return (Q) new ReadSubstance();
	}

	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation put(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation put(Representation representation)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation delete() throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected IProcessor<Q, Representation> createJSONReporter(
			String filenamePrefix) {
		String jsonpcallback = getParams().getFirstValue("jsonp");
		if (jsonpcallback == null)
			jsonpcallback = getParams().getFirstValue("callback");

		String command = "results";
		try {
			if (Boolean.parseBoolean(getParams().getFirstValue("array").toString())) command = null;
		} catch (Exception x) {}		
		return new OutputWriterConvertor<SubstanceRecord, Q>(
				(QueryAbstractReporter<SubstanceRecord, Q, Writer>) new Substance2BucketJsonReporter(command),
				jsonpcallback == null ? MediaType.APPLICATION_JSON
						: MediaType.APPLICATION_JAVASCRIPT, filenamePrefix);

	}
}

class Substance2BucketJsonReporter extends
		AbstractBucketJsonReporter<SubstanceRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8415073915990185959L;
	protected Bucket bucket;
	protected ObjectMapper dx = new ObjectMapper();

	public Substance2BucketJsonReporter(String command) {
		super(command,null,null);
		bucket = new Bucket();
		bucket.setHeader(new String[] { "name", "publicname", "owner_name",
				"s_uuid", "substanceType", "_childDocuments_" , "type_s"});

		SubstanceStudyDetailsProcessor paReader = new SubstanceStudyDetailsProcessor();

		getProcessors().clear();
		getProcessors().add(paReader);
		getProcessors().add(
				new DefaultAmbitProcessor<SubstanceRecord, SubstanceRecord>() {
					@Override
					public SubstanceRecord process(SubstanceRecord target)
							throws Exception {
						processItem(target);
						return target;
					};
				});

	}

	private static final String[] study_header = new String[] {
			"document_uuid", "type_s", "topcategory", "endpointcategory",
			"guidance", "endpoint", "effectendpoint", "reference_owner",
			"reference_year", "reference", "loQualifier", "loValue",
			"upQualifier", "upValue", "err", "errQualifier", "conditions",
			"params", "textValue", "interpretation_result","unit" };

	@Override
	public Bucket transform(SubstanceRecord record) {
		bucket.clear();
		bucket.put("name", record.getSubstanceName());
		bucket.put("publicname", record.getPublicName());
		if (!"".equals(record.getOwnerName()))
			bucket.put("owner_name", record.getOwnerName());
		bucket.put("s_uuid", record.getSubstanceUUID());
		bucket.put("substanceType", record.getSubstancetype());
		bucket.put("type_s", "substance");

		List<Bucket> _childDocuments_ = new ArrayList<>();
		bucket.put("_childDocuments_", _childDocuments_);
		if (record.getMeasurements() != null)
			for (ProtocolApplication<Protocol, Object, String, Object, String> papp : record
					.getMeasurements()) {
				Bucket study = new Bucket();
				study.setHeader(study_header);
				study.put("document_uuid", papp.getDocumentUUID());
				study.put("type_s", "study");
				if (papp.getParameters() != null)
					if (papp.getParameters() instanceof IParams)
						study.put("params", papp.getParameters());
					else {
						study.put("params", SubstanceStudyParser
								.parseConditions(dx, papp.getParameters()
										.toString()));
					}
				if (!"".equals(papp.getInterpretationResult()))
					study.put("interpretation_result",
							papp.getInterpretationResult());
				if (papp.getEffects() != null)
					for (EffectRecord<String, Object, String> e : papp
							.getEffects()) {

						study.put("topcategory", papp.getProtocol()
								.getTopCategory());
						study.put("endpointcategory", papp.getProtocol()
								.getCategory());
						study.put("endpoint", papp.getProtocol().getEndpoint());
						if (!"".equals(papp.getProtocol().getGuideline().get(0)))
							study.put("guidance", papp.getProtocol()
									.getGuideline().get(0));
						study.put("effectendpoint", e.getEndpoint());
						if (!"".equals(papp.getReferenceOwner()))
							study.put("reference_owner",
									papp.getReferenceOwner());
						study.put("reference_year", papp.getReferenceYear());
						study.put("reference", papp.getReference());
						study.put("unit", e.getUnit());
						if (!"".equals(e.getLoQualifier()))
							study.put("loQualifier", e.getLoQualifier());
						study.put("loValue", e.getLoValue());
						if (!"".equals(e.getUpQualifier()))
							study.put("upQualifier", e.getUpQualifier());
						study.put("upValue", e.getUpValue());
						if (!"".equals(e.getErrQualifier()))
							study.put("errQualifier", e.getErrQualifier());
						study.put("err", e.getErrorValue());

						if (e.getConditions() != null)
							if (e.getConditions() instanceof IParams)
								study.put("conditions", e.getConditions());
							else {
								study.put("conditions", SubstanceStudyParser
										.parseConditions(dx, e.getConditions()
												.toString()));
							}
						final String textValueTag = "textValue";
						if (e.getTextValue() != null)
							if (e.getTextValue().toString().startsWith("{")) {
								IParams nonzero = SubstanceStudyParser
										.parseTextValueProteomics(dx, e
												.getTextValue().toString());
								study.put(textValueTag, nonzero);
							} else
								study.put(textValueTag, e.getTextValue());

						_childDocuments_.add(study);
					}
			}
		return bucket;
	}

}
