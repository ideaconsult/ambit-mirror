package ambit2.rest.property;

import org.restlet.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.property.AbstractPropertyRetrieval;
import ambit2.rest.QueryDOMReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.query.XMLTags;
import ambit2.rest.reference.ReferenceDOMReporter;

/**
 * Generates xml for {@link PropertyResource} resource. http://opentox.org/wiki/opentox/Feature
 * @author nina
 * 
 <pre>
 <?xml version="1.0" encoding="UTF-8" ?> 
 <schema xmlns="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.opentox.org/FeatureDefinition/1.0" xmlns:tns="http://www.opentox.org/FeatureDefinition/1.0" elementFormDefault="qualified">
 <complexType name="FeatureDefinition">
  <attribute name="ID" type="string" use="required" /> 
  <attribute name="Name" type="string" use="required" /> 
  <attribute name="Reference" type="string" use="required" /> 
  <attribute name="Type" type="string" use="required" /> 
  <attribute name="DataType" type="string" use="required" /> 
  </complexType>
  </schema>
</pre>
 * @param <Q>
 */
public class PropertyDOMReporter<Q extends IQueryRetrieval<Property>> extends QueryDOMReporter<Property, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7852309113522482578L;
	protected ReferenceDOMReporter referenceReporter;
	public PropertyDOMReporter(Request ref) {
		super(ref);
		referenceReporter = new ReferenceDOMReporter(ref);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request reference) {
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
	public Object processItem(Property item) throws AmbitException {
		if (item ==null) return null;
		   NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_opentox, XMLTags.node_featuredefs);
		   for (int i=0; i < parent.getLength();i++)
		        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {

		                parent.item(i).appendChild(getItemElement(output,item));
		        		break;
		   }		
		   return null;
	}
	@Override
	public Element getItemElement(Document doc, Property item) {
		Element e = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_featuredef);
        e.setAttribute(XMLTags.attr_id,Integer.toString(item.getId()));
        e.setAttribute(XMLTags.attr_name,item.getName());
        e.setAttribute(XMLTags.attr_type,item.getLabel());
        e.setAttribute(XMLTags.attr_nominal,Boolean.toString(item.isNominal()));
        e.setAttribute(XMLTags.attr_datatype,
        		item.getClazz()==Number.class?
        				AbstractPropertyRetrieval._PROPERTY_TYPE.NUMERIC.toString():
        				AbstractPropertyRetrieval._PROPERTY_TYPE.STRING.toString()					
        );
        e.setAttribute("Reference", Integer.toString(item.getReference().getId()));
        e.appendChild(getURIElement(doc, item));
        e.appendChild(referenceReporter.getItemElement(doc, item.getReference()));
        return e;
	}
}
