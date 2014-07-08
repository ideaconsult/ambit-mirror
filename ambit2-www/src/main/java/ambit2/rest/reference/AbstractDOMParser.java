package ambit2.rest.reference;

import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class AbstractDOMParser<T>  {
	protected String nameSpace;
	protected String nodeTag;
	
	public String getNameSpace() {
		return nameSpace;
	}
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	public String getNodeTag() {
		return nodeTag;
	}
	public void setNodeTag(String nodeTag) {
		this.nodeTag = nodeTag;
	}

	public void parse(Reader reader)  throws ParserConfigurationException,IOException, SAXException, AmbitException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        parse(builder.parse(new InputSource(reader)));     
	}	
	/**
	 * There is smth weird with namespaces, and this method doesn't consider name spaces at all
	 * @param doc
	 * @throws AmbitException
	 */
	
	public void parse(Document doc)  throws AmbitException {
		
		NodeList nodes = doc.getElementsByTagName(nodeTag);
		for (int i=0; i < nodes.getLength();i++) 
			if (nodes.item(i) instanceof Element) {
				
				handleItem(parse((Element)nodes.item(i)));
			}
	}
	public abstract void handleItem(T item) throws AmbitException ;
	public abstract T parse(Element element) throws AmbitException;
}
