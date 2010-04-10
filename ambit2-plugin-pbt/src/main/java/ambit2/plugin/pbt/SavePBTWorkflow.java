package ambit2.plugin.pbt;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.db.processors.DBProcessorsChain;
import ambit2.db.processors.RepositoryWriter;
import ambit2.plugin.pbt.processors.PBTProperties;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class SavePBTWorkflow extends Workflow {
	public SavePBTWorkflow() {
		
		SourceDataset dataset = new SourceDataset(PBTWorkBook.PBT_TITLE,
				LiteratureEntry.getInstance(PBTWorkBook.PBT_TITLE,"by Clariant"));		
        final RepositoryWriter writer = new RepositoryWriter();
        writer.setDataset(dataset);
        
        final DBProcessorsChain chain = new DBProcessorsChain();
	    chain.add(new PBTProperties());
	    chain.add(writer);
	    
    	ActivityPrimitive p1 = 
    		new ActivityPrimitive( 
    			PBTWorkBook.PBT_WORKBOOK,
    			DBWorkflowContext.BATCHSTATS,
    			chain,false) {
    		
    		@Override
    		public String toString() {
    			return "Save PBT";
    		}
    		
    	};
    	Primitive p2 = new Primitive(new Performer<PBTWorkBook,Boolean>() {
    		@Override
    		public Boolean execute() throws Exception {
    			Object o = context.get(PBTWorkBook.PBT_WORKBOOK);
    			if ((o!=null) && (o instanceof PBTWorkBook)) {
    				((PBTWorkBook)o).setModified(false);
    			} 
    			return true;
    		}
    	});
    	p2.setName("");	
    	p1.setName("Save PBT");
    	Sequence s = new Sequence();
    	s.addStep(p1);
    	s.addStep(p2);
    	s.setName("Completed");
    	setDefinition(new LoginSequence(s));

	}

}
