package ambit2.rest;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.query.XMLTags;

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
		uriReporter = createURIReporter(req); 
	}
	protected abstract QueryURIReporter createURIReporter(Request req);
	@Override
	public Document getOutput() throws AmbitException {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			return doc;
		} catch (ParserConfigurationException x) {
			throw new AmbitException(x);
		}
	}
	public void open() throws DbAmbitException {
	}	
	public Element getURIElement(Document doc, T item) {
		Element e_uri = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_link);
		e_uri.setAttribute(XMLTags.attr_href,uriReporter.getURI(item));
        return e_uri;
	}		
	public abstract Element getItemElement(Document doc, T item);

	
}
