package ambit2.rest;

import java.util.logging.Logger;

import org.restlet.Request;
import org.restlet.data.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.query.XMLTags;
import ambit2.rest.reference.AbstractDOMParser;

/**
 * Parent class for all DOM reporters
 * @author nina
 *
 * @param <T>
 * @param <Q>
 */
public abstract class QueryDOMReporter<T,Q extends IQueryRetrieval<T>> extends QueryReporter<T, Q, Document> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2357730001828019569L;
	protected QueryURIReporter<T, IQueryRetrieval<T>> uriReporter;
	public QueryDOMReporter(Request req) {
		super();
		uriReporter = createURIReporter(req); 
	}
	protected abstract QueryURIReporter createURIReporter(Request req);

	public void open() throws DbAmbitException {
	}	
	public Element getURIElement(Document doc, T item) {
		Element e_uri = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_link);
		e_uri.setAttribute(XMLTags.attr_href,uriReporter.getURI(item));
        return e_uri;
	}		
	public abstract Element getItemElement(Document doc, T item);
	
	public void readDeep(Reference uri,AbstractDOMParser<T> parser) throws AmbitException {
		Logger.getLogger(getClass().getName()).info(uri.toString());
		RESTClient<T, Q,AbstractDOMParser<T>> client = new RESTClient<T, Q,AbstractDOMParser<T>>(parser,this);
		client.process(uri);
	}		

}
