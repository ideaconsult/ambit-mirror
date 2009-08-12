package ambit2.rest.property;

import org.w3c.dom.Element;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.rest.query.XMLTags;
import ambit2.rest.reference.AbstractDOMParser;

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
	public PropertyDOMParser() {
		super();
		setNameSpace(XMLTags.ns_opentox_feature_definition);
		setNodeTag(XMLTags.node_featuredef);
	}

	
	public Property parse(Element element) throws AmbitException {
		try {
			String name= element.getAttribute(XMLTags.attr_name);
			if ("".equals(name)) throw new AmbitException("Empty name");
			Property p = new Property(name);
			String label = Property.guessLabel(name);
			p.setLabel(label==null?name:label);
			p.setId(Integer.parseInt(element.getAttribute(XMLTags.attr_id)));
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