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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
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
		String substanceURI = uriReporter.getURI(record);
		Resource substance = getOutput().createResource(substanceURI);
		if (record.getMeasurements() != null)
			for (ProtocolApplication<Protocol, String, String, IParams, String> pa : record
					.getMeasurements()) {
				String measuregroupURI = String.format("%s/measuregroup/%s",
						base, pa.getDocumentUUID());
				Resource measuregroup = getOutput().createResource(
						measuregroupURI);
				getOutput().add(substance,
						RDFTerms.BFO_0000056.getProperty(getOutput()),
						measuregroup);
				// interpretation result as as separate endpoint group
				if (pa.getInterpretationResult() != null) {
					String endpointURI = String.format("%s/interpretation/%s",
							base, pa.getDocumentUUID());
					Resource endpoint = getOutput().createResource(endpointURI);
					getOutput().add(
							endpoint,
							RDFTerms.has_value.getProperty(getOutput()),
							getOutput().createLiteral(
									pa.getInterpretationResult()));
					getOutput().add(measuregroup,
							RDFTerms.OBI_0000299.getProperty(getOutput()),
							endpoint);
					getOutput().add(endpoint,
							RDFTerms.IAO_0000136.getProperty(getOutput()),
							substanceURI);
				}
				// each effect as BAO endpoint
				if (pa.getEffects() != null)
					for (EffectRecord<String, IParams, String> effect : pa
							.getEffects()) {
						String endpointURI = String.format("%s/endpoint/ID%d",
								base, effect.getIdresult());
						Resource endpoint = getOutput().createResource(
								endpointURI);
						getOutput().add(measuregroup,
								RDFTerms.OBI_0000299.getProperty(getOutput()),
								endpoint);
						getOutput().add(endpoint,
								RDFTerms.IAO_0000136.getProperty(getOutput()),
								substanceURI);
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
							getOutput()
									.add(endpoint,
											RDFTerms.has_value
													.getProperty(getOutput()),
											getOutput().createTypedLiteral(
													effect.getLoValue()));

						if (effect.getUnit() != null)
							getOutput().add(endpoint,
									RDFTerms.has_unit.getProperty(getOutput()),
									effect.getUnit());

						if (effect.getTextValue() != null) {
							getOutput()
									.add(endpoint,
											RDFTerms.has_value
													.getProperty(getOutput()),
											getOutput().createTypedLiteral(
													effect.getTextValue()));
						}
					}
			}

		return record;
	}
}

enum RDFTerms {
	BFO_0000056 {
		@Override
		public String toString() {
			return "participates in at some time";
		}
	},
	OBI_0000299 {
		@Override
		public String toString() {
			return "has specified output";
		}
	},
	IAO_0000136 {
		@Override
		public String toString() {
			return "is about";
		}
	},
	has_value {
		@Override
		public String getTerm() {
			return "has-value";
		}

		@Override
		public String getNamespace() {
			return "http://semanticscience.org/resource/";
		}
	},
	has_unit {
		@Override
		public String getTerm() {
			return "has-unit";
		}

		@Override
		public String getNamespace() {
			return "http://semanticscience.org/resource/";
		}
	};
	public String getNamespace() {
		return "http://purl.obolibrary.org/obo/";
	}

	public String getTerm() {
		return name();
	}

	public String getURI() {
		return String.format("%s%s", getNamespace(), getTerm());
	}

	public boolean isProperty() {
		return true;
	};

	public Property getProperty(Model jenaModel) throws Exception {
		if (isProperty()) {
			Property p = jenaModel.getProperty(getURI());
			return p != null ? p : jenaModel.createProperty(getURI());
		} else
			throw new Exception("Expected property, found " + getTerm());
	};

	public Resource getResource(Model jenaModel) throws Exception {
		if (!isProperty()) {
			Resource p = jenaModel.getResource(getURI());
			return p != null ? p : jenaModel.createResource(getURI());
		} else
			throw new Exception("Expected resource, found " + getTerm());
	}
}