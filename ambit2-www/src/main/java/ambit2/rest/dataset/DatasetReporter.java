package ambit2.rest.dataset;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.query.XMLTags;

public class DatasetReporter extends QueryReporter<SourceDataset, IQueryRetrieval<SourceDataset>, Document> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1402969311380709143L;
	public Element toURI(Document doc, SourceDataset dataset) {
		Element e_uri = doc.createElementNS(XMLTags.ns_ambit,XMLTags.node_uri);
		StringBuilder b = new StringBuilder();
		b.append(XMLTags.slash);
		b.append(XMLTags.node_dataset);
		b.append(XMLTags.slash);
		b.append(Reference.encode(dataset.getName()));
        e_uri.setTextContent(b.toString());
        return e_uri;
	}		
	public Element toXML(Document doc, LiteratureEntry reference) {

		Element e_reference = doc.createElementNS(XMLTags.ns_ambit,XMLTags.node_reference);
        e_reference.setAttribute(XMLTags.attr_id,Integer.toString(reference.getId()));
        e_reference.setAttribute(XMLTags.attr_name,reference.getName());
		
        Element e_url = doc.createElementNS(XMLTags.ns_ambit,XMLTags.node_www);
        e_url.setTextContent(reference.getURL());
        
        e_reference.appendChild(e_url);
        return e_reference;
	}	
	
	@Override
	public Document getOutput() throws AmbitException {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			doc.appendChild(doc.createElementNS(XMLTags.ns_ambit,XMLTags.node_datasets));
			return doc;
		} catch (ParserConfigurationException x) {
			throw new AmbitException(x);
		}
	}
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void processItem(SourceDataset dataset, Document doc) {
		Element e_dataset = doc.createElementNS(XMLTags.ns_ambit,XMLTags.node_dataset);
        e_dataset.setAttribute(XMLTags.attr_id,Integer.toString(dataset.getId()));
        e_dataset.setAttribute(XMLTags.attr_name,dataset.getName());
		
        e_dataset.appendChild(toXML(doc, dataset.getReference()));
        e_dataset.appendChild(toURI(doc, dataset));
        NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_ambit, XMLTags.node_datasets);
        for (int i=0; i < parent.getLength();i++)
        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {
        		parent.item(i).appendChild(e_dataset);
        		break;
        	}		
	}
    
}
