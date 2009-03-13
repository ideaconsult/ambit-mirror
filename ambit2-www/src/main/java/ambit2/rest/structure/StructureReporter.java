package ambit2.rest.structure;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.DefaultAmbitProcessor;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.search.QueryStructureByID;
import ambit2.rest.query.QueryXMLReporter;
import ambit2.rest.query.XMLTags;

public class StructureReporter extends QueryXMLReporter<QueryStructureByID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5178949909366051266L;
	public StructureReporter() {
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
	protected void processItem(IStructureRecord record, Document output) {
		Element e_record = output.createElementNS(XMLTags.ns_ambit,node_structure);
        e_record.setAttribute(attr_idchemical,Integer.toString(record.getIdchemical()));
        e_record.setAttribute(attr_idstructure,Integer.toString(record.getIdstructure()));        
        e_record.appendChild(toURI(output, record));
        e_record.appendChild(output.createCDATASection(record.getContent()));
        
        NodeList parent = output.getElementsByTagNameNS(XMLTags.ns_ambit, XMLTags.node_dataset);
        for (int i=0; i < parent.getLength();i++)
        	if (parent.item(i).getNodeType() == Node.ELEMENT_NODE) {
        		parent.item(i).appendChild(e_record);
        		break;
        	}
	}				
}

