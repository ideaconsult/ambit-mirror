package ambit2.rest.rdf;

import java.io.IOException;
import java.io.InputStream;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;

public class OT {
	public enum OTClass {
		Compound,
		Conformer,
		Dataset,
		DataEntry,
		Feature,
		NumericFeature {
			@Override
			public OntClass createOntClass(OntModel model) {
				OntClass feature = Feature.getOntClass(model);
				OntClass stringFeature = model.createClass(getNS());
				feature.addSubClass(stringFeature);
				return stringFeature;
			}			
		},
		NominalFeature {
			@Override
			public OntClass createOntClass(OntModel model) {
				OntClass feature = Feature.getOntClass(model);
				OntClass stringFeature = model.createClass(getNS());
				feature.addSubClass(stringFeature);
				return stringFeature;
			}
		},
		StringFeature {
			@Override
			public OntClass createOntClass(OntModel model) {
				OntClass feature = Feature.getOntClass(model);
				OntClass stringFeature = model.createClass(getNS());
				feature.addSubClass(stringFeature);
				return stringFeature;
			}
			
		},
		TupleFeature {
			@Override
			public OntClass createOntClass(OntModel model) {
				OntClass feature = Feature.getOntClass(model);
				OntClass tupleFeature = model.createClass(getNS());
				feature.addSubClass(tupleFeature);
				return tupleFeature;
			}
			
		},		
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
		    validationTestDataset,
		    //Nominal features
		    acceptValue;
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
		jenaModel.setNsPrefix( "ota", OTA.NS );
		jenaModel.setNsPrefix( "otee", OTEE.NS );
		jenaModel.setNsPrefix( "owl", OWL.NS );
		jenaModel.setNsPrefix( "dc", DC.NS );
		jenaModel.setNsPrefix( "bx", BibTex.NS );
		jenaModel.setNsPrefix( "xsd", XSDDatatype.XSD+"#" );
		return jenaModel;
	}

    public static OntModel createModel(OntModel base,InputStream in, MediaType mediaType) throws ResourceException {
    	OntModel model = base==null?createModel(OntModelSpec.OWL_DL_MEM):base;
    	RDFReader reader = model.getReader();
    	/**
    	 * When reading RDF/XML the check for reuse of rdf:ID has a memory overhead,
    	 * which can be significant for very large files. 
    	 * In this case, this check can be suppressed by telling ARP to ignore this error. 
    	 */
    	reader.setProperty("WARN_REDEFINITION_OF_ID","EM_IGNORE");

    	model.enterCriticalSection(Lock.WRITE) ;
    	try {
    		reader.setProperty("error-mode", "lax" );
    		reader.setProperty("WARN_REDEFINITION_OF_ID","EM_IGNORE");

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
    		model.leaveCriticalSection() ;

    	}
    	
    }
    public static OntModel createModel(OntModel model,Representation entity,MediaType mediaType) throws ResourceException {
    	try {
    		return createModel(model,entity.getStream(),entity.getMediaType()==null?entity.getMediaType():entity.getMediaType());
    	} catch (IOException x) {
    		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
    	}
    }	    
    public static OntModel createModel(OntModel model,Reference uri, MediaType mediaType) throws ResourceException {
    	//Client httpclient = new Client(Protocol.HTTP);
    	//httpclient.setConnectTimeout(300000);
    	ClientResource client = new ClientResource(uri);
    	//System.out.println("Connecting "+uri);
		//client.setNext(httpclient);
		client.setFollowingRedirects(true);
		Representation r = client.get(mediaType);
		try {
			if (Status.SUCCESS_OK.equals(client.getStatus()))  {
				if ((r==null) || !r.isAvailable())
					throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
				return createModel(model,r,mediaType);		
			} else throw new ResourceException(client.getStatus());
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		} finally {
			try {r.release();} catch (Exception x) {}
		}
		
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
