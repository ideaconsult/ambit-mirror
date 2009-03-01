package ambit2.plugin.pbt;

import ambit2.core.data.LiteratureEntry;
import ambit2.core.processors.IProcessor;
import ambit2.core.processors.batch.IBatchStatistics;
import ambit2.db.SourceDataset;
import ambit2.db.processors.DBProcessorsChain;
import ambit2.db.processors.RepositoryWriter;
import ambit2.plugin.pbt.processors.PBTProperties;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.process.Workflow;

public class SavePBTWorkflow extends Workflow {
	public SavePBTWorkflow() {
		
		SourceDataset dataset = new SourceDataset(PBTWorkBook.PBT_TITLE,
				new LiteratureEntry(PBTWorkBook.PBT_TITLE,"by Clariant"));		
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
    	p1.setName("Save PBT");
    	setDefinition(new LoginSequence(p1));

	}

}
