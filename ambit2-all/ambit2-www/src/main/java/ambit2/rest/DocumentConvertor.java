package ambit2.rest;

import javax.xml.parsers.DocumentBuilderFactory;

import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.w3c.dom.Document;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

/**
 * Converts resource to DOM representation
 * @author nina
 *
 * @param <T>
 * @param <Q>
 * @param <R>
 */
public class DocumentConvertor<T,Q extends IQueryRetrieval<T>>  
										extends AbstractObjectConvertor<T,Q,Document>  {

	public DocumentConvertor(QueryReporter<T,Q,Document> reporter) {
		super(reporter,MediaType.APPLICATION_PDF);
		if (this.reporter != null) ((QueryReporter<T,Q,Document>)this.reporter).setMaxRecords(5000);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6126693410309179856L;
	
	protected Document createOutput(Q query) throws AmbitException {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	};
	public Representation process(Document doc) throws AmbitException {
			return new DomRepresentation(MediaType.TEXT_XML,doc);
	}

}
