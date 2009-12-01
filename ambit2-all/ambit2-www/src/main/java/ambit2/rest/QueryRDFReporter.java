package ambit2.rest;

import java.io.OutputStream;

import org.restlet.data.MediaType;
import org.restlet.data.Request;

import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Parent class for RDF reporters
 * @author nina
 *
 * @param <T>
 * @param <Q>
 */
public abstract class QueryRDFReporter<T,Q extends IQueryRetrieval<T>> extends QueryReporter<T, Q, OutputStream> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1160842325900158717L;
	protected QueryURIReporter<T, IQueryRetrieval<T>> uriReporter;
	protected abstract QueryURIReporter<T, IQueryRetrieval<T>> createURIReporter(Request req);
	protected OntModel jenaModel;
	protected MediaType mediaType;
	
	
	public QueryRDFReporter(Request request,MediaType mediaType) {
		super();
		uriReporter = createURIReporter(request);
		this.mediaType = mediaType;
	}
	public OntModel getJenaModel() {
		return jenaModel;
	}
	public void setJenaModel(OntModel jenaModel) {
		this.jenaModel = jenaModel;
	}
	
	public OntModel createModel() throws Exception {
		OntModel jenaModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM,null);
		jenaModel.setNsPrefix( "ot", OT.NS );
		jenaModel.setNsPrefix( "owl", OWL.NS );
		jenaModel.setNsPrefix( "dc", DC.NS );
		return jenaModel;
	}
	public void footer(OutputStream output, Q query) {
		if (mediaType.equals(MediaType.APPLICATION_RDF_XML))
			//getJenaModel().write(output,"RDF/XML");
			getJenaModel().write(output,"RDF/XML-ABBREV");	
		else if (mediaType.equals(MediaType.APPLICATION_RDF_TURTLE))
			getJenaModel().write(output,"TURTLE");
		else if (mediaType.equals(MediaType.TEXT_RDF_N3))
			getJenaModel().write(output,"N3");
		else if (mediaType.equals(MediaType.TEXT_RDF_NTRIPLES))
			getJenaModel().write(output,"N-TRIPLE");	
		else 
			getJenaModel().write(output,"RDF/XML-ABBREV");	
	};
	public void header(OutputStream output, Q query) {
		try {
			setJenaModel(jenaModel==null?createModel():jenaModel);
		} catch (Exception x) {
			x.printStackTrace();
		}
	};
}
