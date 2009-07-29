package ambit2.rest.dataset;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.query.XMLTags;

/**
 * http://opentox.org/wiki/1/Dataset
 * <pre>
<?xml version="1.0" encoding="UTF-8"?>
<ot:dataset id="" name="" xmlns:ot="http://opentox.org/1.0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://opentox.org/1.0/ datasets.xsd ">
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
 */
public class DatasetReporter extends QueryReporter<SourceDataset, IQueryRetrieval<SourceDataset>, Document> {
	protected Reference reference ;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1402969311380709143L;
	public static Element toURI(Reference reference, Document doc, SourceDataset dataset) {

		Element e_uri = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_link);
		e_uri.setAttribute(XMLTags.attr_href,
				String.format("%s/%s/%s", 
						reference.toString(),XMLTags.node_dataset,Reference.encode(dataset.getName()) )
						);
        return e_uri;
	}		
	public DatasetReporter(Reference reference) {
		this.reference = reference;
	}
	public Element toXML(Document doc, LiteratureEntry reference) {
/*
		Element e_reference = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_reference);
        e_reference.setAttribute(XMLTags.attr_id,Integer.toString(reference.getId()));
        e_reference.setAttribute(XMLTags.attr_name,reference.getName());
		
        Element e_url = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_www);
        e_url.setTextContent(reference.getURL());
        
        e_reference.appendChild(e_url);
        return e_reference;
        */
		return null;
	}	
	
	@Override
	public Document getOutput() throws AmbitException {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			return doc;
		} catch (ParserConfigurationException x) {
			throw new AmbitException(x);
		}
	}
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void processItem(SourceDataset dataset, Document doc) {
		
		Element e_dataset = doc.createElementNS(XMLTags.ns_opentox,XMLTags.node_dataset);
        e_dataset.setAttribute(XMLTags.attr_id,Integer.toString(dataset.getId()));
        e_dataset.setAttribute(XMLTags.attr_name,dataset.getName());
		
        //e_dataset.appendChild(toXML(doc, dataset.getReference()));
        e_dataset.appendChild(toURI(reference,doc, dataset));
        doc.appendChild(e_dataset);
	}
    @Override
    public void footer(Document output, IQueryRetrieval<SourceDataset> query) {
    	// TODO Auto-generated method stub
    	
    }
    @Override
    public void header(Document output, IQueryRetrieval<SourceDataset> query) {
    	// TODO Auto-generated method stub
    	
    }
}
