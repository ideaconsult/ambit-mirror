package ambit2.rest.substance;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryRDFReporter;

import org.restlet.Request;
import org.restlet.data.MediaType;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.db.substance.study.SubstanceStudyDetailsProcessor;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SubstanceRDFReporter<Q extends IQueryRetrieval<SubstanceRecord>>
		extends QueryRDFReporter<SubstanceRecord, Q> {
	private String base = "http://example.com";
	/**
	 * 
	 */
	private static final long serialVersionUID = -4522513627076425922L;

	public SubstanceRDFReporter(Request request, MediaType mediaType) {
		super(request, mediaType, null);
		if (request != null)
			base = request.getRootRef().toString();
		init();
	}

	public SubstanceRDFReporter(String base, MediaType mediaType) {
		super(null, mediaType, null);
		this.base = base;
		init();
	}
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
		output.setNsPrefix("obo", "http://purl.obolibrary.org/obo/");
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

		String substanceURI = uriReporter.getURI(record);
		Resource substanceResource = getOutput().createResource(substanceURI);
		getOutput().add(substanceResource, RDF.type,
				RDFTermsSubstance.CHEBI_59999.getResource(getOutput()));

		String sownerURI = String.format("%s/owner/%s", base,
				record.getOwnerUUID());
		Resource sowner = getOutput().createResource(sownerURI);
		getOutput().add(substanceResource, DCTerms.source, sowner);
		if (record.getOwnerName() != null)
			getOutput().add(sowner, DCTerms.title, record.getOwnerName());

		if (record.getMeasurements() != null)
			for (ProtocolApplication<Protocol, String, String, IParams, String> pa : record
					.getMeasurements()) {
				/*
				 * assays - for now each protocol application as one assay,
				 * having one measure group. Later to consolidate assays on
				 * perhaps protocol + reference basis?
				 */

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
					if (params!=null) {
						if (params instanceof String)
							b.append(params.toString());
						else {
							//todo
						}
					}
					
		

					HashCode hc = hf.newHasher()
							.putString(b.toString(), Charsets.UTF_8).hash();

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
					Resource reference = getOutput().createResource(
							referenceURI);
					getOutput().add(reference, DC.title, pa.getReference());
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
					for (EffectRecord<String, IParams, String> effect : pa
							.getEffects()) {
						String endpointURI = String.format("%s/endpoint/ID%d",
								base, effect.getIdresult());
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
						 * TODO
						 * 
						 * <pre>
						 * getOutput().add(endpoint, RDF.type, endpoint type as per BAO, e.g. AC50);
						 * </pre>
						 */
						if (effect.getLoValue() != null)
							getOutput().add(
									endpoint,
									RDFTermsSubstance.has_value
											.getProperty(getOutput()),
									getOutput().createTypedLiteral(
											effect.getLoValue()));

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
					}
			}

		return record;
	}
}
