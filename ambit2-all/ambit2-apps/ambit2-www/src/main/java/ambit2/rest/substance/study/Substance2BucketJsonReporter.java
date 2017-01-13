package ambit2.rest.substance.study;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.Value;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.core.io.json.SubstanceStudyParser;
import net.idea.modbcum.i.bucket.Bucket;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.restnet.db.reporter.AbstractBucketJsonReporter;

public class Substance2BucketJsonReporter extends AbstractBucketJsonReporter<SubstanceRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8415073915990185959L;
	protected Bucket bucket;
	protected ObjectMapper dx = new ObjectMapper();
	protected _JSON_MODE jsonmode = _JSON_MODE.experiment;
	protected String summaryMeasurement = null;

	public enum _JSON_MODE {
		experiment, substance
	}

	public Substance2BucketJsonReporter(String command, ProcessorsChain chain, _JSON_MODE jsonmode,
			String summaryMeasurement) {
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
		if (chain != null)
			for (int i = 0; i < chain.size(); i++)
				getProcessors().add((IProcessor) chain.get(i));
		getProcessors().add(itemp);

	}

	private static final String[][] study_headers = new String[][] {
			{ "name", "publicname", "owner_name", "s_uuid", "substanceType", "_childDocuments_", "type_s",
					"ChemicalName.CONSTITUENT", "ChemicalName.ADDITIVE", "ChemicalName.IMPURITY", "ChemicalName.CORE",
					"ChemicalName.COATING", "ChemicalName.FUNCTIONALISATION", "ChemicalName.DOPING", "content",

					"TradeName.CONSTITUENT", "TradeName.ADDITIVE", "TradeName.IMPURITY", "TradeName.CORE",
					"TradeName.COATING", "TradeName.FUNCTIONALISATION", "TradeName.DOPING",

					"CASRN.CONSTITUENT", "CASRN.ADDITIVE", "CASRN.IMPURITY", "CASRN.CORE", "CASRN.COATING",
					"CASRN.FUNCTIONALISATION", "CASRN.DOPING",

					"EINECS.CONSTITUENT", "EINECS.ADDITIVE", "EINECS.IMPURITY", "EINECS.CORE", "EINECS.COATING",
					"EINECS.FUNCTIONALISATION", "EINECS.DOPING",

					"IUCLID5_UUID.CONSTITUENT", "IUCLID5_UUID.ADDITIVE", "IUCLID5_UUID.IMPURITY", "IUCLID5_UUID.CORE",
					"IUCLID5_UUID.COATING", "IUCLID5_UUID.FUNCTIONALISATION", "IUCLID5_UUID.DOPING",

					"COMPOSITION.CONSTITUENT", "COMPOSITION.ADDITIVE", "COMPOSITION.IMPURITY", "COMPOSITION.CORE",
					"COMPOSITION.COATING", "COMPOSITION.FUNCTIONALISATION", "COMPOSITION.DOPING" },
			{ "id", "document_uuid", "type_s", "topcategory", "endpointcategory", "guidance", "endpoint",
					"effectendpoint", "reference_owner", "reference_year", "reference", "loQualifier", "loValue",
					"upQualifier", "upValue", "err", "errQualifier", "conditions", "params", "textValue",
					"interpretation_result", "unit", "category", "idresult" },
			{ "P-CHEM.PC_GRANULOMETRY_SECTION.SIZE" }

	};
	private static final String[][] study_headers_combined = new String[][] {
			{ "id", "name_s", "publicname_s", "owner_name_s", "substanceType_s", "s_uuid_s", "name_hs", "publicname_hs",
					"owner_name_hs", "substanceType_hs", "s_uuid_hs", "_childDocuments_", "type_s", "component",
					"ChemicalName_s", "TradeName_s", "CASRN_s", "EINECS_s", "IUCLID5_UUID_s", "COMPOSITION_s",
					"SMILES_s", "document_uuid_s", "topcategory_s", "endpointcategory_s", "guidance_s", "endpoint_s",
					"effectendpoint_s", "reference_owner_s", "reference_year_s", "reference_s", "loQualifier_s",
					"loValue_d", "upQualifier_s", "upValue_d", "err_d", "errQualifier_s", "conditions_s", "params",
					"textValue_s", "interpretation_result_s", "unit_s", "category_s", "idresult" } };

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
			substance2Bucket(record, bucket, false);
			if (record.getRelatedStructures() != null) {
				Map<String, Integer> composition = new Hashtable<String, Integer>();
				for (CompositionRelation rel : record.getRelatedStructures()) {
					composition2Bucket(rel, bucket, composition, false);
				}
				Iterator<String> keys = composition.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					bucket.put(String.format("COMPOSITION.%s", key), composition.get(key));
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
						bucket.put("content",
								String.format("http://www.crystallography.net/cod/%s.html", id.getSystemIdentifier()));
					}
					if (id.getSystemIdentifier().startsWith("http"))
						bucket.put("content", id.getSystemIdentifier());
					// ids.add(externalids2Bucket(id));
				}

			if (record.getMeasurements() != null)
				for (ProtocolApplication<Protocol, Object, String, Object, String> papp : record.getMeasurements()) {
					if (papp.getEffects() != null)
						for (EffectRecord<String, Object, String> e : papp.getEffects()) {
							Bucket effect = ((BucketDenormalised) bucket).addBucket(new Bucket());
							effect.put("id", String.format("%s/%d", papp.getDocumentUUID(), e.getIdresult()));
							effect.put("idresult", e.getIdresult());
							List<Bucket> _childDocuments_ = new ArrayList<>();
							effect.put("_childDocuments_", _childDocuments_);

							if (ids != null)
								for (Bucket id : ids)
									_childDocuments_.add(id);

							protocolapplication2Bucket(papp, effect, false);
							protocol2Bucket(papp.getProtocol(), effect, false);
							reference2Bucket(papp, effect, false);
							// params
							if (papp.getParameters() != null) {
								Bucket params = new Bucket();
								params.setHeader(new String[] { "document_uuid", "params" });
								if (papp.getParameters() != null) {
									params2Bucket(papp, params, "params", null);
									params.put("document_uuid", papp.getDocumentUUID());
								}
								_childDocuments_.add(params);
							}

							effectrecord2bucket(papp.getProtocol(), e, effect, false);

							// "P-CHEM/PC_GRANULOMETRY_SECTION/SIZE"
							if (summaryMeasurement != null)
								summarymeasurement2bucket(papp.getProtocol(), e, bucket);

							// conditions
							Bucket conditions = new Bucket();
							conditions.setHeader(new String[] { "document_uuid", "conditions" });
							conditions.put("document_uuid", papp.getDocumentUUID());
							if (e.getConditions() != null)
								condition2bucket(e, conditions, "conditions", null);
							_childDocuments_.add(conditions);
						}
					else {
						// just protocolapplication
						Bucket effect = ((BucketDenormalised) bucket).addBucket(new Bucket());
						effect.put("id", papp.getDocumentUUID());
						protocolapplication2Bucket(papp, effect, true);
						protocol2Bucket(papp.getProtocol(), effect, true);
						reference2Bucket(papp, effect, true);

						List<Bucket> _childDocuments_ = new ArrayList<>();
						effect.put("_childDocuments_", _childDocuments_);

						// params
						if (papp.getParameters() != null) {
							Bucket params = new Bucket();
							params.setHeader(new String[] { "document_uuid", "params" });
							if (papp.getParameters() != null) {
								params2Bucket(papp, params, "params", null);
								params.put("document_uuid", papp.getDocumentUUID());
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
			boolean suffix = true;
			// each substance is one record, effect records are child documents
			bucket.clear();
			bucket.setHeaders(study_headers_combined);
			substance2Bucket(record, bucket, true, "_hs");

			List<Bucket> _childDocuments_ = new ArrayList<>();

			if (record.getRelatedStructures() != null) {

				for (int r = 0; r < record.getRelatedStructures().size(); r++) {
					CompositionRelation rel = record.getRelatedStructures().get(r);
					Bucket bcomposition = composition2Buckets(record, rel, suffix);
					bcomposition.put("id", String.format("%s/c/%d", record.getSubstanceUUID(), (r + 1)));
					_childDocuments_.add(bcomposition);
				}
			}

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
						bucket.put("content",
								String.format("http://www.crystallography.net/cod/%s.html", id.getSystemIdentifier()));
					}
					if (id.getSystemIdentifier().startsWith("http"))
						bucket.put("content", id.getSystemIdentifier());
					Bucket bids = externalids2Bucket(id);
					// _childDocuments_.add(bids);
				}

			if (record.getMeasurements() != null) {
				for (ProtocolApplication<Protocol, Object, String, Object, String> papp : record.getMeasurements()) {

					IParams prm = null;
					if (papp.getParameters() != null) {
						if (papp.getParameters() instanceof IParams)
							prm = (IParams) papp.getParameters();
						else
							prm = SubstanceStudyParser.parseConditions(dx, papp.getParameters().toString());
					}
					prm = solarize(prm);

					if (papp.getEffects() != null)
						for (EffectRecord<String, Object, String> e : papp.getEffects()) {
							List<IParams> _childParams_ = new ArrayList<IParams>();

							if (prm.size() > 0) {
								prm.put("type_s", "params");
								prm.put("id", String.format("%s/%d/prm", papp.getDocumentUUID(), e.getIdresult()));
								_childParams_.add(prm);
							}
							if (summaryMeasurement != null)
								summarymeasurement2bucket(papp.getProtocol(), e, bucket);

							Bucket study = new Bucket();

							substance2Bucket(record, study, suffix);
							study.remove("id");
							protocolapplication2Bucket(papp, study, suffix);
							study.setHeader(study_headers_combined[0]);

							protocol2Bucket(papp.getProtocol(), study, suffix);
							reference2Bucket(papp, study, suffix);
							effectrecord2bucket(papp.getProtocol(), e, study, suffix);

							if (e.getConditions() != null) {
								IParams prmc = null;
								if (e.getConditions() instanceof IParams)
									prmc = (IParams) e.getConditions();
								else
									prmc = SubstanceStudyParser.parseConditions(dx, e.getConditions().toString());
								if (prmc.size() > 0) {
									prmc = solarize(prmc);
									prmc.put("type_s", "conditions");
									prmc.put("id", String.format("%s/%d/cn", papp.getDocumentUUID(), e.getIdresult()));
									_childParams_.add(prmc);
								}
							}

							if (papp.getDocumentUUID() != null)
								study.put("id", String.format("%s/%d", papp.getDocumentUUID(), e.getIdresult()));
							_childDocuments_.add(study);
							study.put("_childDocuments_", _childParams_);

						}

					else {

						List<IParams> _childParams_ = new ArrayList<IParams>();

						if (prm.size() > 0) {
							prm.put("type_s", "params");
							_childParams_.add(prm);
						}
						Bucket study = new Bucket();
						substance2Bucket(record, study, suffix);
						protocolapplication2Bucket(papp, study, suffix);
						study.setHeader(study_headers_combined[0]);

						protocol2Bucket(papp.getProtocol(), study, suffix);
						reference2Bucket(papp, study, suffix);
						_childDocuments_.add(study);
						study.put("_childDocuments_", _childParams_);
					}

				}
			}

			bucket.put("_childDocuments_", _childDocuments_);
			/*
			 * if (ids != null) for (Bucket id : ids) _childDocuments_.add(id);
			 */

		}
		}

		return bucket;
	}

	protected IParams solarize(IParams prm) {
		IParams newprm = new Params();
		for (Object key : prm.keySet()) {
			Object value = prm.get(key);
			if (value==null) continue;
			if (value instanceof Value) {
				Value v = (Value) value;
				Object unit = v.getUnits();
				value = v.getLoValue();
				newprm.put(String.format("%s%s_s", key.toString(),unit==null?"":("_"+unit)), value);
			} else
				newprm.put(key.toString() + "_s", value.toString());

		}
		return newprm;
	}
	protected void composition2Bucket(CompositionRelation component, Bucket bucket, Map<String, Integer> composition,
			boolean suffix) {
		String ctype = component.getRelationType().name().replace("HAS_", "");
		if (composition.get(ctype) == null)
			composition.put(ctype, new Integer(1));
		else
			composition.put(ctype, ((Integer) composition.get(ctype)) + 1);

		for (Property p : component.getSecondStructure().getRecordProperties()) {
			if (p.getLabel().indexOf("UUID") >= 0)
				continue;
			String label = String.format("%s.%s", p.getLabel().replace("http://www.opentox.org/api/1.1#", ""), ctype);
			String val = component.getSecondStructure().getRecordProperty(p).toString().trim();
			if ("".equals(val))
				continue;
			List<String> vals;
			if (bucket.get(label) == null) {
				vals = new ArrayList<String>();
				bucket.put(ns(label, suffix, "_s"), vals);
			} else
				vals = (List) bucket.get(label);

			if (vals.indexOf(val) < 0)
				vals.add(val);
		}
	}

	// TODO
	protected Bucket composition2Buckets(SubstanceRecord record, CompositionRelation component, boolean suffix) {

		Bucket bcomposition = new Bucket<>();
		bcomposition.setHeaders(study_headers_combined);
		bcomposition.put("type_s", "composition");

		String ctype = component.getRelationType().name().replace("HAS_", "");
		bcomposition.put("component", ctype);
		bcomposition.put("COMPOSITION", component.getName());
		if (component.getSecondStructure().getSmiles() != null)
			bcomposition.put("SMILES", component.getSecondStructure().getSmiles());

		if (component.getSecondStructure().getInchi() != null)
			bcomposition.put("InChI", component.getSecondStructure().getInchi());

		if (component.getSecondStructure().getInchiKey() != null)
			bcomposition.put("InChIKey", component.getSecondStructure().getInchiKey());

		for (Property p : component.getSecondStructure().getRecordProperties()) {
			if (p.getLabel().indexOf("UUID") >= 0)
				continue;
			String label = String.format("%s_s", p.getLabel().replace("http://www.opentox.org/api/1.1#", ""));
			String val = component.getSecondStructure().getRecordProperty(p).toString().trim();
			if ("".equals(val))
				continue;
			bcomposition.put(label, val);

		}
		return bcomposition;

	}

	protected void substance2Bucket(SubstanceRecord record, Bucket bucket, boolean suffix) {
		substance2Bucket(record, bucket, suffix, "_s");
	}

	protected void substance2Bucket(SubstanceRecord record, Bucket bucket, boolean hasSuffix, String suffix) {
		if (record == null)
			return;
		bucket.put(ns("name", hasSuffix, suffix), record.getSubstanceName());
		bucket.put(ns("publicname", hasSuffix, suffix), record.getPublicName());
		if (!"".equals(record.getOwnerName()))
			bucket.put(ns("owner_name", hasSuffix, suffix), record.getOwnerName());
		bucket.put(ns("s_uuid", hasSuffix, suffix), record.getSubstanceUUID());
		bucket.put(ns("substanceType", hasSuffix, suffix), record.getSubstancetype());
		bucket.put("type_s", "substance");
		bucket.put("id", record.getSubstanceUUID());

		bucket.put(ns("content", hasSuffix, suffix), record.getContent());
	}

	protected Bucket externalids2Bucket(ExternalIdentifier id) {
		Bucket ids = new Bucket();
		ids.setHeader(new String[] { "type_s", "ID", "VALUE" });
		ids.put("type_s", "externalid");
		ids.put("system", id.getSystemDesignator());
		ids.put("VALUE", id.getSystemIdentifier());
		return ids;
	}

	protected void protocolapplication2Bucket(ProtocolApplication<Protocol, Object, String, Object, String> papp,
			Bucket bucket, boolean suffix) {
		bucket.put(ns("document_uuid", suffix, "_s"), papp.getDocumentUUID());
		bucket.put("type_s", "study");
		if (papp.getInterpretationResult() != null && !"".equals(papp.getInterpretationResult()))
			bucket.put(ns("interpretation_result", suffix, "_s"), papp.getInterpretationResult().toUpperCase());
	}

	protected void reference2Bucket(ProtocolApplication<Protocol, Object, String, Object, String> papp, Bucket bucket,
			boolean suffix) {
		if (papp.getReferenceOwner() != null && !"".equals(papp.getReferenceOwner()))
			bucket.put(ns("reference_owner", suffix, "_s"), papp.getReferenceOwner().toUpperCase());
		bucket.put(ns("reference_year", suffix, "_i"), papp.getReferenceYear());
		bucket.put(ns("reference", suffix, "_s"), papp.getReference());
	}

	protected String ns(String key, boolean addSuffix, String suffix) {
		return key + (addSuffix ? suffix : "");
	}

	protected void protocol2Bucket(Protocol protocol, Bucket bucket, boolean suffix) {
		bucket.put(ns("topcategory", suffix, "_s"), protocol.getTopCategory());
		bucket.put(ns("endpointcategory", suffix, "_s"), protocol.getCategory());

		if ((protocol.getTopCategory() != null) && (protocol.getCategory() != null))
			bucket.put(ns("category", suffix, "_ancestor_path"),
					String.format("%s/%s", protocol.getTopCategory(), protocol.getCategory()));

		bucket.put(ns("endpoint", suffix, "_s"), protocol.getEndpoint().toUpperCase());
		if (protocol.getGuideline() != null && protocol.getGuideline().get(0) != null
				&& !"".equals(protocol.getGuideline().get(0)))
			bucket.put(ns("guidance", suffix, "_s"), protocol.getGuideline().get(0).toUpperCase());
	}

	protected void summarymeasurement2bucket(Protocol protocol, EffectRecord<String, Object, String> e, Bucket bucket) {
		if ((e != null) & (e.getEndpoint() != null) && e.getEndpoint().toUpperCase().indexOf(summaryMeasurement) >= 0) {

			String label = String.format("%s.%s.%s", protocol.getTopCategory(), protocol.getCategory(),
					summaryMeasurement);
			String val = String.format("%s%4.1f%s",
					(e.getLoQualifier() == null || "".equals(e.getLoQualifier())) ? "" : (e.getLoQualifier() + " "),
					e.getLoValue(), e.getUnit() == null ? "" : e.getUnit());
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

	protected void effectrecord2bucket(Protocol protocol, EffectRecord<String, Object, String> e, Bucket bucket,
			boolean suffix) {
		if (e.getEndpoint() != null)
			bucket.put(ns("effectendpoint", suffix, "_s"), e.getEndpoint().toUpperCase());
		bucket.put(ns("unit", suffix, "_s"), e.getUnit());
		if (!"".equals(e.getLoQualifier()))
			bucket.put(ns("loQualifier", suffix, "_s"), e.getLoQualifier());
		bucket.put(ns("loValue", suffix, "_d"), e.getLoValue());
		if (!"".equals(e.getUpQualifier()))
			bucket.put(ns("upQualifier", suffix, "_s"), e.getUpQualifier());
		bucket.put(ns("upValue", suffix, "_d"), e.getUpValue());
		if (!"".equals(e.getErrQualifier()))
			bucket.put(ns("errQualifier", suffix, "_s"), e.getErrQualifier());
		if (!"".equals(e.getErrorValue()))
			bucket.put(ns("err", suffix, "_d"), e.getErrorValue());

		final String catchall4search = "_text_";
		if (e.getTextValue() != null)
			if (e.getTextValue().toString().startsWith("{")) {
				IParams nonzero = SubstanceStudyParser.parseTextValueProteomics(dx, e.getTextValue().toString());
				bucket.put(catchall4search, nonzero);
			} else
				bucket.put(catchall4search, e.getTextValue());

	}

	protected void params2Bucket(ProtocolApplication<Protocol, Object, String, Object, String> papp, Bucket bucket,
			String key, String prefix) {
		if (papp.getParameters() instanceof IParams)
			iparams2bucket((IParams) papp.getParameters(), bucket, key, prefix);
		else {
			iparams2bucket(SubstanceStudyParser.parseConditions(dx, papp.getParameters().toString()), bucket, key,
					prefix);
		}

	}

	protected void condition2bucket(EffectRecord<String, Object, String> e, Bucket bucket, String key, String prefix) {
		if (e.getConditions() != null)
			if (e.getConditions() instanceof IParams)
				iparams2bucket((IParams) e.getConditions(), bucket, key, prefix);
			else {
				iparams2bucket(SubstanceStudyParser.parseConditions(dx, e.getConditions().toString()), bucket, key,
						prefix);
			}

	}

	protected void iparams2bucket(IParams params, Bucket bucket, String key, String prefix) {
		bucket.put(key, params);
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