package ambit2.rest.rdf;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * OpenTox algorithm types ontology http://www.opentox.org/algorithmTypes#
 * @author nina
 *
 */
public class OTA {
	public enum OTAClass {
		DescriptorCalculation,
		PatternMining,
		PharmacophoreGeneration,
		PhysicoChemical,
		QuantumChemical,
		SimilarityDistance,
		Topological,
		//Learning
		MSDMTox,
		Clustering,
		Learning,
		Classification,
		EagerLearning,
		LazyLearning,
		Regression,
		MultipleTargets,
		SingleTarget,
		//
		SemiSupervised,
		Supervised,
		Unsupervised,
		//
		Rules,
		//Data processing
		Preprocessing,
		DataCleanup,
		Discretization,
		FeatureSelection,
		Normalization,
		Utility,		
		//Misc
		Generation3D,
		SimilarityDistanceCalculation,
		Visualisation,		
		;
		public String getNS() {
			return String.format(_NS, toString());
		}
	
		public OntClass getOntClass(OntModel model) {
			OntClass c = model.getOntClass(getNS());
			return (c==null)?createOntClass(model):c;
		}
		public OntClass createOntClass(OntModel model) {
			return model.createClass(getNS());
		}	

	};
	//
	   public enum OTAProperty {
		   	hasInput,
		   	hasOutput;
		   	public Property createProperty(OntModel jenaModel) {
		   		Property p = jenaModel.getObjectProperty(String.format(_NS, toString()));
		   		return p!= null?p:
		   				jenaModel.createObjectProperty(String.format(_NS, toString()));
		   	}
   }	
	/** <p>The RDF model that holds the vocabulary terms</p> */
	private static Model m_model = ModelFactory.createDefaultModel();
	/** <p>The namespace of the vocabalary as a string ({@value})</p> */
	protected static final String _NS = "http://www.opentox.org/algorithmTypes.owl#%s";
	public static final String NS = String.format(_NS,"");
	
	public static String getURI() {return NS;}
	/** <p>The namespace of the vocabalary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
}
