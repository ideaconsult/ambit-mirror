package ambit2.rest.reference;

import org.w3c.dom.Element;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.rest.query.XMLTags;

/**
 * Schema as in {@link ReferenceDOMReporter} and at http://opentox.org/wiki/opentox/Feature
<pre>
  <?xml version="1.0" encoding="UTF-8" ?> 
 <schema xmlns="http://www.w3.org/2001/XMLSchema" targetNamespace="http://www.opentox.org/Reference/1.0" xmlns:tns="http://www.opentox.org/Reference/1.0" elementFormDefault="qualified">
 <complexType name="Reference">
  <attribute name="ID" type="string" use="required" /> 
  <attribute name="Name" type="string" use="required" /> 
  <attribute name="AlgorithmID" type="string" use="optional" /> 
  <attribute name="Parameters" type="string" use="optional" /> 
  <attribute name="ExperimentalProtocol" type="string" use="optional" /> 
  </complexType>
  </schema>
</pre>
 * <br>Example:
<pre>
<Reference AlgorithmID="http://www.cas.org" id="1" name="CAS Registry Number">
<link href="http://localhost:8181/reference/1"/>
</Reference>
</pre>
 * @author nina
 *
 */
public abstract class ReferenceDOMParser extends AbstractDOMParser<LiteratureEntry>{
	public ReferenceDOMParser() {
		super();
		setNameSpace(XMLTags.ns_opentox_reference);
		setNodeTag(XMLTags.node_reference);
	}

	
	public LiteratureEntry parse(Element element) throws AmbitException {
		try {
			String name= element.getAttribute(XMLTags.attr_name);
			if ("".equals(name)) throw new AmbitException("Empty name");
			return LiteratureEntry.getInstance(
					element.getAttribute(XMLTags.attr_name),
					element.getAttribute("AlgorithmID"),
					Integer.parseInt(element.getAttribute(XMLTags.attr_id))
					);
		} catch (AmbitException x) {
			throw x;
		} catch (NumberFormatException x) {
			throw new AmbitException("ID should be numeric!");
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
}
