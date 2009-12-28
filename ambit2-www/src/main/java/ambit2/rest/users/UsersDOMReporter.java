package ambit2.rest.users;

import org.restlet.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.base.data.AmbitUser;
import ambit2.base.data.ILiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.QueryUser;
import ambit2.rest.QueryDOMReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.query.XMLTags;
import ambit2.rest.reference.ReferenceURIReporter;

/**
 * AmbitUser to XML
 * @author nina
 *
 */
public class UsersDOMReporter extends QueryDOMReporter<AmbitUser, QueryUser> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4039338676682812945L;

	public UsersDOMReporter(Request reference) {
		super(reference);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference) {
		return new ReferenceURIReporter<IQueryRetrieval<ILiteratureEntry>>(reference);
	}

	@Override
	public void footer(Document output, QueryUser query) {
		
	}

	@Override
	public void header(Document doc, QueryUser query) {

		doc.appendChild(doc.createElementNS(XMLTags.ns_opentox_user,XMLTags.node_users));
	}

	@Override
	public Object processItem(AmbitUser user) throws AmbitException {
		   NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_opentox_user, XMLTags.node_users);
		   for (int i=0; i < parent.getLength();i++)
		        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {
		                parent.item(i).appendChild(getItemElement(output,user));
		        		break;
		   }		
		   return null;

		
	}
	@Override
	public Element getItemElement(Document doc, AmbitUser item) {
		Element e = doc.createElementNS(XMLTags.ns_opentox_user,XMLTags.node_user);
        e.setAttribute(XMLTags.attr_id,item.getName());
        e.setAttribute(XMLTags.attr_name,item.getLastName());
        return e;
	}

}