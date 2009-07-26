package ambit2.rest.query;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

public class QueryXMLReporter<Q extends IQueryRetrieval<IStructureRecord>> 
			extends QueryReporter<IStructureRecord,Q, Document> {
	protected final static String attr_idstructure = "idstructure";
	protected final static String attr_idchemical = "idchemical";	
	protected final static String node_structure = "structure";
	protected Reference reference;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7776155843790521467L;


	public QueryXMLReporter(Reference reference) {
		super();
		this.reference = reference;
	}
	@Override
	public void processItem(IStructureRecord record, Document output) {
		Element e_record = toURI(reference,output, record);
        /*
        NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_opentox, XMLTags.node_structures);
        for (int i=0; i < parent.getLength();i++)
        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {
        		parent.item(i).appendChild(e_record);
        		break;
        	}
        */
		output.appendChild(e_record);
	}
	@Override
	public Document getOutput() throws AmbitException{
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element node = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_dataset);
			node.appendChild(doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_features));
			doc.appendChild(node);
			return doc;
		} catch (ParserConfigurationException x) {
			throw new AmbitException(x);
		}
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public static Element toURI(Reference reference, Document doc, IStructureRecord record) {
		Element e_uri = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_compound);
        e_uri.setAttribute(XMLTags.attr_href,String.format("%s/%s/%d", (reference==null)?"":reference.toString(),XMLTags.node_compound,record.getIdchemical()));
        return e_uri;
	}		

}
