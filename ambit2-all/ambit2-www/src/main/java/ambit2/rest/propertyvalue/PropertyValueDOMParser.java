package ambit2.rest.propertyvalue;

import org.w3c.dom.Element;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.PropertyValue;
import ambit2.rest.query.XMLTags;
import ambit2.rest.reference.AbstractDOMParser;

/**
<?xml version="1.0" encoding="UTF-8"?>
<Features xmlns="http://opentox.org/Feature/1.0">
<Feature CompoundID="1" ID="1" Name="MF" value="CH2O"/>
</Features>
 * @author nina
 *
 * @param <Q>
 */
public abstract class PropertyValueDOMParser extends AbstractDOMParser<PropertyValue>{
		public PropertyValueDOMParser() {
			super();
			setNameSpace(XMLTags.ns_opentox_feature_value);
			setNodeTag(XMLTags.node_feature);
		}

		
		public PropertyValue parse(Element element) throws AmbitException {
			try {
				PropertyValue pv = new PropertyValue();
				Property p = new Property(element.getAttribute(XMLTags.attr_name));
				p.setId(Integer.parseInt(element.getAttribute(XMLTags.attr_id)));
				pv.setProperty(p);
				pv.setValue(element.getAttribute(XMLTags.attr_value));
				return pv;
			} catch (NumberFormatException x) {
				throw new AmbitException("ID should be numeric!");
			} catch (Exception x) {
				throw new AmbitException(x);
			}
		}
	}