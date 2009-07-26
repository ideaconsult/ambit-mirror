package ambit2.rest.dataset;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.query.XMLTags;

/**
 * http://opentox.org/wiki/1/Dataset
<pre>
<?xml version="1.0" encoding="UTF-8"?>
<ot:datasets xmlns:ot="http://opentox.org/1.0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://opentox.org/1.0/ datasets.xsd ">
  <ot:link href="http://tempuri.org" order="1"/>
</ot:datasets>

</pre>
 * @author nina
 *
 */
public class DatasetsXMLReporter extends QueryReporter<SourceDataset, IQueryRetrieval<SourceDataset>, Document> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7889958961008530806L;
	protected Reference reference ;
	public DatasetsXMLReporter(Reference reference) {
		this.reference = reference;
	}
	@Override
	public Document getOutput() throws AmbitException {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			doc.appendChild(doc.createElementNS(XMLTags.ns_opentox,
					XMLTags.node_datasets));
			return doc;
		} catch (ParserConfigurationException x) {
			throw new AmbitException(x);
		}
	}

	@Override
	public void processItem(SourceDataset dataset, Document doc) {
        Element e_dataset = DatasetReporter.toURI(reference ,doc, dataset);
        NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_opentox, XMLTags.node_datasets);
        for (int i=0; i < parent.getLength();i++)
        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {
        		parent.item(i).appendChild(e_dataset);
        		break;
        	}		
	}	

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
