package org.opentox.fastox.ambit.test;

import junit.framework.Assert;

import org.junit.Test;
import org.opentox.fastox.ambit.ui.RDFTableModel;
import org.opentox.fastox.ambit.workflow.RDFWorkflowContext;
import org.opentox.fastox.ambit.workflow.RDFWorkflowContext.WFC_KEY;
import org.opentox.fastox.ambit.workflow.StructureReaderWorkflow;
import org.opentox.rdf.OT.OTClass;
import org.restlet.data.Reference;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class StructuresReaderWorkflowTest {

	@Test
	public void test() throws Exception {
		RDFWorkflowContext context = new RDFWorkflowContext();
		context.put(WFC_KEY.DATASET_REFERENCE.toString(),new Reference("http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/8?max=10"));			

		StructureReaderWorkflow wf = new StructureReaderWorkflow();
		wf.executeWith(context);
		OntModel model = context.getRDFModel();
		Assert.assertTrue(model.size()>0);
		
		RDFTableModel table = new RDFTableModel(OTClass.DataEntry);
		table.setRecords(model);
		Assert.assertEquals(10,table.getRowCount());
		Object resource = table.getValueAt(0,0);
		Assert.assertTrue(resource instanceof Resource);
		
		Assert.assertEquals("http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/8", resource.toString());
		//Assert.assertEquals("pKa",RDFObjectIterator.getTitle((Resource)resource));
	}
	
	//returns text/html as content-type for the returned entity (the content is rdf+xml as requested)
	@Test
	public void testIST() throws Exception {
		RDFWorkflowContext context = new RDFWorkflowContext();
		context.put(WFC_KEY.DATASET_REFERENCE.toString(),new Reference("http://webservices.in-silico.ch/test/dataset/37"));
				//"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/8?max=10"));			

		StructureReaderWorkflow wf = new StructureReaderWorkflow();
		wf.executeWith(context);
		OntModel model = context.getRDFModel();
		Assert.assertTrue(model.size()>0);
		
		RDFTableModel table = new RDFTableModel(OTClass.DataEntry);
		table.setRecords(model);
		Assert.assertEquals(10,table.getRowCount());
		Object resource = table.getValueAt(0,0);
		Assert.assertTrue(resource instanceof Resource);
		
		Assert.assertEquals("http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/8", resource.toString());
		//Assert.assertEquals("pKa",RDFObjectIterator.getTitle((Resource)resource));
	}	
}
