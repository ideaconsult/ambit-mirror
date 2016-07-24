package ambit2.rest.substance.study;

import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.bucket.Bucket;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.MasterDetailsProcessor;
import net.idea.modbcum.r.QueryAbstractReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.db.reporter.AbstractBucketJsonReporter;

import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.core.io.json.SubstanceStudyParser;
import ambit2.db.substance.ids.ReadChemIdentifiersByComposition;
import ambit2.db.substance.ids.ReadSubstanceIdentifiers;
import ambit2.db.substance.relation.ReadSubstanceComposition;
import ambit2.db.substance.study.SubstanceStudyDetailsProcessor;
import ambit2.rest.substance.SubstanceResource;
import ambit2.rest.substance.study.SubstanceExportResource._JSON_MODE;

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
	enum _JSON_MODE {
		experiment, substance
	}

	protected _JSON_MODE jsonmode = _JSON_MODE.experiment;
	protected String summaryMeasurement = null;

	@Override
	public String getTemplateName() {
		return "jsonplaceholder.ftl";
	}

	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		try {
			jsonmode = _JSON_MODE.valueOf(form.getFirstValue("json_mode"));
		} catch (Exception x) {
			jsonmode = _JSON_MODE.experiment;
		}
		try {
			summaryMeasurement = form.getFirstValue("measurement")
					.toUpperCase();
		} catch (Exception x) {
			summaryMeasurement = null;
		}
		return super.createQuery(context, request, response);
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
			if (Boolean.parseBoolean(getParams().getFirstValue("array")
					.toString()))
				command = null;
		} catch (Exception x) {
		}
		ProcessorsChain chain = new ProcessorsChain<>();
		chain.add(new SubstanceStudyDetailsProcessor());
		getCompositionProcessors(chain);
		return new OutputWriterConvertor<SubstanceRecord, Q>(
				(QueryAbstractReporter<SubstanceRecord, Q, Writer>) new Substance2BucketJsonReporter(
						command, chain, jsonmode, summaryMeasurement),
				jsonpcallback == null ? MediaType.APPLICATION_JSON
						: MediaType.APPLICATION_JAVASCRIPT, filenamePrefix);

	}

	@Override
	protected void getCompositionProcessors(ProcessorsChain chain) {
		final SubstanceEndpointsBundle bundle = null;
		final ReadSubstanceComposition q = new ReadSubstanceComposition();
		MasterDetailsProcessor<SubstanceRecord, CompositionRelation, IQueryCondition> compositionReader = new MasterDetailsProcessor<SubstanceRecord, CompositionRelation, IQueryCondition>(
				q) {
			/**
		     * 
		     */
			private static final long serialVersionUID = -4012709744454255487L;

			@Override
			public SubstanceRecord process(SubstanceRecord target)
					throws Exception {
				if (target == null || (target.getIdsubstance() <= 0))
					return target;
				q.setBundle(bundle);
				if (target.getRelatedStructures() != null)
					target.getRelatedStructures().clear();
				return super.process(target);
			}

			protected SubstanceRecord processDetail(SubstanceRecord target,
					CompositionRelation detail) throws Exception {
				target.addStructureRelation(detail);
				q.setRecord(null);
				return target;
			};
		};
		chain.add(compositionReader);
		final ReadChemIdentifiersByComposition qids = new ReadChemIdentifiersByComposition();
		MasterDetailsProcessor<SubstanceRecord, IStructureRecord, IQueryCondition> idsReader = new MasterDetailsProcessor<SubstanceRecord, IStructureRecord, IQueryCondition>(
				qids) {
			private static final long serialVersionUID = -3547633994853667140L;

			@Override
			protected SubstanceRecord processDetail(SubstanceRecord target,
					IStructureRecord detail) throws Exception {
				return qids.processDetail(target, detail);
			}

			@Override
			public SubstanceRecord process(SubstanceRecord target)
					throws AmbitException {
				try {
					return super.process(target);
				} catch (Exception x) {
					logger.log(Level.FINEST, x.getMessage());
					return target;
				}
			}
		};
		chain.add(idsReader);

		IQueryRetrieval<ExternalIdentifier> queryP = new ReadSubstanceIdentifiers();
		MasterDetailsProcessor<SubstanceRecord, ExternalIdentifier, IQueryCondition> substanceIdentifiers = new MasterDetailsProcessor<SubstanceRecord, ExternalIdentifier, IQueryCondition>(
				queryP) {
			/**
							     * 
							     */
			private static final long serialVersionUID = 5246468397385927943L;

			@Override
			protected void configureQuery(
					SubstanceRecord target,
					IParameterizedQuery<SubstanceRecord, ExternalIdentifier, IQueryCondition> query)
					throws AmbitException {
				query.setFieldname(target);
			}

			@Override
			protected SubstanceRecord processDetail(SubstanceRecord target,
					ExternalIdentifier detail) throws Exception {
				if (target.getExternalids() == null)
					target.setExternalids(new ArrayList<ExternalIdentifier>());
				target.getExternalids().add(detail);
				return target;
			}
		};
		substanceIdentifiers.setCloseConnection(false);
		chain.add(substanceIdentifiers);

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
	protected _JSON_MODE jsonmode;
	protected String summaryMeasurement = null;

	public Substance2BucketJsonReporter(String command, ProcessorsChain chain,
			_JSON_MODE jsonmode, String summaryMeasurement) {
		super(command, null, null);
		this.summaryMeasurement = summaryMeasurement;
		this.jsonmode = jsonmode;
		// new IProcessor[] { new SubstanceStudyDetailsProcessor() });
		switch (jsonmode) {
		case experiment: {
			bucket = new BucketDenormalised<>();
			break;
		}
		default: {
			bucket = new Bucket();
		}
		}

		bucket.setHeader(study_headers[0]);

		IProcessor itemp = getProcessors().get(0);
		getProcessors().clear();
		for (int i = 0; i < chain.size(); i++)
			getProcessors().add((IProcessor) chain.get(i));
		getProcessors().add(itemp);

	}

	private static final String[][] study_headers = new String[][] {
			{ "name", "publicname", "owner_name", "s_uuid", "substanceType",
					"_childDocuments_", "type.s", "ChemicalName.CONSTITUENT",
					"ChemicalName.ADDITIVE", "ChemicalName.IMPURITY",
					"ChemicalName.CORE", "ChemicalName.COATING",
					"ChemicalName.FUNCTIONALISATION", "ChemicalName.DOPING",
					"content",

					"TradeName.CONSTITUENT", "TradeName.ADDITIVE",
					"TradeName.IMPURITY", "TradeName.CORE",
					"TradeName.COATING", "TradeName.FUNCTIONALISATION",
					"TradeName.DOPING",

					"CASRN.CONSTITUENT", "CASRN.ADDITIVE", "CASRN.IMPURITY",
					"CASRN.CORE", "CASRN.COATING", "CASRN.FUNCTIONALISATION",
					"CASRN.DOPING",

					"EINECS.CONSTITUENT", "EINECS.ADDITIVE", "EINECS.IMPURITY",
					"EINECS.CORE", "EINECS.COATING",
					"EINECS.FUNCTIONALISATION", "EINECS.DOPING",

					"IUCLID5_UUID.CONSTITUENT", "IUCLID5_UUID.ADDITIVE",
					"IUCLID5_UUID.IMPURITY", "IUCLID5_UUID.CORE",
					"IUCLID5_UUID.COATING", "IUCLID5_UUID.FUNCTIONALISATION",
					"IUCLID5_UUID.DOPING", 
			
					"COMPOSITION.CONSTITUENT", "COMPOSITION.ADDITIVE", "COMPOSITION.IMPURITY",
					"COMPOSITION.CORE", "COMPOSITION.COATING", "COMPOSITION.FUNCTIONALISATION",
					"COMPOSITION.DOPING"					
			 },
			{ "id", "document_uuid", "type_s", "topcategory",
					"endpointcategory", "guidance", "endpoint",
					"effectendpoint", "reference_owner", "reference_year",
					"reference", "loQualifier", "loValue", "upQualifier",
					"upValue", "err", "errQualifier", "conditions", "params",
					"textValue", "interpretation_result", "unit", "category",
					"idresult" }, { "P-CHEM.PC_GRANULOMETRY_SECTION.SIZE" }

	};

	@Override
	public void setConnection(Connection conn) throws DbAmbitException {
		super.setConnection(conn);

	}

	@Override
	public Bucket transform(SubstanceRecord record) {

		switch (jsonmode) {
		case experiment: {
			bucket.clear();
			bucket.setHeaders(study_headers);
			substance2Bucket(record, bucket);
			if (record.getRelatedStructures() != null) {
				Map<String, Integer> composition = new Hashtable<String, Integer>();
				for (CompositionRelation rel : record.getRelatedStructures()) {
					composition2Bucket(rel, bucket,composition);
				}
				Iterator<String> keys = composition.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					bucket.put(String.format("COMPOSITION.%s",key), composition.get(key));
				}
				
			}	

			List<Bucket> ids = new ArrayList<Bucket>();
			if (record.getExternalids() != null)
				for (ExternalIdentifier id : record.getExternalids()) {
					// these should not be in external ids in first place
					if ("Has_Identifier".equals(id.getSystemDesignator()))
						continue;
					if ("Composition".equals(id.getSystemDesignator()))
						continue;
					if ("DATASET".equals(id.getSystemDesignator()))
						continue;
					if ("SOURCE".equals(id.getSystemDesignator()))
						continue;
					if ("Coating".equals(id.getSystemDesignator()))
						continue;
					if ("COD ID".equals(id.getSystemDesignator())) {
						bucket.put("content", String.format(
								"http://www.crystallography.net/cod/%s.html",
								id.getSystemIdentifier()));
					}
					if (id.getSystemIdentifier().startsWith("http"))
						bucket.put("content", id.getSystemIdentifier());
					ids.add(externalids2Bucket(id));
				}

			if (record.getMeasurements() != null)
				for (ProtocolApplication<Protocol, Object, String, Object, String> papp : record
						.getMeasurements()) {
					if (papp.getEffects() != null)
						for (EffectRecord<String, Object, String> e : papp
								.getEffects()) {
							Bucket effect = ((BucketDenormalised) bucket)
									.addBucket(new Bucket());
							effect.put(
									"id",
									String.format("%s/%d",
											papp.getDocumentUUID(),
											e.getIdresult()));
							effect.put("idresult", e.getIdresult());
							List<Bucket> _childDocuments_ = new ArrayList<>();
							effect.put("_childDocuments_", _childDocuments_);

							if (ids != null)
								for (Bucket id : ids)
									_childDocuments_.add(id);

							protocolapplication2Bucket(papp, effect);
							protocol2Bucket(papp.getProtocol(), effect);
							reference2Bucket(papp, effect);
							// params
							if (papp.getParameters() != null) {
								Bucket params = new Bucket();
								params.setHeader(new String[] {
										"document_uuid", "params" });
								if (papp.getParameters() != null) {
									params2Bucket(papp, params);
									params.put("document_uuid",
											papp.getDocumentUUID());
								}
								_childDocuments_.add(params);
							}

							effectrecord2bucket(papp.getProtocol(), e, effect);

							// "P-CHEM/PC_GRANULOMETRY_SECTION/SIZE"
							if (summaryMeasurement != null)
								summarymeasurement2bucket(papp.getProtocol(),
										e, bucket);

							// conditions
							Bucket conditions = new Bucket();
							conditions.setHeader(new String[] {
									"document_uuid", "conditions" });
							conditions.put("document_uuid",
									papp.getDocumentUUID());
							if (e.getConditions() != null)
								condition2bucket(e, conditions);
							_childDocuments_.add(conditions);
						}
					else {
						// just protocolapplication
						Bucket effect = ((BucketDenormalised) bucket)
								.addBucket(new Bucket());
						effect.put("id", papp.getDocumentUUID());
						protocolapplication2Bucket(papp, effect);
						protocol2Bucket(papp.getProtocol(), effect);
						reference2Bucket(papp, effect);

						List<Bucket> _childDocuments_ = new ArrayList<>();
						effect.put("_childDocuments_", _childDocuments_);

						// params
						if (papp.getParameters() != null) {
							Bucket params = new Bucket();
							params.setHeader(new String[] { "document_uuid",
									"params" });
							if (papp.getParameters() != null) {
								params2Bucket(papp, params);
								params.put("document_uuid",
										papp.getDocumentUUID());
							}
							_childDocuments_.add(params);
						}

					}
				}
			else
				((BucketDenormalised) bucket).addBucket(new Bucket());
			break;
		}
		case substance: {
			// each substance is one record, effect records are child documents
			bucket.clear();
			substance2Bucket(record, bucket);
			if (record.getRelatedStructures() != null) {
				Map<String, Integer> composition = new Hashtable<String, Integer>();
				for (CompositionRelation rel : record.getRelatedStructures()) {
					composition2Bucket(rel, bucket,composition);
				}
				bucket.put("composition", composition.toString());
			}	

			List<Bucket> _childDocuments_ = new ArrayList<>();
			bucket.put("_childDocuments_", _childDocuments_);
			if (record.getMeasurements() != null)
				for (ProtocolApplication<Protocol, Object, String, Object, String> papp : record
						.getMeasurements()) {

					Bucket study = new Bucket();
					protocolapplication2Bucket(papp, study);
					study.setHeader(study_headers[1]);

					if (papp.getParameters() != null)
						params2Bucket(papp, study);

					if (papp.getEffects() != null)
						for (EffectRecord<String, Object, String> e : papp
								.getEffects()) {
							protocol2Bucket(papp.getProtocol(), study);
							reference2Bucket(papp, study);
							effectrecord2bucket(papp.getProtocol(), e, study);
							condition2bucket(e, study);
							study.put(
									"id",
									String.format("%s/%d",
											papp.getDocumentUUID(),
											e.getIdresult()));
							_childDocuments_.add(study);
						}
				}
		}
		}

		return bucket;
	}

	protected void composition2Bucket(CompositionRelation component,
			Bucket bucket, Map<String, Integer> composition) {
		String ctype = component.getRelationType().name()
				.replace("HAS_", "");
		if (composition.get(ctype) == null)
			composition.put(ctype, new Integer(1));
		else
			composition
					.put(ctype, ((Integer) composition.get(ctype)) + 1);

		for (Property p : component.getSecondStructure().getRecordProperties()) {
			String label = String
					.format("%s.%s",
							p.getLabel().replace(
									"http://www.opentox.org/api/1.1#", ""),
							ctype);
			String val = component.getSecondStructure().getRecordProperty(p)
					.toString().trim();
			if ("".equals(val))
				continue;
			List<String> vals;
			if (bucket.get(label) == null) {
				vals = new ArrayList<String>();
				bucket.put(label, vals);
			} else
				vals = (List) bucket.get(label);

			if (vals.indexOf(val) < 0)
				vals.add(val);
		}
	}

	protected void substance2Bucket(SubstanceRecord record, Bucket bucket) {
		bucket.put("name", record.getSubstanceName());
		bucket.put("publicname", record.getPublicName());
		if (!"".equals(record.getOwnerName()))
			bucket.put("owner_name", record.getOwnerName());
		bucket.put("s_uuid", record.getSubstanceUUID());
		bucket.put("substanceType", record.getSubstancetype());
		bucket.put("type_s", "substance");
		bucket.put("id", record.getSubstanceUUID());

		bucket.put("content", record.getContent());
	}

	protected Bucket externalids2Bucket(ExternalIdentifier id) {
		Bucket ids = new Bucket();
		ids.setHeader(new String[] { "type_s", "ID", "VALUE" });
		ids.put("type_s", "externalid");
		ids.put("ID", id.getSystemDesignator());
		ids.put("VALUE", id.getSystemIdentifier());
		return ids;
	}

	protected void protocolapplication2Bucket(
			ProtocolApplication<Protocol, Object, String, Object, String> papp,
			Bucket bucket) {
		bucket.put("document_uuid", papp.getDocumentUUID());
		bucket.put("type_s", "study");
		if (papp.getInterpretationResult() != null
				&& !"".equals(papp.getInterpretationResult()))
			bucket.put("interpretation_result", papp.getInterpretationResult()
					.toUpperCase());
	}

	protected void reference2Bucket(
			ProtocolApplication<Protocol, Object, String, Object, String> papp,
			Bucket bucket) {
		if (papp.getReferenceOwner() != null
				&& !"".equals(papp.getReferenceOwner()))
			bucket.put("reference_owner", papp.getReferenceOwner()
					.toUpperCase());
		bucket.put("reference_year", papp.getReferenceYear());
		bucket.put("reference", papp.getReference());

	}

	protected void params2Bucket(
			ProtocolApplication<Protocol, Object, String, Object, String> papp,
			Bucket bucket) {
		if (papp.getParameters() instanceof IParams)
			bucket.put("params", papp.getParameters());
		else {
			bucket.put("params", SubstanceStudyParser.parseConditions(dx, papp
					.getParameters().toString()));
		}

	}

	protected void protocol2Bucket(Protocol protocol, Bucket bucket) {
		bucket.put("topcategory", protocol.getTopCategory());
		bucket.put("endpointcategory", protocol.getCategory());

		bucket.put(
				"category",
				String.format("%s/%s", protocol.getTopCategory(),
						protocol.getCategory()));

		bucket.put("endpoint", protocol.getEndpoint().toUpperCase());
		if (protocol.getGuideline().get(0) != null
				&& !"".equals(protocol.getGuideline().get(0)))
			bucket.put("guidance", protocol.getGuideline().get(0).toUpperCase());
	}

	protected void summarymeasurement2bucket(Protocol protocol,
			EffectRecord<String, Object, String> e, Bucket bucket) {
		if ((e != null)
				& (e.getEndpoint() != null)
				&& e.getEndpoint().toUpperCase().indexOf(summaryMeasurement) >= 0) {

			String label = String.format("%s.%s.%s", protocol.getTopCategory(),
					protocol.getCategory(), summaryMeasurement);
			String val = String
					.format("%s%4.1f%s",
							(e.getLoQualifier() == null || "".equals(e
									.getLoQualifier())) ? "" : (e
									.getLoQualifier() + " "), e.getLoValue(), e
									.getUnit() == null ? "" : e.getUnit());
			List<String> vals;
			if (bucket.get(label) == null) {
				vals = new ArrayList<String>();
				bucket.put(label, vals);
			} else
				vals = (List<String>) bucket.get(label);
			if (vals.indexOf(val) == -1)
				vals.add(val);
		}
	}

	protected void effectrecord2bucket(Protocol protocol,
			EffectRecord<String, Object, String> e, Bucket bucket) {
		if (e.getEndpoint() != null)
			bucket.put("effectendpoint", e.getEndpoint().toUpperCase());
		bucket.put("unit", e.getUnit());
		if (!"".equals(e.getLoQualifier()))
			bucket.put("loQualifier", e.getLoQualifier());
		bucket.put("loValue", e.getLoValue());
		if (!"".equals(e.getUpQualifier()))
			bucket.put("upQualifier", e.getUpQualifier());
		bucket.put("upValue", e.getUpValue());
		if (!"".equals(e.getErrQualifier()))
			bucket.put("errQualifier", e.getErrQualifier());
		bucket.put("err", e.getErrorValue());

		final String textValueTag = "textValue";
		if (e.getTextValue() != null)
			if (e.getTextValue().toString().startsWith("{")) {
				IParams nonzero = SubstanceStudyParser
						.parseTextValueProteomics(dx, e.getTextValue()
								.toString());
				bucket.put(textValueTag, nonzero);
			} else
				bucket.put(textValueTag, e.getTextValue());

	}

	protected void condition2bucket(EffectRecord<String, Object, String> e,
			Bucket bucket) {
		if (e.getConditions() != null)
			if (e.getConditions() instanceof IParams)
				bucket.put("conditions", e.getConditions());
			else {
				bucket.put("conditions", SubstanceStudyParser.parseConditions(
						dx, e.getConditions().toString()));
			}

	}
}

/**
 * This is a hack to return multiple JSON records per substance
 * 
 * @author nina
 * 
 * @param <V>
 */
class BucketDenormalised<V> extends Bucket<V> {
	protected List<Bucket> buckets = new ArrayList<>();
	protected String summaryMeasurement = "P-CHEM.PC_GRANULOMETRY_SECTION.SIZE";

	@Override
	public void clear() {
		super.clear();
		buckets.clear();
	}

	/**
	 * Sets the same header and copies properties
	 * 
	 * @param bucket
	 * @return
	 */
	public Bucket addBucket(Bucket bucket) {
		bucket.setHeader(getHeader());
		for (String header : getHeader()) {
			bucket.put(header, get(header));
		}
		buckets.add(bucket);
		return bucket;
	}

	@Override
	public String asJSON() {
		StringBuilder b = new StringBuilder();
		String d = "";
		for (Bucket bucket : buckets) {
			bucket.put(summaryMeasurement, get(summaryMeasurement));
			b.append(d);
			b.append(bucket.asJSON());
			d = ",\n";
		}
		return b.toString();
	}
}