package ambit2.rest;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class BO {
	/** <p>The RDF model that holds the vocabulary terms</p> */
	private static Model m_model = ModelFactory.createDefaultModel();
	/** <p>The namespace of the vocabalary as a string ({@value})</p> */
	public static final String _NS = "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#%s";
	public static final String NS = String.format(_NS,"");
	
	public static String getURI() {return NS;}
	/** <p>The namespace of the vocabalary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );

	public enum BOClass {
		Algorithm,
		Descriptor,
		AtomicDescriptor,
		BondDescriptor,
		ProteinDescriptor,
		MolecularDescriptor,
		DataFeature,
		DataRepresentation,
		DescriptorValue,
		Implementation,
		Reference,
		Category,
		Contributor;
		
		public String getNS() {
			return String.format(_NS, toString());
		}
		public OntClass getOntClass(OntModel model) {
			return model.getOntClass(getNS());
		}
		public OntClass createOntClass(OntModel model) {
			return model.createClass(getNS());
		}		
		public Property createProperty(OntModel model) {
			return model.createProperty(getNS());
		}			

	};
    
    /**
     * Object properties
     */
    public static final Property isClassifiedAs = m_model.createProperty(String.format(_NS, "isClassifiedAs"));
    public static final Property instanceOf = m_model.createProperty(String.format(_NS, "instanceOf"));

}
