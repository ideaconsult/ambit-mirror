package ambit2.rest.aa.opensso.policy;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parses OpenSSO XML policies.  Should be moved to opentox-opensso library
 * <pre>http://java.net/projects/opensso/</pre>
 * @author nina

 */
public class PolicyParser {
	protected Document doc;
	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
	public enum type {
		cn {
			@Override
			public String toString() {
				return "User";
			}
		},
		member {
			@Override
			public String toString() {
				return "members";
			}
		},
		LDAPUsers {
			@Override
			public String toString() {
				return "Applies to user ";
			}
		},
		LDAPGroups {
			@Override
			public String toString() {
				return "Applies to group ";
			}
		};
	}
	public enum tags {
		ResourceName,
		Policies,
		Policy,
		Rule,
		Subjects,
		Subject,
		AttributeValuePair,
		Attribute,
		Value
		
	}
	public PolicyParser(String content) throws Exception {
		doc = parse(content);
	}
	
	public Document parse(String content) throws IOException,  SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
       
        StringReader reader = new StringReader(content);
        Document doc = builder.parse(new InputSource(reader));
        doc.normalize();
        return doc;
	}
	
	public String getHTML() throws Exception {
		StringBuilder b = new StringBuilder();
		
		Element top = doc.getDocumentElement();
		org.w3c.dom.NodeList policies = top.getElementsByTagName(tags.Policy.toString());
		for (int i=0; i < policies.getLength(); i++) {
			Element policy = (Element) policies.item(i);
			//b.append(String.format("Policy %s<br>\n",policy.getAttribute("name")));
			org.w3c.dom.NodeList rules = policy.getElementsByTagName(tags.Rule.toString());
			for (int j=0; j < rules.getLength(); j++) {
				Element rule = ((Element) rules.item(j));

				b.append(String.format("<h4>Rule %s</h4>\n", rule.getAttribute("name")));
				
				org.w3c.dom.NodeList ResourceName = rule.getElementsByTagName(tags.ResourceName.toString());
				for (int k=0; k < ResourceName.getLength(); k++) {
					Element attr = ((Element) ResourceName.item(k));
					b.append(String.format("Resource %s<br>\n", attr.getAttribute("name")));
				}	
				
				b.append("<table>");
				org.w3c.dom.NodeList attrs = rule.getElementsByTagName(tags.AttributeValuePair.toString());
				for (int k=0; k < attrs.getLength(); k++) {
					Element attr = ((Element) attrs.item(k));
					processAttrValuePair(attr,b);
				}				
				b.append("</table>");
			}
			//subjects
			org.w3c.dom.NodeList subjects = policy.getElementsByTagName(tags.Subjects.toString());
			for (int j=0; j < subjects.getLength(); j++) {
				Element subject = ((Element) subjects.item(j));
				
				org.w3c.dom.NodeList ss = subject.getElementsByTagName(tags.Subject.toString());
				/**
Subject 'member' 'LDAPGroups' 'inclusive'
Values	cn=member, ou=groups, dc=opentox, dc=org

Subject 'nina' 'LDAPUsers' 'inclusive'
Values	uid=nina, ou=people, dc=opentox,dc=org
				 */
				for (int l=0; l < ss.getLength(); l++) {
					Element s = ((Element) ss.item(j));
					
					String t = s.getAttribute("type");
					try {
						t = type.valueOf(s.getAttribute("type")).toString();
					} catch (Exception x) {
						
					}
					String name = s.getAttribute("name");
					try {
						name = type.valueOf(s.getAttribute("name")).toString();

					} catch (Exception x) {
						
					}					
					b.append(String.format("%s <b>%s</b> [%s]<br>\n",
							t,
							name,
							s.getAttribute("includeType")));
					
				
				}

			}
		}
		return b.toString();
	}
	
	public void processAttrValuePair(Element vp, StringBuilder b) {
		org.w3c.dom.NodeList attr = vp.getElementsByTagName(tags.Attribute.toString());
		b.append("<tr>\n");
		for (int k=0; k < attr.getLength(); k++) {
			b.append(String.format("<th>%s</th>",((Element)attr.item(k)).getAttribute("name")));
		}
		org.w3c.dom.NodeList val = vp.getElementsByTagName(tags.Value.toString());
		for (int k=0; k < val.getLength(); k++) {
			b.append(String.format("<td>%s</td>",((Element)val.item(k)).getTextContent()));
		}	
		b.append("</tr>\n");
	}	
}
