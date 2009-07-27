package ambit2.rest.query;

import java.io.StringWriter;

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
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.rest.structure.ConformerURIReporter;

/**
<pre>
<?xml version="1.0" encoding="UTF-8"?>
<ot:dataset id="" name="" xmlns:ot="http://opentox.org/1.0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://opentox.org/1.0/ dataset.xsd ">
  <ot:link href="http://tempuri.org" id="1" order="1"/>
  <ot:features>
    <ot:link href="http://tempuri.org" id="1" order="1"/>
  </ot:features>
  <ot:compound href="http://tempuri.org" id="1" order="1">
    <ot:conformer href="http://tempuri.org" id="1" order="1"/>
  </ot:compound>
</ot:dataset>
</pre>
 * @author nina
 *
 * @param <Q>
 */
public class QueryXMLReporter<Q extends IQueryRetrieval<IStructureRecord>> 
			extends QueryReporter<IStructureRecord,Q, Document> {
	protected CompoundURIReporter<IQueryRetrieval<IStructureRecord>> compoundURI ;
	protected ConformerURIReporter<IQueryRetrieval<IStructureRecord>> conformerURI;
	protected Reference reference;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7776155843790521467L;


	public QueryXMLReporter(Reference reference) {
		super();
		this.reference = reference;
		compoundURI = new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(reference);
		conformerURI = new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(reference);
		
	}
	@Override
	public void processItem(IStructureRecord record, Document output) {
		Element e_record = toURI(reference,output, record);
        if (e_record == null) return;
        NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_opentox, XMLTags.node_compounds);
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
			Element node = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_dataset);
			node.appendChild(doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_features));
			node.appendChild(doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_compounds));
			doc.appendChild(node);
			return doc;
		} catch (ParserConfigurationException x) {
			throw new AmbitException(x);
		}
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public Element toURI(Reference reference, Document doc, IStructureRecord record) {
		StringWriter w = new StringWriter();
		compoundURI.processItem(record, w);
		String uri = w.toString();
        NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_opentox, XMLTags.node_compound);
        for (int i=0; i < parent.getLength();i++)
        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {
        		//found same compound
        		if (uri.equals(((Element)parent.item(i)).getAttribute(XMLTags.attr_href))) {
        			return null;
        		}
        	}
        
		Element e_uri = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_compound);
        e_uri.setAttribute(XMLTags.attr_href,uri);
        e_uri.setAttribute(XMLTags.attr_id, String.format("%d",record.getIdchemical()));
        Element e_conformer = conformertoURI(reference, doc, record);
        if (e_conformer!=null)
        	e_uri.appendChild(e_conformer);
        return e_uri;
	}		

	public Element conformertoURI(Reference reference, Document doc, IStructureRecord record) {
		if (record.getIdstructure()<=0) return null;
		
		StringWriter w = new StringWriter();
		conformerURI.processItem(record, w);

		Element e_uri = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_conformer);
        e_uri.setAttribute(XMLTags.attr_href,w.toString());
        e_uri.setAttribute(XMLTags.attr_id, String.format("%d",record.getIdstructure()));
        return e_uri;
	}	
}
