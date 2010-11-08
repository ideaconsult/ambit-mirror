package ambit2.rest.dataset;

import org.restlet.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryDOMReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
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
public class DatasetsXMLReporter extends QueryDOMReporter<SourceDataset, IQueryRetrieval<SourceDataset>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7889958961008530806L;

	public DatasetsXMLReporter(Request reference,ResourceDoc doc) {
		super(reference,doc);
	}

	@Override
	protected QueryURIReporter createURIReporter(Request reference,ResourceDoc doc) {
		return new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(reference,doc);
	}
	@Override
	public void header(Document doc, IQueryRetrieval<SourceDataset> query) {
		doc.appendChild(doc.createElementNS(XMLTags.ns_opentox,
				XMLTags.node_datasets));
		
	}
	@Override
	public void footer(Document output, IQueryRetrieval<SourceDataset> query) {
		
	}
	@Override
	public Object processItem(SourceDataset dataset) throws AmbitException {

        NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_opentox, XMLTags.node_datasets);
        for (int i=0; i < parent.getLength();i++)
        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {
        		parent.item(i).appendChild(getItemElement(output, dataset));
        		break;
        	}	
        return null;
	}	
	@Override
	public Element getItemElement(Document doc, SourceDataset dataset) {
		return getURIElement(doc, dataset);
	}



}
