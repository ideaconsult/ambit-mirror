package ambit2.rest.structure;

import org.restlet.data.Request;
import org.w3c.dom.Document;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.query.QueryXMLReporter;

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
 */
public class StructureReporter extends QueryXMLReporter<QueryStructureByID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5178949909366051266L;
	public StructureReporter(Request reference) {
		super(reference);
		getProcessors().clear();
		getProcessors().add(new ProcessorStructureRetrieval());
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target,output);
				return target;
			};
		});	
		
	}
	@Override
	public void processItem(IStructureRecord record, Document output) {
		/*
		Element e_record = output.createElementNS(XMLTags.ns_opentox,node_structure);
        e_record.setAttribute(attr_idchemical,Integer.toString(record.getIdchemical()));
        e_record.setAttribute(attr_idstructure,Integer.toString(record.getIdstructure()));        
        e_record.appendChild(toURI(reference,output, record));
        e_record.appendChild(output.createCDATASection(record.getContent()));
        
        NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_opentox, XMLTags.node_dataset);
        for (int i=0; i < parent.getLength();i++)
        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {
        		parent.item(i).appendChild(e_record);
        		break;
        	}
        	*/
	}				
}

