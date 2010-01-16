package ambit2.rest;

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.rdf.OT;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * Jena model convertor
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <R>
 */
public class RDFJenaConvertor<T,Q extends IQueryRetrieval<T>>  extends AbstractObjectConvertor<T,Q,OntModel>  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6566828643076743577L;

	
	public RDFJenaConvertor(QueryReporter<T,Q,OntModel> reporter) {
		this(reporter,MediaType.APPLICATION_RDF_XML);
		if (this.reporter != null) ((QueryReporter<T,Q,OntModel>)this.reporter).setMaxRecords(5000);
	}
	public RDFJenaConvertor(QueryReporter<T,Q,OntModel> reporter,MediaType media) {
		super(reporter,media);
	}

	@Override
	protected OntModel createOutput(Q query) throws AmbitException {
		try {
			return OT.createModel();
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}

	@Override
	public Representation process(final OntModel jenaModel) throws AmbitException {
		 return new OutputRepresentation(mediaType) {
	            @Override
	            public void write(OutputStream output) throws IOException {
	            	try {
	        			if (mediaType.equals(MediaType.APPLICATION_RDF_XML))
	        				jenaModel.write(output,"RDF/XML");
	        				//getJenaModel().write(output,"RDF/XML-ABBREV");	
	        			else if (mediaType.equals(MediaType.APPLICATION_RDF_TURTLE))
	        				jenaModel.write(output,"TURTLE");
	        			else if (mediaType.equals(MediaType.TEXT_RDF_N3))
	        				jenaModel.write(output,"N3");
	        			else if (mediaType.equals(MediaType.TEXT_RDF_NTRIPLES))
	        				jenaModel.write(output,"N-TRIPLE");	
	        			else 
	        				jenaModel.write(output,"RDF/XML-ABBREV");	

	            	} catch (Exception x) {
	            		Throwable ex = x;
	            		while (ex!=null) {
	            			if (ex instanceof IOException) 
	            				throw (IOException)ex;
	            			ex = ex.getCause();
	            		}
	            		Context.getCurrentLogger().warning(x.getMessage()==null?x.toString():x.getMessage());
	            	} finally {

	            		try {if (output !=null) output.flush(); } catch (Exception x) { x.printStackTrace();}
	            	}
	            }
	        };				

	}

}
