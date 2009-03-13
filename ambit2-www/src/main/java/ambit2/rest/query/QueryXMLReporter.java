package ambit2.rest.query;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

public class QueryXMLReporter<Q extends IQueryRetrieval<IStructureRecord>> 
			extends QueryReporter<IStructureRecord,Q, Document> {
	protected final static String attr_idstructure = "idstructure";
	protected final static String attr_idchemical = "idchemical";	
	protected final static String node_structure = "structure";
	/**
	 * 
	 */
	private static final long serialVersionUID = -7776155843790521467L;

	public QueryXMLReporter() {
		super();
	}
	@Override
	protected void processItem(IStructureRecord record, Document output) {
		Element e_record = output.createElementNS(XMLTags.ns_ambit,node_structure);
        e_record.setAttribute(attr_idchemical,Integer.toString(record.getIdchemical()));
        e_record.setAttribute(attr_idstructure,Integer.toString(record.getIdstructure()));        
        e_record.appendChild(toURI(output, record));
        
        NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_ambit, XMLTags.node_dataset);
        for (int i=0; i < parent.getLength();i++)
        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {
        		parent.item(i).appendChild(e_record);
        		break;
        	}
	}
	@Override
	public Document getOutput() throws AmbitException{
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element node = doc.createElementNS(XMLTags.ns_ambit,XMLTags.node_dataset);
			doc.appendChild(node);
			return doc;
		} catch (ParserConfigurationException x) {
			throw new AmbitException(x);
		}
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	
	public Element toURI(Document doc, IStructureRecord record) {
		Element e_uri = doc.createElementNS(XMLTags.ns_ambit,XMLTags.node_uri);
		StringBuilder b = new StringBuilder();
		b.append(XMLTags.slash);
		b.append(node_structure);
		b.append(XMLTags.slash);
		b.append(record.getIdstructure());
        e_uri.setTextContent(b.toString());
        return e_uri;
	}		
}
