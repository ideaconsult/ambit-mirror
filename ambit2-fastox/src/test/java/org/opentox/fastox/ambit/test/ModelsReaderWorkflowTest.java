package org.opentox.fastox.ambit.test;
import junit.framework.Assert;

import org.junit.Test;
import org.opentox.fastox.ambit.ui.RDFTableModel;
import org.opentox.fastox.ambit.workflow.ModelsReaderWorkflow;
import org.opentox.fastox.ambit.workflow.RDFWorkflowContext;
import org.opentox.fastox.ambit.workflow.RDFWorkflowContext.WFC_KEY;
import org.opentox.rdf.OT.OTClass;
import org.restlet.data.Reference;

import ambit2.rest.rdf.RDFObjectIterator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;


public class ModelsReaderWorkflowTest {
	@Test
	public void test() throws Exception {
		RDFWorkflowContext context = new RDFWorkflowContext();
		context.put(WFC_KEY.MODEL_REFERENCE.toString(),new Reference("http://ambit.uni-plovdiv.bg:8080/ambit2/model/1"));			

		ModelsReaderWorkflow wf = new ModelsReaderWorkflow();
		wf.executeWith(context);
		OntModel model = context.getRDFModel();
		Assert.assertTrue(model.size()>0);
		
		RDFTableModel table = new RDFTableModel(OTClass.Model);
		table.setRecords(model);
		Assert.assertEquals(1,table.getRowCount());
		Object resource = table.getValueAt(0,0);
		Assert.assertTrue(resource instanceof Resource);
		Assert.assertEquals("http://ambit.uni-plovdiv.bg:8080/ambit2/model/1", resource.toString());
		Assert.assertEquals("pKa",RDFObjectIterator.getTitle((Resource)resource));
	}
}
