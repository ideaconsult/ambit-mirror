package ambit2.rest.property;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.rest.query.XMLTags;
import ambit2.rest.reference.AbstractDOMParser;
import ambit2.rest.reference.ReferenceDOMParser;

/**
 * http://opentox.org/wiki/opentox/Feature
 * 
 * No support for Type attribute!!!
 * 
<pre>
  <?xml version="1.0" encoding="UTF-8" ?> 
  <schema xmlns="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.opentox.org/FeatureDefinition/1.0" xmlns:tns="http://www.opentox.org/FeatureDefinition/1.0" elementFormDefault="qualified">
  <complexType name="FeatureDefinition">
  <attribute name="ID" type="string" use="required" /> 
  <attribute name="Name" type="string" use="required" /> 
  <attribute name="Reference" type="string" use="required" /> 
  <attribute name="Type" type="string" use="required" /> 
  </complexType>
  </schema>
</pre>
 * @author nina
 *
 */
public abstract class PropertyDOMParser extends AbstractDOMParser<Property>{
	protected ReferenceDOMParser referenceParser;
	public PropertyDOMParser() {
		super();
		setNameSpace(XMLTags.ns_opentox_feature);
		setNodeTag(XMLTags.node_featuredef);
		referenceParser = new ReferenceDOMParser(){
			@Override
			public void handleItem(LiteratureEntry item) throws AmbitException {
				// TODO Auto-generated method stub
				
			}
		};
	}

	
	public Property parse(Element element) throws AmbitException {
		try {
			String name= element.getAttribute(XMLTags.attr_name);
			if ("".equals(name)) throw new AmbitException("Empty name");
			Property p = new Property(name);
			String label = Property.guessLabel(name);
			if (element.getAttribute(XMLTags.attr_type)!=null)
				p.setLabel(element.getAttribute(XMLTags.attr_type));
			else
				p.setLabel(label==null?name:label);
			p.setId(Integer.parseInt(element.getAttribute(XMLTags.attr_id)));
			
			LiteratureEntry le = null;
			NodeList nrefs = element.getElementsByTagName(XMLTags.node_reference);
			for (int i=0; i < nrefs.getLength();i++) 
				if (nrefs.item(i)instanceof Element) {
					le = referenceParser.parse((Element)nrefs.item(i));
					break;
				}
			if (le ==null) {
				String idref = element.getAttribute(XMLTags.node_reference);
				le = new LiteratureEntry("","");
				le.setId(Integer.parseInt(idref));
			}
			p.setReference(le);
			return p;
		} catch (AmbitException x) {
			throw x;
		} catch (NumberFormatException x) {
			throw new AmbitException("ID should be numeric!");
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
}