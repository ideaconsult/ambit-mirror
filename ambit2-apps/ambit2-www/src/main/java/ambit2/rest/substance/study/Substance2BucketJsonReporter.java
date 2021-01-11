package ambit2.rest.substance.study;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

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
import ambit2.base.ro.SubstanceRecordAnnotationProcessor;
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
	protected String dbTag = "ENM";
	protected boolean searchfriendly = true;
	public static final SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

	public enum _JSON_MODE {
		experiment, substance
	}

	protected SubstanceRecordAnnotationProcessor annotator;

	public SubstanceRecordAnnotationProcessor getAnnotator() {
		return annotator;
	}

	public void setAnnotator(SubstanceRecordAnnotationProcessor annotator) {
		this.annotator = annotator;
	}

	public Substance2BucketJsonReporter(String command, ProcessorsChain chain, _JSON_MODE jsonmode,
			String summaryMeasurement, String dbTag, SubstanceRecordAnnotationProcessor annotator) {
		super(command, null, null);
		setAnnotator(annotator);
		this.summaryMeasurement = summaryMeasurement;
		this.jsonmode = jsonmode;
		this.dbTag = dbTag;
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
			{ "name", "publicname", "owner_name", "s_uuid", "substanceType", "_childDocuments_", "type_s", "nmcode_hs",
					"substance_annotation_hss", "substance_annotation_ss", "nmcode_s", "ChemicalName.CONSTITUENT",
					"ChemicalName.ADDITIVE", "ChemicalName.IMPURITY", "ChemicalName.CORE", "ChemicalName.COATING",
					"ChemicalName.FUNCTIONALISATION", "ChemicalName.DOPING", "content_hss",

					"investigation_uuid", "assay_uuid",

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
			{ "id", "document_uuid", "investigation_uuid", "assay_uuid", "type_s", "topcategory", "endpointcategory",
					"guidance", "guidance_synonym", "E.method_synonym", "E.cell_type_synonym", "endpoint",
					"effectendpoint", "effectendpoint_type", "effectendpoint_synonym", "effectendpoint_group",
					"reference_owner", "reference_year", "reference", "loQualifier", "loValue", "upQualifier",
					"upValue", "err", "errQualifier", "conditions", "params", "textValue", "interpretation_result",
					"unit", "category", "idresult", "updated", "r_value", "r_purposeFlag", "r_studyResultType" },
			{ "P-CHEM.PC_GRANULOMETRY_SECTION.SIZE" }

	};
	private static final String header_summary_results = "SUMMARY.RESULTS_hss";
	private static final String header_summary_refs = "SUMMARY.REFS_hss";
	private static final String header_summary_refowner = "SUMMARY.REFOWNERS_hss";
	private static final String header_dbtag = "dbtag_hss";

	private static final String header_reliability = "reliability_s";
	private static final String header_studyResultType = "studyResultType_s";
	private static final String header_purposeFlag = "purposeFlag_s";

	private static final String header_component = "component_s";

	private static String FIELD_METHOD = "E.method_s";
	private static String FIELD_METHOD_SYNONYM = "E.method_synonym_ss";
	private static String FIELD_CELLTYPE = "E.cell_type_s";
	private static String FIELD_CELLTYPE_SYNONYM = "E.cell_type_synonym_ss";

	private final String[][] study_headers_combined = new String[][] { { "id", "name_s", "publicname_s", "owner_name_s",
			"content_hss", header_dbtag, "substanceType_s", "s_uuid_s", "name_hs", "publicname_hs", "owner_name_hs",
			"substanceType_hs", "s_uuid_hs", "_childDocuments_", "type_s", header_component, "ChemicalName_s",
			"TradeName_s", "CASRN_s", "EINECS_s", "IUCLID5_UUID_s", "COMPOSITION_s", "SMILES_s","InChIKey_s","InChI_s", "document_uuid_s",
			"investigation_uuid_s", "assay_uuid_s", "topcategory_s", "endpointcategory_s", "guidance_s",
			"guidance_synonym_ss", FIELD_METHOD_SYNONYM, FIELD_CELLTYPE_SYNONYM, "endpoint_s", "effectendpoint_s",
			"effectendpoint_type_s", "effectendpoint_synonym_ss", "effectendpoint_group_d", "reference_owner_s",
			"reference_year_s", "reference_s", "loQualifier_s", "loValue_d", "upQualifier_s", "upValue_d", "err_d",
			"errQualifier_s", "conditions_s", "effectid_hs", "params", "textValue_s", "interpretation_result_s",
			"unit_s", "category_s", "idresult", "nmcode_hs", "nmcode_s", "substance_annotation_hss",
			"substance_annotation_ss", "updated_s", FIELD_METHOD, FIELD_CELLTYPE, header_reliability,
			header_studyResultType, header_purposeFlag, header_summary_results, header_summary_refs,
			header_summary_refowner, "" } };

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
			String nmcode = null;

			List<Bucket> ids = new ArrayList<Bucket>();
			if (record.getExternalids() != null) {
				List<String> xids = new ArrayList<String>();

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
						xids.add(String.format("http://www.crystallography.net/cod/%s.html", id.getSystemIdentifier()));
					} else if (id.getSystemIdentifier().startsWith("http"))
						xids.add(id.getSystemIdentifier());
					else if ("NM code".equals(id.getSystemDesignator())) {
						nmcode = id.getSystemIdentifier();
						bucket.put("nmcode_s", nmcode);
					}
					// ids.add(externalids2Bucket(id));
				}
				bucket.put("content_hss", xids);
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

							effectrecord2bucket(papp, e, effect, false);

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
			bucket.getHeader()[study_headers_combined.length - 1] = getSummaryLabel();
			substance2Bucket(record, bucket, true, "_hs");

			List<Bucket> _childDocuments_ = new ArrayList<>();

			if (record.getRelatedStructures() != null) {

				for (int r = 0; r < record.getRelatedStructures().size(); r++) {
					CompositionRelation rel = record.getRelatedStructures().get(r);
					Bucket bcomposition = composition2Buckets(record, rel, suffix);
					bcomposition.put("id", String.format("%s/c/%d", record.getSubstanceUUID(), (r + 1)));
					bcomposition.put("s_uuid_hs", record.getSubstanceUUID());
					_childDocuments_.add(bcomposition);
				}
			}

			String nmcode = null;
			Bucket bucket_ids = new Bucket();
			List<String> xids = new ArrayList<String>();
			if (record.getExternalids() != null) {

				Set<String> keys = new TreeSet<String>();
				keys.add("type_s");
				keys.add("id");
				keys.add("s_uuid_hs");
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
						xids.add(String.format("http://www.crystallography.net/cod/%s.html", id.getSystemIdentifier()));
					} else if (id.getSystemIdentifier().startsWith("http"))
						xids.add(id.getSystemIdentifier());
					else if ("NM code".equals(id.getSystemDesignator())) {
						nmcode = id.getSystemIdentifier();
					} else {
						String key = ns(id.getSystemDesignator(), true, "_s");
						bucket_ids.put(key, id.getSystemIdentifier());
						keys.add(key);
					}

					// ids.add();
				}

				if (bucket_ids.size() > 0) {
					bucket_ids.setHeaders(new String[][] { keys.toArray(new String[keys.size()]) });
					bucket_ids.put("id", String.format("%s/id", record.getSubstanceUUID()));
					bucket_ids.put("s_uuid_hs", record.getSubstanceUUID());
					bucket_ids.put("type_s", "identifier");
					_childDocuments_.add(bucket_ids);
				}

			}
			if (record.getContent() != null)
				xids.add(record.getContent());
			if (xids != null && xids.size() > 0)
				bucket.put("content_hss", xids);

			List<String> summary_papp = null;
			List<String> summary_refs = null;
			List<String> summary_owners = null;

			if (record.getMeasurements() != null) {
				summary_papp = new ArrayList<String>();
				summary_refs = new ArrayList<String>();
				for (ProtocolApplication<Protocol, Object, String, Object, String> papp : record.getMeasurements()) {

					String label = String.format("%s.%s", papp.getProtocol().getTopCategory(),
							papp.getProtocol().getCategory());
					gatherSummary(bucket, header_summary_results, label, summary_papp);
					gatherSummary(bucket, header_summary_refs, papp.getReference(), summary_refs);
					gatherSummary(bucket, header_summary_refowner, papp.getReferenceOwner(), summary_owners);

					IParams prm = null;
					if (papp.getParameters() != null) {
						if (papp.getParameters() instanceof IParams)
							prm = (IParams) papp.getParameters();
						else
							prm = SubstanceStudyParser.parseConditions(dx, papp.getParameters().toString());
					}
					prm = solarize(prm);
					Object cell = prm.get(FIELD_CELLTYPE);
					Object method = prm.get(FIELD_METHOD);

					if (papp.getEffects() != null)
						for (EffectRecord<String, Object, String> e : papp.getEffects()) {
							String effectid = String.format("%s/%d",
									papp.getDocumentUUID() == null ? "P" : papp.getDocumentUUID(), e.getIdresult());
							List<IParams> _childParams_ = new ArrayList<IParams>();

							if (prm.size() > 0) {
								prm.put("type_s", "params");
								prm.put("id", String.format("%s/%d/prm", papp.getDocumentUUID(), e.getIdresult()));
								prm.put("document_uuid_s", papp.getDocumentUUID());
								if (searchfriendly) {
									prm.put("topcategory_s", papp.getProtocol().getTopCategory());
									prm.put("endpointcategory_s", papp.getProtocol().getCategory());
									if (papp.getProtocol().getGuideline() != null
											&& !papp.getProtocol().getGuideline().isEmpty()) {
										try {
											prm.put("guidance_s",
													papp.getProtocol().getGuideline().get(0).toUpperCase());
										} catch (Exception x) {
										}

									} else
										prm.put("guidance_s", "Not specified");

								}
								_childParams_.add(prm);
							}
							if (summaryMeasurement != null)
								summarymeasurement2bucket(papp.getProtocol(), e, bucket);

							Bucket study = new Bucket();

							substance2Bucket(record, study, suffix);
							study.remove("id");
							protocolapplication2Bucket(papp, study, suffix);
							if (cell != null)
								study.put(FIELD_CELLTYPE, cell.toString());

							String[] terms_method = null;
							if (method != null) {
								study.put(FIELD_METHOD, method.toString());
								terms_method = annotator.annotateParam(method.toString());
								if (terms_method != null)
									study.put(FIELD_METHOD_SYNONYM, Arrays.asList(terms_method));
							}

							study.setHeader(study_headers_combined[0]);

							protocol2Bucket(papp.getProtocol(), study, suffix);
							reference2Bucket(papp, study, suffix);
							effectrecord2bucket(papp, e, study, suffix);

							if (e.getConditions() != null) {
								IParams prmc = null;
								if (e.getConditions() instanceof IParams)
									prmc = (IParams) e.getConditions();
								else
									prmc = SubstanceStudyParser.parseConditions(dx, e.getConditions().toString());
								if (prmc.size() > 0) {
									prmc = solarize(prmc);
									if (prmc.size() > 0) {
										prmc.put("type_s", "conditions");
										prmc.put("id",
												String.format("%s/%d/cn", papp.getDocumentUUID(), e.getIdresult()));
										prmc.put("document_uuid_s", papp.getDocumentUUID());
										prmc.put("effectid_hs", effectid);
										if (searchfriendly) {
											prmc.put("topcategory_s", papp.getProtocol().getTopCategory());
											prmc.put("endpointcategory_s", papp.getProtocol().getCategory());
											if (papp.getProtocol().getGuideline() != null
													&& !papp.getProtocol().getGuideline().isEmpty())
												prmc.put("guidance_s",
														papp.getProtocol().getGuideline().get(0).toUpperCase());
											if (cell != null)
												prmc.put(FIELD_CELLTYPE, cell.toString());
											if (method != null) {
												prmc.put(FIELD_METHOD, method.toString());
											}
										}
										_childParams_.add(prmc);
									}
								}
							}

							if (papp.getDocumentUUID() != null)
								study.put("id", effectid);
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
						if (cell != null)
							study.put(FIELD_CELLTYPE, cell.toString());
						if (method != null)
							study.put(FIELD_METHOD, method.toString());
						study.setHeader(study_headers_combined[0]);

						protocol2Bucket(papp.getProtocol(), study, suffix);
						reference2Bucket(papp, study, suffix);
						_childDocuments_.add(study);
						study.put("_childDocuments_", _childParams_);
					}

				}
			}

			bucket.put("_childDocuments_", _childDocuments_);
			bucket.put(header_dbtag, dbTag);
			/*
			 * if (ids != null) for (Bucket id : ids) _childDocuments_.add(id);
			 */
			/*
			 * if (summary_papp != null) { Iterator<String> keys =
			 * summary_papp.keySet().iterator(); while (keys.hasNext()) { String key =
			 * keys.next(); bucket.put(String.format("RESULTS.%s_hs", key),
			 * summary_papp.get(key)); } } if (summary_refs != null) { Iterator<String> keys
			 * = summary_refs.keySet().iterator(); while (keys.hasNext()) { String key =
			 * keys.next(); bucket.put(String.format("REFS.%s_hs", key),
			 * summary_refs.get(key)); } }
			 */
		}
		}

		return bucket;
	}

	protected void gatherSummary(Bucket bucket, String key, String value, List<String> values) {
		if (value == null)
			return;
		if (values == null) {
			values = new ArrayList<String>();
			values.add(value);
		} else {
			if (!values.contains(value))
				values.add(value);
		}
		bucket.put(key, values);
	}

	protected IParams solarize(IParams prm) {
		IParams newprm = new Params();
		if (prm != null)
			for (Object key : prm.keySet()) {
				Object value = prm.get(key);
				if (value == null)
					continue;
				if (value instanceof Value) {
					Value v = (Value) value;
					Object unit = v.getUnits();
					if (unit != null && !"".equals(unit.toString().trim())) {
						newprm.put(String.format("%s_UNIT_s", key.toString()), unit.toString());
					}
					if (v.getLoValue() != null && v.getUpValue() != null) {
						newprm.put(String.format("%s_LOVALUE_d", key.toString()), v.getLoValue());
						if (v.getLoQualifier() != null && !"".equals(v.getLoQualifier().trim()))
							newprm.put(String.format("%s_LOQUALIFIER_s", key.toString()), v.getLoQualifier());
						newprm.put(String.format("%s_UPVALUE_d", key.toString()), v.getUpValue());
						if (v.getUpQualifier() != null && !"".equals(v.getUpQualifier().trim()))
							newprm.put(String.format("%s_UPQUALIFIER_s", key.toString()), v.getUpQualifier());
					} else if (v.getLoValue() != null) {
						newprm.put(String.format("%s_d", key.toString()), v.getLoValue());
					}
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
		bcomposition.put(header_component, ctype);
		bcomposition.put("COMPOSITION", component.getName());
		if (component.getSecondStructure().getSmiles() != null)
			bcomposition.put("SMILES_s", component.getSecondStructure().getSmiles());

		if (component.getSecondStructure().getInchi() != null)
			bcomposition.put("InChI_s", component.getSecondStructure().getInchi());

		if (component.getSecondStructure().getInchiKey() != null)
			bcomposition.put("InChIKey_s", component.getSecondStructure().getInchiKey());

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

		if (record.getExternalids() != null) {
			for (ExternalIdentifier id : record.getExternalids())
				if ("NM code".equals(id.getSystemDesignator())) {
					bucket.put(ns("nmcode", hasSuffix, suffix), id.getSystemIdentifier());
				}
		}

		String[] terms_substance = annotator.annotateSubstance(record);
		if (terms_substance != null) {
			String key = ns("substance_annotation", hasSuffix, suffix + "s");
			bucket.put(key, Arrays.asList(terms_substance));
		}

		// bucket.put(ns("content", hasSuffix, suffix), record.getContent());
	}

	protected void protocolapplication2Bucket(ProtocolApplication<Protocol, Object, String, Object, String> papp,
			Bucket bucket, boolean suffix) {
		bucket.put(ns("document_uuid", suffix, "_s"), papp.getDocumentUUID());
		if (papp.getInvestigationUUID() != null)
			bucket.put(ns("investigation_uuid", suffix, "_s"), papp.getInvestigationUUID());
		if (papp.getAssayUUID() != null)
			bucket.put(ns("assay_uuid", suffix, "_s"), papp.getAssayUUID());
		bucket.put("type_s", "study");
		if (papp.getUpdated() != null)
			bucket.put("updated_s", dateformatter.format(papp.getUpdated()));
		if (papp.getReliability() != null) {
			if (papp.getReliability().getValue() != null)
				bucket.put(header_reliability, papp.getReliability().getValue());
			if (papp.getReliability().getStudyResultType() != null)
				bucket.put(header_studyResultType, papp.getReliability().getStudyResultType());
			if (papp.getReliability().getPurposeFlag() != null)
				bucket.put(header_purposeFlag, papp.getReliability().getPurposeFlag());
		}
		if (papp.getInterpretationResult() != null && !"".equals(papp.getInterpretationResult()))
			bucket.put(ns("interpretation_result", suffix, "_s"), papp.getInterpretationResult().toUpperCase());
	}

	protected void reference2Bucket(ProtocolApplication<Protocol, Object, String, Object, String> papp, Bucket bucket,
			boolean suffix) {
		if (papp.getReferenceOwner() != null && !"".equals(papp.getReferenceOwner()))
			bucket.put(ns("reference_owner", suffix, "_s"), papp.getReferenceOwner().toUpperCase());
		bucket.put(ns("reference_year", suffix, "_s"), papp.getReferenceYear());
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

		if (protocol.getEndpoint() != null)
			bucket.put(ns("endpoint", suffix, "_s"), protocol.getEndpoint().toUpperCase());
		if (protocol.getGuideline() != null && protocol.getGuideline().get(0) != null
				&& !"".equals(protocol.getGuideline().get(0))) {
			bucket.put(ns("guidance", suffix, "_s"), protocol.getGuideline().get(0).toUpperCase());
		}
		annotateGuidance(protocol, bucket, suffix);
	}

	protected void annotateGuidance(Protocol protocol, Bucket bucket, boolean suffix) {
		String[] terms = null;
		if (protocol.getGuideline() != null && protocol.getGuideline().get(0) != null
				&& !"".equals(protocol.getGuideline().get(0))) {
			bucket.put(ns("guidance", suffix, "_s"), protocol.getGuideline().get(0).toUpperCase());
			terms = annotator.annotateGuideline(protocol.getGuideline().get(0));
		}
		if (protocol.getEndpoint() != null) {
			String[] terms_endpoint = annotator.annotateGuideline(protocol.getEndpoint());
			if (terms == null)
				terms = terms_endpoint;
			else if (terms_endpoint != null)
				terms = ArrayUtils.addAll(terms, terms_endpoint);

		}
		if (terms != null)
			bucket.put(ns("guidance_synonym", suffix, "_ss"), Arrays.asList(terms));

	}

	protected void summarymeasurement2bucket(Protocol protocol, EffectRecord<String, Object, String> e, Bucket bucket) {
		if ((e != null) & (e.getEndpoint() != null) && e.getEndpoint().toUpperCase().indexOf(summaryMeasurement) >= 0) {

			// String label = String.format("SUMMARY.%s.%s.%s_s",
			// protocol.getTopCategory(),
			// protocol.getCategory(),e.getEndpoint());
			String label = getSummaryLabel();
			String val = String.format("%s%4.1f %s",
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

	protected void effectrecord2bucket(ProtocolApplication papp, EffectRecord<String, Object, String> e, Bucket bucket,
			boolean suffix) {

		if (papp.getInvestigationUUID() != null)
			bucket.put(ns("investigation_uuid", suffix, "_s"), papp.getInvestigationUUID());
		if (papp.getAssayUUID() != null)
			bucket.put(ns("assay_uuid", suffix, "_s"), papp.getAssayUUID());

		if (e.getEndpoint() != null)
			bucket.put(ns("effectendpoint", suffix, "_s"), e.getEndpoint().toUpperCase());

		String[] terms = annotator.annotateEndpoint(e.getEndpoint());
		if (terms != null) {
			bucket.put(ns("effectendpoint_synonym", suffix, "_ss"), Arrays.asList(terms));
		}

		if (e.getEndpointType() != null)
			bucket.put(ns("effectendpoint_type", suffix, "_s"), e.getEndpointType().toUpperCase());
		if (e.getEndpointGroup() != null)
			bucket.put(ns("effectendpoint_group", suffix, "_s"), e.getEndpointGroup());
		bucket.put(ns("unit", suffix, "_s"), e.getUnit() == null ? "" : e.getUnit());

		if (e.getLoValue() != null || e.getUpValue() != null) {
			if (e.getLoQualifier() != null && !"".equals(e.getLoQualifier()))
				bucket.put(ns("loQualifier", suffix, "_s"), e.getLoQualifier());

			if (e.getLoValue() != null)
				bucket.put(ns("loValue", suffix, "_d"), e.getLoValue());
			if (e.getUpQualifier() != null && !"".equals(e.getUpQualifier()))
				bucket.put(ns("upQualifier", suffix, "_s"), e.getUpQualifier());
			if (e.getUpValue() != null)
				bucket.put(ns("upValue", suffix, "_d"), e.getUpValue());
			if (e.getErrQualifier() != null && !"".equals(e.getErrQualifier()))
				bucket.put(ns("errQualifier", suffix, "_s"), e.getErrQualifier());
			if (e.getErrorValue() != null && !"".equals(e.getErrorValue()))
				bucket.put(ns("err", suffix, "_d"), e.getErrorValue());
		}

		final String catchall4search = "_text_";
		if (e.getTextValue() != null) {
			if (e.getTextValue().toString().startsWith("{")) {
				IParams nonzero = SubstanceStudyParser.parseTextValueProteomics(dx, e.getTextValue().toString());
				bucket.put(catchall4search, nonzero);
			} else {
				bucket.put(ns("textValue", suffix, "_s"), e.getTextValue());
			}
		}

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

	protected void externalids2Bucket(ExternalIdentifier id, Bucket bucket, String key, String prefix) {
		TreeSet<String> keys = new TreeSet<String>();
		IParams ids = (IParams) bucket.get(key);
		if (ids == null) {
			ids = new Params();
			bucket.put(key, ids);
			keys.add(key);
		}
		ids.put(id.getSystemDesignator(), id.getSystemIdentifier());

	}

	/**
	 * 
	 * @return
	 */
	protected String getSummaryLabel() {

		return String.format("SUMMARY.%s_hss", summaryMeasurement);
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