package ambit2.rest.property;

import org.restlet.data.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.base.data.Property;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryDOMReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.query.XMLTags;
import ambit2.rest.reference.ReferenceDOMReporter;

/**
 * Generates xml for {@link PropertyResource} resource
 * @author nina
 *
 * @param <Q>
 */
public class PropertyDOMReporter<Q extends IQueryRetrieval<Property>> extends QueryDOMReporter<Property, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7852309113522482578L;
	protected ReferenceDOMReporter referenceReporter;
	public PropertyDOMReporter(Reference ref) {
		super(ref);
		referenceReporter = new ReferenceDOMReporter(ref);
	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new PropertyURIReporter(reference);
	}

	@Override
	public void footer(Document output, Q query) {
	
	}

	@Override
	public void header(Document doc, Q query) {
		doc.appendChild(doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_featuredefs));
	}

	@Override
	public void processItem(Property item, Document doc) {
		   NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_opentox, XMLTags.node_featuredefs);
		   for (int i=0; i < parent.getLength();i++)
		        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {

		                parent.item(i).appendChild(getItemElement(doc,item));
		        		break;
		   }			
	}
	@Override
	public Element getItemElement(Document doc, Property item) {
		Element e = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_featuredef);
        e.setAttribute(XMLTags.attr_id,Integer.toString(item.getId()));
        e.setAttribute(XMLTags.attr_name,item.getName());
        e.setAttribute(XMLTags.attr_type,"TODO");
        e.appendChild(getURIElement(doc, item));
        e.appendChild(referenceReporter.getItemElement(doc, item.getReference()));
        return e;
	}
}
