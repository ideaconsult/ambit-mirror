package ambit2.rest.structure;

import org.restlet.data.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.query.QueryXMLReporter;
import ambit2.rest.query.XMLTags;

public class StructureReporter extends QueryXMLReporter<QueryStructureByID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5178949909366051266L;
	public StructureReporter(Reference reference) {
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
	}				
}

