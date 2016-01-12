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

public class SubstanceRDFReporter<Q extends IQueryRetrieval<SubstanceRecord>>
		extends QueryRDFReporter<SubstanceRecord, Q> {
	private String base = "http://example.com";
	/**
	 * 
	 */
	private static final long serialVersionUID = -4522513627076425922L;

	public SubstanceRDFReporter(Request request, MediaType mediaType) {
		super(request, mediaType, null);
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
				if (pa.getEffects() != null)
					for (EffectRecord<String, IParams, String> effect : pa
							.getEffects()) {
						String endpointURI = String.format("%s/endpoint/%s",
								base, effect.getSampleID());
						Resource endpoint = getOutput().createResource(
								endpointURI);
						getOutput().add(measuregroup,
								RDFTerms.OBI_0000299.getProperty(getOutput()),
								endpoint);
						getOutput().add(endpoint,
								RDFTerms.IAO_0000136.getProperty(getOutput()),
								substanceURI);
						if (effect.getLoValue() != null)
							getOutput()
									.add(endpoint,
											RDFTerms.has_value
													.getProperty(getOutput()),
											String.format("%e",
													effect.getLoValue()));
						if (effect.getUnit() != null)
							getOutput().add(endpoint,
									RDFTerms.has_unit.getProperty(getOutput()),
									effect.getUnit());
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
		return String.format("%s/%s", getNamespace(), getTerm());
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