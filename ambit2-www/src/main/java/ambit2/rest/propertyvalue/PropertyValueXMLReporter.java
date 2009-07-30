package ambit2.rest.propertyvalue;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveField;
import ambit2.rest.QueryDOMReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.query.XMLTags;

/**
<pre>
	<complexType name="Feature">
		<attribute name="ID" type="string"  use="required"></attribute>
		<attribute name="CompoundID" type="string"  use="required"></attribute>
		<attribute name="Value" type="string"  use="required"></attribute>
		<attribute name="ConformerID" type="string"  use="optional"></attribute>
	</complexType>
</pre>
 * @author nina
 *
 * @param <Q>
 */
public class PropertyValueXMLReporter extends QueryDOMReporter<Object,RetrieveField>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8615960185043692493L;
	protected Property property;
	protected String feature_uri;
	
	public PropertyValueXMLReporter(Reference baseRef) {
		super(baseRef);
	}	

	public PropertyValueXMLReporter() {
		this(null);
	}	
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new PropertyValueURIReporter<IQueryRetrieval<Property>>(reference);
	}
	public Document getOutput() throws AmbitException {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			return doc;
		} catch (ParserConfigurationException x) {
			throw new AmbitException(x);
		}
	}	

	public void header(Document doc, RetrieveField query) { 
		
		/* needs IStructureRecord 
		StringWriter w = new StringWriter();
		uriReporter.processItem(query.getFieldname(),w);
		feature_uri = w.toString();
		*/
		doc.appendChild(doc.createElementNS(XMLTags.ns_opentox_feature,
				XMLTags.node_features));		
		property = query.getFieldname();
		
	}
	@Override
	public void processItem(Object item, Document doc) {

        NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_opentox_feature, XMLTags.node_features);
        for (int i=0; i < parent.getLength();i++)
        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {
        		

        		/*
        		Element e_uri = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_link);
        		e_uri.setAttribute(XMLTags.attr_href,feature_uri);
        		e_feature.appendChild(e_uri);
        		*/
        		parent.item(i).appendChild(getItemElement(doc, item));
        		break;
        	}	
	}
	@Override
	public Element getItemElement(Document doc, Object item) {
		Element e_feature = doc.createElementNS(XMLTags.ns_opentox_feature,XMLTags.node_feature);
		e_feature.setAttribute(XMLTags.attr_name,property.getName());
		e_feature.setAttribute(XMLTags.attr_value,item.toString());
		return e_feature;
	}
	@Override
	public void footer(Document output, RetrieveField query) {
		
	}
}
