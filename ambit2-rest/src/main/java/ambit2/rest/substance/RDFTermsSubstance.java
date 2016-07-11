package ambit2.rest.substance;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public enum RDFTermsSubstance {

	CHEBI_59999 {
		@Override
		public String toString() {
			return "substance";
		}

		public String getNamespace() {
			return "http://purl.obolibrary.org/obo/";
		}

		@Override
		public boolean isProperty() {
			return false;
		}
	},
	/**
	 * VoID terms
	 */
	VOID_DATASET {
		@Override
		public String toString() {
			return "dataset";
		}
		@Override
		public boolean isProperty() {
			return false;
		}
		@Override
		public String getNamespace() {
			return "http://rdfs.org/ns/void#";
		}
		@Override
		public String getURI() {
			return getNamespace()+"Dataset";
		}
	},
	/**
	 * assay
	 */
	BAO_0000015 {
		@Override
		public String toString() {
			return "assay";
		}

		@Override
		public boolean isProperty() {
			return false;
		}
	},
	BAO_0000040 {
		@Override
		public String toString() {
			return "measure group";
		}

		@Override
		public boolean isProperty() {
			return false;
		}
	},
	/**
	 * protocol
	 */
	OBI_0000272 {
		@Override
		public String toString() {
			return "protocol";
		}

		@Override
		public boolean isProperty() {
			return false;
		}
	},
	/**
	 * endpoint
	 */
	BAO_0000179 {
		@Override
		public String toString() {
			return "result";
		}
		@Override
		public boolean isProperty() {
			return false;
		}
		@Override
		public String getNamespace() {
			return "http://www.bioassayontology.org/bao#";
		}
	},
	
	/**
	 * Property. has assay protocol
	 */
	BAO_0002846 {
		@Override
		public String toString() {
			return "has assay protocol";
		}

	},
	/**
	 * Property. participates in at some time
	 */
	BFO_0000056 {
		@Override
		public String toString() {
			return "participates in at some time";
		}
	},
	/**
	 * Property. has specified output
	 */
	OBI_0000299 {
		@Override
		public String toString() {
			return "has specified output";
		}
	},
	/**
	 * Property. is about
	 */
	IAO_0000136 {
		@Override
		public String toString() {
			return "is about";
		}
	},
	/**
	 * Property. has participant at some time
	 */
	BFO_0000057 {
		// todo use for conditions and parameters , e.g. proteins
		@Override
		public String toString() {
			return "has participant at some time";
		}
	},
	/**
	 * Property. has measure group
	 */
	BAO_0000209 {
		@Override
		public String toString() {
			return "has measure group";
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