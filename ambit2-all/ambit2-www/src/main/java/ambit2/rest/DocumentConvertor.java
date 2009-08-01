package ambit2.rest;

import org.restlet.data.MediaType;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.w3c.dom.Document;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.Reporter;
import ambit2.db.readers.IQueryRetrieval;

/**
 * Converts resource to DOM representation
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <R>
 */
public class DocumentConvertor<T,Q extends IQueryRetrieval<T>,R extends Reporter<Q,Document>>  
					extends RepresentationConvertor<T,Q,Document,R>  {
	public DocumentConvertor(R reporter) {
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
