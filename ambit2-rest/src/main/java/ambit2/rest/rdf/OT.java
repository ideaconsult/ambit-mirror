package ambit2.rest.rdf;

import java.io.IOException;
import java.io.InputStream;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.arp.impl.DefaultErrorHandler;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;

public class OT {
	public enum OTClass {
		Compound,
		Conformer,
		Dataset,
		DataEntry,
		Feature,
		FeatureValue,
		Algorithm,
		Model,
		Parameter,
		Validation,
		ValidationInfo,
		Task;
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
	/** <p>The RDF model that holds the vocabulary terms</p> */
	private static Model m_model = ModelFactory.createDefaultModel();
	/** <p>The namespace of the vocabalary as a string ({@value})</p> */
	protected static final String _NS = "http://www.opentox.org/api/1.1#%s";
	public static final String NS = String.format(_NS,"");
	
	public static String getURI() {return NS;}
	/** <p>The namespace of the vocabalary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );

    /**
     * Object properties
     */
    public enum OTProperty {
		   	dataEntry,
		    compound ,
		    feature ,
		    values ,
		    hasSource,
		    conformer ,
		    isA ,
		    model ,
		    parameters ,
		    report ,
		    algorithm ,
		    dependentVariables ,
		    independentVariables ,
		    predictedVariables ,
		    trainingDataset ,
		    validationReport ,
		    validation ,
		    hasValidationInfo,
		    validationModel ,
		    validationPredictionDataset ,
		    validationTestDataset;
		   	public Property createProperty(OntModel jenaModel) {
		   		Property p = jenaModel.getObjectProperty(String.format(_NS, toString()));
		   		return p!= null?p:
		   				jenaModel.createObjectProperty(String.format(_NS, toString()));
		   	}
    }
    /**
     * Data properties
     */
    public enum DataProperty {
    	value,
    	units,
    	has3Dstructure,
    	hasStatus,
    	percentageCompleted,
    	paramScope,
    	paramValue;
	   	public Property createProperty(OntModel jenaModel) {
	   		Property p = jenaModel.getDatatypeProperty(String.format(_NS, toString()));
	   		return p!= null?p:
	   				jenaModel.createDatatypeProperty(String.format(_NS, toString()));
	   	}
    };
    /*
    public static final Property value = m_model.createProperty(String.format(_NS, "value"));
    public static final Property units = m_model.createProperty(String.format(_NS, "units"));
    public static final Property has3Dstructure = m_model.createProperty(String.format(_NS, "has3Dstructure"));
    public static final Property hasStatus = m_model.createProperty(String.format(_NS, "hasStatus"));
    public static final Property percentageCompleted = m_model.createProperty(String.format(_NS, "percentageCompleted"));
    public static final Property paramScope = m_model.createProperty(String.format(_NS, "paramScope"));
    public static final Property paramValue = m_model.createProperty(String.format(_NS, "paramValue"));
	*/
    public static OntModel createModel() throws Exception {
    	return createModel(OntModelSpec.OWL_DL_MEM);
    }
	public static OntModel createModel(OntModelSpec spec) throws ResourceException {
		OntModel jenaModel = ModelFactory.createOntologyModel( spec,null);
		jenaModel.setNsPrefix( "ot", OT.NS );
		jenaModel.setNsPrefix( "owl", OWL.NS );
		jenaModel.setNsPrefix( "dc", DC.NS );
		return jenaModel;
	}

    public static OntModel createModel(InputStream in, MediaType mediaType) throws ResourceException {
    	OntModel model = createModel(OntModelSpec.OWL_DL_MEM);
    	RDFReader reader = model.getReader();
    	try {
    		reader.setProperty("error-mode", "strict" );
    		reader.setErrorHandler(new RDFDefaultErrorHandler() {
    			@Override
    			public void warning(Exception e) {
    				super.warning(e);
    				
    			}
    		});
    		reader.read(model,in,getJenaFormat(mediaType));
    		return model;
    	} catch (Exception x) {
    		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
    	} finally {
    		
    	}
    	
    }
    public static OntModel createModel(Representation entity,MediaType mediaType) throws ResourceException {
    	try {
    		return createModel(entity.getStream(),entity.getMediaType()==null?entity.getMediaType():entity.getMediaType());
    	} catch (IOException x) {
    		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
    	}
    }	    
    public static OntModel createModel(Reference uri, MediaType mediaType) throws ResourceException {
		ClientResource client = new ClientResource(uri);
		client.setFollowRedirects(true);
		Representation r = client.get(mediaType);
		if (Status.SUCCESS_OK.equals(client.getStatus()))  {
			if ((r==null) || !r.isAvailable())
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			return createModel(r,mediaType);		
		} else throw new ResourceException(client.getStatus());
    }
	public static String getJenaFormat(MediaType mediaType) throws ResourceException {
		if (mediaType.equals(MediaType.APPLICATION_RDF_XML))
			return "RDF/XML";
			//return "RDF/XML-ABBREV";	
		else if (mediaType.equals(MediaType.APPLICATION_RDF_TURTLE))
			return "TURTLE";
		else if (mediaType.equals(MediaType.TEXT_RDF_N3))
			return "N3";
		else if (mediaType.equals(MediaType.TEXT_RDF_NTRIPLES))
			return "N-TRIPLE";		
		else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
						String.format("Unsupported format %s",mediaType));
	}
	

}
