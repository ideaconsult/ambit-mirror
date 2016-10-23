package ambit2.rest.substance;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Map.Entry;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.math.util.MultidimensionalCounter.Iterator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.Request;
import org.restlet.data.MediaType;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.IValue;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.substance.study.SubstanceStudyDetailsProcessor;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.structure.CompoundURIReporter;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryRDFReporter;

public class SubstanceRDFReporter<Q extends IQueryRetrieval<SubstanceRecord>>
		extends QueryRDFReporter<SubstanceRecord, Q> {
	private String base = "http://example.com";
	protected PropertyURIReporter propertyReporter;
	protected CompoundURIReporter compoundReporter;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4522513627076425922L;

	public SubstanceRDFReporter(Request request, MediaType mediaType) {
		super(request, mediaType, null);
		if (request != null) {
			base = request.getRootRef().toString();
			propertyReporter = new PropertyURIReporter(request);
			compoundReporter = new CompoundURIReporter(base);
		}
		init();
	}

	/*
	 * public SubstanceRDFReporter(String base, MediaType mediaType) {
	 * super(null, mediaType, null); this.base = base; init(); }
	 */
	protected void init() {

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

	@Override
	public void header(Model output, Q query) {
		super.header(output, query);
		output.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		output.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		output.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
		output.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
		output.setNsPrefix("obo", "http://purl.obolibrary.org/obo/");
		output.setNsPrefix("sso", "http://semanticscience.org/resource/");
		output.setNsPrefix("bao", "http://www.bioassayontology.org/bao#");
		output.setNsPrefix("enm", "http://purl.enanomapper.org/onto/");
		output.setNsPrefix("npo", "http://purl.bioontology.org/ontology/npo#");
		output.setNsPrefix("void", "http://rdfs.org/ns/void#");
		output.setNsPrefix("owl", OWL.NS);
		output.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
	}

	@Override
	protected QueryURIReporter<SubstanceRecord, IQueryRetrieval<SubstanceRecord>> createURIReporter(
			Request req, ResourceDoc doc) {
		return new SubstanceURIReporter<>(req);
	}

	@Override
	public Object processItem(SubstanceRecord record) throws Exception {
		HashFunction hf = Hashing.murmur3_32();

		Resource bioassayType = RDFTermsSubstance.BAO_0000015
				.getResource(getOutput());

		output.setNsPrefix("substance", base + "/substance/");
		String substanceURI = uriReporter.getURI(record);
		Resource substanceResource = getOutput().createResource(substanceURI);
		if (record.getPublicName() != null)
			getOutput().addLiteral(substanceResource, RDFS.label, getOutput().createLiteral(record.getPublicName()));
		if (record.getSubstanceName() != null)
			getOutput().addLiteral(substanceResource, RDFS.label, getOutput().createLiteral(record.getSubstanceName()));
		getOutput().add(substanceResource, RDF.type,
				RDFTermsSubstance.CHEBI_59999.getResource(getOutput()));

		//convert to proper triple
		String substanceType = record.getSubstancetype();
		if (substanceType != null) {
			if (substanceType.startsWith("NPO_")) {
				getOutput().add(
					substanceResource, DCTerms.type,
					getOutput().createResource("http://purl.bioontology.org/ontology/npo#" + substanceType)
				);
			} else if (substanceType.startsWith("ENM_")) {
				getOutput().add(
					substanceResource, DCTerms.type,
					getOutput().createResource("http://purl.enanomapper.org/onto/" + substanceType)
				);
			}
		}

		output.setNsPrefix("owner", base + "/owner/");
		String sownerURI = String.format("%s/owner/%s", base,
				record.getOwnerUUID());
		Resource sowner = getOutput().createResource(sownerURI);
		getOutput().add(substanceResource, DCTerms.source, sowner);
		getOutput().add(sowner, RDF.type, RDFTermsSubstance.VOID_DATASET.getResource(getOutput()));
		if (record.getOwnerName() != null)
			getOutput().add(sowner, DCTerms.title, record.getOwnerName());

		// plaseholder, change to property
		String TMP_NS = "http://TMP.URI";

		int componentNr = 0;
		if (record.getRelatedStructures() != null)
			for (CompositionRelation r : record.getRelatedStructures()) {
				IStructureRecord struct = r.getSecondStructure();

				Resource component = getOutput().createResource(
						compoundReporter.getURI(struct));
				if (struct.getIdstructure() == -1) {
					component = getOutput().createResource(
						substanceResource.getURI() + "-c" + componentNr
					);
					componentNr += 1;
				}
				switch (r.getRelationType()) {
				case HAS_COATING: {
					getOutput()
							.add(component,
									RDF.type,
									getOutput()
											.createResource(
													"http://purl.bioontology.org/ontology/npo#NPO_1367"));
					break;
				}
				case HAS_CORE: {
					getOutput()
							.add(component,
									RDF.type,
									getOutput()
											.createResource(
													"http://purl.bioontology.org/ontology/npo#NPO_1617"));
					break;
				}
				case HAS_FUNCTIONALISATION: {
					// todo, for now rely on fall back
				}
				case HAS_CONSTITUENT: {
					// this should work for normal substances also, not only for
					// nanomaterials
				}
				case HAS_ADDITIVE: {

				}
				case HAS_IMPURITY: {

				}
				default: { // default to fiat material entity
					getOutput()
							.add(component,
									RDF.type,
									getOutput()
											.createResource(
													"http://purl.bioontology.org/ontology/npo#NPO_1597"));
				}
				}
				// we'll have to be able to express the quantity of the
				// component
				// todo find out which property to use
				if (r.getRelation().getReal_lowervalue() != null)
					getOutput().add(
							component,
							getOutput().createProperty(
									String.format("%s/#PURITY", TMP_NS)),
							getOutput().createTypedLiteral(
									r.getRelation().getReal_lowervalue()));

				// Property parttype = getOutput().createProperty(
				// String.format("%s/#%s", TMP_NS, r.getRelationType()
				// .name()));
				Property parttype = getOutput().createProperty(
						"http://purl.bioontology.org/ontology/npo#has_part");
				getOutput().add(substanceResource, parttype, component);

				if (struct.getRecordProperties() != null)
					for (ambit2.base.data.Property p : struct
							.getRecordProperties()) {
						Object value = struct.getRecordProperty(p);
						Property feature = getOutput().createProperty(
								propertyReporter.getURI(p));
						if (value instanceof Number)
							getOutput().add(
									component,
									feature,
									getOutput().createTypedLiteral(
											((Number) value).floatValue()));
						else
							getOutput()
									.add(component,
											feature,
											getOutput().createLiteral(
													value.toString()));

					}
				if (struct.getInchi() != null) {
					Resource inchiRes = getOutput().createResource(
							component.getURI() + "_inchi");
					Resource inchiType = getOutput()
							.createResource(
									"http://semanticscience.org/resource/CHEMINF_000113");
					Property hasAttribute = getOutput()
							.createProperty(
									"http://semanticscience.org/resource/CHEMINF_000200");
					Property hasValue = getOutput().createProperty(
							"http://semanticscience.org/resource/SIO_000300");
					getOutput().add(component, hasAttribute, inchiRes);
					getOutput().add(inchiRes, RDF.type, inchiType);
					getOutput().add(inchiRes, hasValue, struct.getInchi());
				}
				if (struct.getSmiles() != null) {
					Resource smilesRes = getOutput().createResource(
							component.getURI() + "_smiles");
					Resource smilesType = getOutput()
							.createResource(
									"http://semanticscience.org/resource/CHEMINF_000018");
					Property hasAttribute = getOutput()
							.createProperty(
									"http://semanticscience.org/resource/CHEMINF_000200");
					Property hasValue = getOutput().createProperty(
							"http://semanticscience.org/resource/SIO_000300");
					getOutput().add(component, hasAttribute, smilesRes);
					getOutput().add(smilesRes, RDF.type, smilesType);
					getOutput().add(smilesRes, hasValue, struct.getSmiles());
				}

				// r.getRelation()
				// r.getRelationType().

			}

		if (record.getMeasurements() != null)
			for (ProtocolApplication<Protocol, String, String, IParams, String> pa : record
					.getMeasurements()) {
				/*
				 * assays - for now each protocol application as one assay,
				 * having one measure group. Later to consolidate assays on
				 * perhaps protocol + reference basis?
				 */

				output.setNsPrefix("as", base + "/assay/");
				String assayURI = String.format("%s/assay/%s", base,
						pa.getDocumentUUID());
				Resource assay = getOutput().createResource(assayURI);
				Protocol._categories assay_type = null;
				try {
					assay_type = Protocol._categories.valueOf(pa.getProtocol()
							.getCategory());
				} catch (Exception x) {
				}
				getOutput().add(assay, RDF.type, bioassayType);
				if (assay_type != null)
					getOutput().add(
							assay,
							RDF.type,
							getOutput().createResource(
									assay_type.getOntologyURI()));
				if (pa.getProtocol() != null
						&& pa.getProtocol().getEndpoint() != null)
					getOutput().add(assay, DC.title,
							pa.getProtocol().getEndpoint());
				if (pa.getProtocol() != null) {
					String guideline = null;
					if (pa.getProtocol().getGuideline() != null
							&& pa.getProtocol().getGuideline().size() > 0)

						guideline = pa.getProtocol().getGuideline().get(0);
					StringBuilder b = new StringBuilder();
					b.append(guideline == null ? "" : guideline);
					b.append(pa.getReference() == null ? "" : pa.getReference());

					Object params = pa.getParameters();
					if (params != null) {
						if (params instanceof String)
							b.append(params.toString());
						else {
							// todo
						}
					}

					HashCode hc = hf.newHasher()
							.putString(b.toString(), Charsets.UTF_8).hash();

					output.setNsPrefix("ap", base + "/protocol/");
					String protocolURI = String.format("%s/protocol/%s", base,
							hc.toString().toUpperCase());
					Resource protocol = getOutput().createResource(protocolURI);
					getOutput().add(
							protocol,
							RDF.type,
							RDFTermsSubstance.OBI_0000272
									.getResource(getOutput()));
					if (guideline != null)
						getOutput().add(protocol, DC.title, guideline);
					getOutput().add(
							assay,
							RDFTermsSubstance.BAO_0002846
									.getProperty(getOutput()), protocol);
				}

				/*
				 * this is not right, but just to have it for now the owner and
				 * the reference as source the owner of the assay, e.g. the
				 * company/lab
				 */
				if (pa.getReference() != null && !"".equals(pa.getReference())) {
					HashCode hc = hf.newHasher()
							.putString(pa.getReference(), Charsets.UTF_8)
							.hash();
					String referenceURI = String.format("%s/reference/%s",
							base, hc.toString().toUpperCase());
					output.setNsPrefix("ref", base + "/reference/");
					Resource reference = getOutput().createResource(
							referenceURI);
					String referenceStr = pa.getReference();
					if (referenceStr.startsWith("http://dx.doi.org/")) {
						getOutput().add(reference, OWL.sameAs, output.createResource(referenceStr));
						getOutput().add(reference, DC.title, referenceStr.substring(18));
					} else if (referenceStr.startsWith("http://doi.org/")) {
						getOutput().add(reference, OWL.sameAs, output.createResource(referenceStr));
						getOutput().add(reference, DC.title, referenceStr.substring(15));
					} else if (referenceStr.startsWith("https://dx.doi.org/")) {
						getOutput().add(reference, OWL.sameAs, output.createResource(referenceStr));
						getOutput().add(reference, DC.title, referenceStr.substring(19));
					} else if (referenceStr.startsWith("https://doi.org/")) {
						getOutput().add(reference, OWL.sameAs, output.createResource(referenceStr));
						getOutput().add(reference, DC.title, referenceStr.substring(16));
					} else {
						getOutput().add(reference, DC.title, referenceStr);
					}
					getOutput().add(assay, DCTerms.source, reference);

					if (pa.getReferenceOwner() != null
							&& !"".equals(pa.getReferenceOwner())) {
						String rownerURI = String.format("%s/owner/%s", base,
								pa.getReferenceOwner());
						Resource rowner = getOutput().createResource(rownerURI);
						getOutput().add(reference, DC.publisher, rowner);
					}
				}

				/*
				 * each protocol application as one measure group
				 */
				output.setNsPrefix("mgroup", base + "/measuregroup/");
				String measuregroupURI = String.format("%s/measuregroup/%s",
						base, pa.getDocumentUUID());
				Resource measuregroup = getOutput().createResource(
						measuregroupURI);

				getOutput().add(measuregroup, RDF.type,
						RDFTermsSubstance.BAO_0000040.getResource(getOutput()));
				getOutput().add(substanceResource,
						RDFTermsSubstance.BFO_0000056.getProperty(getOutput()),
						measuregroup);
				getOutput().add(assay,
						RDFTermsSubstance.BAO_0000209.getProperty(getOutput()),
						measuregroup);
				/*
				 * interpretation result as as separate endpoint group
				 */
				if (pa.getInterpretationResult() != null) {
					String endpointURI = String.format("%s/interpretation/%s",
							base, pa.getDocumentUUID());
					Resource endpoint = getOutput().createResource(endpointURI);
					getOutput().add(
							endpoint,
							RDFTermsSubstance.has_value
									.getProperty(getOutput()),
							getOutput().createLiteral(
									pa.getInterpretationResult()));
					getOutput().add(
							measuregroup,
							RDFTermsSubstance.OBI_0000299
									.getProperty(getOutput()), endpoint);
					getOutput().add(
							endpoint,
							RDFTermsSubstance.IAO_0000136
									.getProperty(getOutput()),
							substanceResource);
				}
				/*
				 * each effect as BAO endpoint
				 */
				if (pa.getEffects() != null)
					output.setNsPrefix("ep", base + "/endpoint/");
					for (EffectRecord<String, IParams, String> effect : pa
							.getEffects()) {
						String endpointURI = String.format("%s/endpoint/ID%d",
								base, effect.getIdresult());
						if (effect.getIdresult() == -1) {
							endpointURI = String.format("%s/endpoint/ID%s",
									base, UUID.nameUUIDFromBytes(
											(substanceResource.getURI() + pa.getDocumentUUID() + effect.hashCode()).getBytes()
									).toString()
							);
						}
						Resource endpoint = getOutput().createResource(
								endpointURI);
						getOutput().add(
								endpoint,
								RDF.type,
								RDFTermsSubstance.BAO_0000179
										.getResource(getOutput()));
						getOutput().add(
								measuregroup,
								RDFTermsSubstance.OBI_0000299
										.getProperty(getOutput()), endpoint);
						getOutput().add(
								endpoint,
								RDFTermsSubstance.IAO_0000136
										.getProperty(getOutput()),
								substanceResource);
						if (effect.getEndpoint() != null)
							getOutput().add(endpoint, RDFS.label,
									effect.getEndpoint());
						/**
						 * Expand some properties to make the RDF more useful.
						 */
						someValuesAreMoreEqual(effect, endpoint, pa, substanceResource, measuregroup);
						/**
						 * TODO
						 * 
						 * <pre>
						 * getOutput().add(endpoint, RDF.type, endpoint type as per BAO, e.g. AC50);
						 * </pre>
						 */
						if (effect.getLoValue() != null && effect.getUpValue() != null) {
							Property statoProp = getOutput().createProperty(
								"http://purl.obolibrary.org/obo/STATO_0000035"
							);
							getOutput().add(
									endpoint,
									statoProp,
									getOutput().createTypedLiteral(
											effect.getLoValue() + "-" +
											effect.getUpValue()
									)
							);
						} else if (effect.getLoValue() != null ) {
							getOutput().add(
									endpoint,
									RDFTermsSubstance.has_value
											.getProperty(getOutput()),
									getOutput().createTypedLiteral(
											effect.getLoValue()));
						}

						if (effect.getUnit() != null)
							getOutput().add(
									endpoint,
									RDFTermsSubstance.has_unit
											.getProperty(getOutput()),
									effect.getUnit());

						if (effect.getTextValue() != null
								&& !"".equals(effect.getTextValue())) {
							getOutput().add(
									endpoint,
									RDFTermsSubstance.has_value
											.getProperty(getOutput()),
									getOutput().createTypedLiteral(
											effect.getTextValue()));
						}
						IParams<?> conditions = effect.getConditions();
						int conditionCounter = 0;
						if (conditions != null && conditions.size() > 0) {
							for (Entry<String,?> condition : conditions.entrySet()) {
								if (condition.getValue() != null) {
									conditionCounter++;
									String conditionURI = endpoint.getURI() + "C" + conditionCounter;
									Resource conditionRes = getOutput().createResource(conditionURI);
									getOutput().add(conditionRes, RDFS.label, condition.getKey());
									if (condition.getValue() instanceof IValue) {
										IValue<?,String,String> value = (IValue)condition.getValue();
										System.out.println("Value: " + value.toString());
										output.setNsPrefix("amb", "http://purl.enanomapper.net/");
										getOutput().add(
											endpoint,
											getOutput().createProperty("http://purl.enanomapper.net/has-condition"),
											conditionRes
										);
										if (value.getLoValue() != null) {
											getOutput().add(
												conditionRes,
												RDFTermsSubstance.has_value.getProperty(getOutput()),
											    value.getLoValue().toString()
											);
										}
										if (value.getUnits() != null) {
											getOutput().add(
												conditionRes,
												RDFTermsSubstance.has_unit.getProperty(getOutput()),
												value.getUnits()
											);
										}
									} else if (condition.getValue() instanceof IParams) {
										for (Object paramsObj : ((IParams)condition.getValue()).entrySet()) {
											Entry params = (Entry)paramsObj;
											if ("loValue".equals(params.getKey())) {
												getOutput().add(
													conditionRes,
													RDFTermsSubstance.has_value.getProperty(getOutput()),
													params.getValue().toString()
												);
											} else if ("unit".equals(params.getKey())) {
												getOutput().add(
													conditionRes,
													RDFTermsSubstance.has_unit.getProperty(getOutput()),
													params.getValue().toString()
												);
											} else {
												// System.out.println("Condition: " + params.getKey() + " -> " + params.getValue());
											}
										}
										
									} else {
										System.out.println("Unknown value type: " + condition.getValue().getClass().getName());
									}
								} else {
									// System.out.println("Incomplete conditions: " + condition);
								}
							}
						}
					}
			}

		final Property skosCloseMatch = getOutput().createProperty("http://www.w3.org/2004/02/skos/core#closeMatch");
		final Property skosRelatedMatch = getOutput().createProperty("http://www.w3.org/2004/02/skos/core#relatedMatch");
		if (record.getExternalids() != null)
			for (ExternalIdentifier extID : record.getExternalids()) {
				if ("Same as".equals(extID.getSystemDesignator())) {
					Resource sameResource = getOutput().createResource(extID.getSystemIdentifier());
					getOutput().add(
						substanceResource, OWL.sameAs, sameResource
					);
				} else if ("Close match".equals(extID.getSystemDesignator())) {
					output.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
					Resource sameResource = getOutput().createResource(extID.getSystemIdentifier());
					getOutput().add(
						substanceResource, skosCloseMatch, sameResource
					);
				} else if ("Related match".equals(extID.getSystemDesignator())) {
					output.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
					Resource relatedResource = getOutput().createResource(extID.getSystemIdentifier());
					getOutput().add(
						substanceResource, skosRelatedMatch, relatedResource
					);
				} else if ("See also".equals(extID.getSystemDesignator())) {
					Resource seeAlsoResource = getOutput().createResource(extID.getSystemIdentifier());
					getOutput().add(
						substanceResource, RDFS.seeAlso, seeAlsoResource
					);
				} else if ("HOMEPAGE".equals(extID.getSystemDesignator())) {
					Resource homepage = getOutput().createResource(extID.getSystemIdentifier());
					getOutput().add(
						substanceResource, FOAF.page, homepage
					);
				}
			}

		return record;
	}

	private void someValuesAreMoreEqual(EffectRecord<String, IParams, String> effect,
			Resource endpoint, ProtocolApplication<Protocol, String, String, IParams, String> pa,
			Resource substanceResource, Resource measuregroup) throws Exception {
		if (effect.getEndpoint() == null) return;
		String endPointLabel = effect.getEndpoint();
		if ("Spectral counts".equals(endPointLabel)) { // this is a property used for protein coronas
			output.setNsPrefix("uniprot", "http://rdf.uniprot.org/");
			String derivURI = endpoint.getURI() + "D";
			Resource deriv = getOutput().createResource(derivURI);
			getOutput().add(deriv, RDF.type, RDFTermsSubstance.BAO_0000179.getResource(getOutput()));
			getOutput().add(measuregroup, RDFTermsSubstance.OBI_0000299.getProperty(getOutput()), deriv);
			getOutput().add(deriv, RDFTermsSubstance.IAO_0000136.getProperty(getOutput()), substanceResource);
			getOutput().add(deriv, RDFS.label, "Protein binding");
			
			ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;

            try {
            	String json = effect.getTextValue().toString();
            	root = mapper.readTree(new ByteArrayInputStream(json.getBytes()));
            	java.util.Iterator<Entry<String,JsonNode>> entries = root.getFields();
            	while (entries.hasNext()) {
            		Entry<String,JsonNode> entry = entries.next();
            		String loValue = entry.getValue().toString(); 
            		if (loValue.contains(":1")) {
            			getOutput().add(
    						deriv,
    						RDFTermsSubstance.has_value
    								.getProperty(getOutput()),
    						getOutput().createResource("http://rdf.uniprot.org/" + entry.getKey())
    					);
            		}
            	}
             } catch (Exception x) {
            	throw x;
            }
		}
	}
}
