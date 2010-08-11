package ambit2.rest.rdf;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.DC;

public class Annotea {
	protected static final String _NS_BOOKMARK = "http://www.w3.org/2002/01/bookmark#%s";
	protected static final String _NS_ANNOTEA = "http://www.w3.org/2000/10/annotation-ns#";
	
	public enum Bookmark {
		Bookmark
		;

		public String getNS() {
			return String.format(_NS_BOOKMARK, toString());
		}		
		public OntClass getOntClass(OntModel model) {
			OntClass c = null;
			try {
				c = model.getOntClass(getNS());
			} catch (Exception x) { c = null; x.printStackTrace();};
			return (c==null)?createOntClass(model):c;
		}
		public OntClass createOntClass(OntModel model) {
			return model.createClass(getNS());
		}		
		public Property createProperty(OntModel model) {
			return model.createProperty(getNS());
		}			

	};
	
	 public enum BookmarkProperty {
		   	recalls,
		    hasTopic,
		    ;

		   	public Property createProperty(OntModel jenaModel) {
		   		Property p = jenaModel.getObjectProperty(String.format(_NS_BOOKMARK, toString()));
		   		return p!= null?p:
		   				jenaModel.createObjectProperty(String.format(_NS_BOOKMARK, toString()));
		   	}
      }
	 
		
	 public enum AnnoteaProperty {
		   	created
		    ;

		   	public Property createProperty(OntModel jenaModel) {
		   		Property p = jenaModel.getObjectProperty(String.format(_NS_ANNOTEA, toString()));
		   		return p!= null?p:
		   				jenaModel.createObjectProperty(String.format(_NS_ANNOTEA, toString()));
		   	}
      }	 
	 
	    public static OntModel createModel() throws Exception {
	    	return createModel(OntModelSpec.OWL_DL_MEM);
	    }    
		public static OntModel createModel(OntModelSpec spec) throws Exception {
			OntModel jenaModel = ModelFactory.createOntologyModel( spec,null);
			jenaModel.setNsPrefix( "a", _NS_ANNOTEA );
			jenaModel.setNsPrefix( "b", _NS_BOOKMARK );
			
			jenaModel.setNsPrefix( "dc", DC.NS );
			return jenaModel;
		}   	 
}
