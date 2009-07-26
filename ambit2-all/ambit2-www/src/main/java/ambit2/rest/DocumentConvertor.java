package ambit2.rest;

import org.restlet.data.MediaType;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.w3c.dom.Document;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

public class DocumentConvertor<T,Q extends IQueryRetrieval<T>>  extends RepresentationConvertor<T,Q,Document> {
	public DocumentConvertor(QueryReporter<T, Q, Document> reporter) {
		super(reporter);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6126693410309179856L;
	
	public Representation process(Document doc) throws AmbitException {
			return new DomRepresentation(MediaType.TEXT_XML,doc);
	}

	@Override
	public Representation process(Q query) throws AmbitException {
		return process(reporter.process(query));
	};

}
