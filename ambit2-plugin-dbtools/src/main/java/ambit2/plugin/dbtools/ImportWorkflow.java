package ambit2.plugin.dbtools;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IInputState;
import ambit2.db.IDBProcessor;
import ambit2.db.SourceDataset;
import ambit2.db.processors.BatchDBProcessor;
import ambit2.db.processors.RepositoryWriter;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.library.DatasetSelection;
import ambit2.workflow.library.InputFileSelection;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class ImportWorkflow extends Workflow {

	public ImportWorkflow()  {
		SourceDataset dataset = new SourceDataset("Default");
		
        Sequence seq=new Sequence();
        seq.setName("[Import chemical structures]");    	

        final RepositoryWriter writer = new RepositoryWriter();
        writer.setDataset(dataset);
        final ProcessorsChain<String, IBatchStatistics,IProcessor> chain = 
        		new ProcessorsChain<String, IBatchStatistics,IProcessor>();
        //chain.add(processor);
        chain.add(writer);

        final BatchDBProcessor batch = new BatchDBProcessor();
        batch.setProcessorChain(chain);
    	ActivityPrimitive<IInputState,IBatchStatistics> p1 = 
    		new ActivityPrimitive<IInputState,IBatchStatistics>( 
    			InputFileSelection.INPUTFILE,
    			DBWorkflowContext.BATCHSTATS,
    			(IDBProcessor)batch,false) {
    		
    	};

        p1.setName("Read file and import structures");
        
        Sequence s1 = new Sequence();
        Primitive<FileInputState, SourceDataset> p2 = new Primitive<FileInputState, SourceDataset> (
        				InputFileSelection.INPUTFILE,
        				DBWorkflowContext.DATASET,
        				new Performer<FileInputState, SourceDataset>() {
        	public SourceDataset execute() throws Exception {
        			SourceDataset dataset = 
        						new SourceDataset(getTarget().getFile().getName(),
        						LiteratureEntry.getInstance(getTarget().getFilename(),"file"));
        			writer.setDataset(dataset);
        			return dataset;
        	};
        }
        );
        p2.setName("Create new dataset");
        s1.addStep(p2);
        s1.addStep(new DatasetSelection(p1,dataset));

        setDefinition(new LoginSequence(new InputFileSelection(s1)));

	}

}
