package ambit2.rest.rdf.sparql;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Iterator;

import net.idea.restnet.rdf.ns.OT;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Abstract class to iterate over OpenTox objects in RDF model. 
 * Constructors, reading external sources (stream, representation, url) assume Jena -supported RDF mime types.
 * application/rdf+xml is the default.
 * @author nina
 *
 * @param <Item>
 */
public abstract class RDFSparqlIterator<Item> implements Iterator<Item>{
	protected Model jenaModel;
	protected Reference reference;
	protected Reference baseReference;
	public Reference getBaseReference() {
		return baseReference;
	}

	public void setBaseReference(Reference baseReference) {
		this.baseReference = baseReference;
	}

	protected ResultSet resultSet;
	protected QueryExecution qe = null;
	
	protected String recordIDVar = null;
	protected RDFNode recordNode = null;
	
	protected Item record;
	protected Item nextRecord;
	protected RDFNode nextRecordNode = null;
	
	/**
	 * OpenTox object iterator - reads restlet representation
	 * @param representation
	 * @param mediaType
	 * @param sparql
	 * @throws ResourceException
	 */
	public RDFSparqlIterator(Representation representation, MediaType mediaType, String sparql) throws ResourceException {
		this(OT.createModel(null,representation,mediaType),sparql);
	}
	/**
	 * OpenTox object iterator - reads RDF, assumes application/rdf+xml
	 * @param reference
	 * @param sparql
	 * @throws ResourceException
	 */
	
	public RDFSparqlIterator(Reference reference, String sparql) throws ResourceException ,MalformedURLException,IOException{
		this(OT.createModel(null,reference, MediaType.APPLICATION_RDF_XML),sparql);
		this.reference = reference;
	}
	/**
	 * OpenTox object iterator - reads RDF representation , given a reference ad mime type
	 * @param reference
	 * @param mediaType
	 * @param sparql
	 * @throws ResourceException
	 */
	public RDFSparqlIterator(Reference reference,MediaType mediaType, String sparql) throws ResourceException,MalformedURLException,IOException {
		this(OT.createModel(null,reference, mediaType),sparql);
		this.reference = reference;
	}
	/**
	 * OpenTox object iterator over input stream
	 * @param in
	 * @param mediaType
	 * @param sparql
	 * @throws ResourceException
	 */
	public RDFSparqlIterator(InputStream in,MediaType mediaType, String sparql) throws ResourceException {
		this(OT.createModel(null,in, mediaType),sparql);
	}	

	/**
	 * Allows to initialize the iterator over existing model
	 * @param model
	 * @param sparql
	 * @throws ResourceException
	 */
	public RDFSparqlIterator(Model model, String sparql) throws ResourceException {
		super();
		this.jenaModel = model;

		resultSet = query(sparql);
	}
	
	/**
	 * Executes sparql query; 
	 * Don't touch
	 * @param sparqlQuery
	 * @return
	 * @throws ResourceException
	 */
	private ResultSet query(String sparqlQuery) throws ResourceException {
		try {
			Query query = QueryFactory.create(sparqlQuery);
			qe = QueryExecutionFactory.create(query,jenaModel);
			return qe.execSelect();
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
	}
	public void close() {
		try { if (qe!=null) qe.close();} catch (Exception x) {}
	}
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	public Model getJenaModel() {
		return jenaModel;
	}
	/**
	 * Not supported
	 */
	public void remove() {

	}
	protected abstract Item parse(QuerySolution querySolution, Item record) ;

	
	/**
	 * This is considered record ID
	 * @param querySolution
	 * @return
	 */
	protected RDFNode getRecordURI(QuerySolution querySolution) {
		return querySolution.getResource(recordIDVar);
	}
	/**
	 * @param querySolution
	 * @return
	 */
	protected boolean isSameRecord(QuerySolution querySolution) {
		RDFNode node = getRecordURI(querySolution);
		if (recordNode == null) {
			return false;
		} else if (node.isURIResource() && recordNode.isURIResource() && 
		    ((Resource)node).getURI().equals(((Resource)recordNode).getURI())) {
			return true; 
		} else return false;
	}
	
	public boolean hasNext() {
		boolean found = nextRecord != null;
		record = nextRecord;
		recordNode = nextRecordNode;
		nextRecord = null;
		nextRecordNode = null;
		while (resultSet.hasNext()) {
			found = true;
			QuerySolution querySolution = resultSet.next();
			if ((record == null) || isSameRecord(querySolution)) {
				record = parse(querySolution,record);
				recordNode = getRecordURI(querySolution);				
			} else {
				nextRecord = parse(querySolution,null);
				nextRecordNode = getRecordURI(querySolution);				
				break;
			}
		} 
		return found;
	}

	public Item next() {
		return record;
	}
}
