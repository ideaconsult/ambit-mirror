package ambit2.plugin.dbtools;

import java.sql.Connection;
import java.sql.SQLException;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.StructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IInputState;
import ambit2.core.processors.IProcessor;
import ambit2.core.processors.ProcessorsChain;
import ambit2.core.processors.batch.IBatchStatistics;
import ambit2.db.IDBProcessor;
import ambit2.db.SessionID;
import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
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
		SourceDataset dataset = new SourceDataset("Test");
		
        Sequence seq=new Sequence();
        seq.setName("[Import chemical structures]");    	

        
        IDBProcessor<String,IStructureRecord> processor = 
        	new IDBProcessor<String,IStructureRecord>() {
        	protected final StructureRecord r = new StructureRecord();
        	public IStructureRecord process(String target)
        			throws AmbitException {
        		r.setIdchemical(-1);
        		r.setIdstructure(-1);
        		r.setFormat("SDF");
        		r.setContent(target);
        		return r;
                //return new StructureRecord(-1,-1,target,"SDF");
        	}

			public boolean isEnabled() {
				return true;
			}

			public void setEnabled(boolean value) {
			}
			public Connection getConnection() {
					// TODO Auto-generated method stub
					return null;
				}
			public SessionID getSession() {
					// TODO Auto-generated method stub
					return null;
				}

			public void setConnection(Connection connection)
					throws DbAmbitException {
				// TODO Auto-generated method stub
				
			}

			public void close() throws SQLException {
				// TODO Auto-generated method stub
				
			}

			public void open() throws DbAmbitException {
				// TODO Auto-generated method stub
				
			}

			public void setSession(SessionID session) {
				// TODO Auto-generated method stub
				
			}
        };
        
        RepositoryWriter writer = new RepositoryWriter();
        writer.setDataset(dataset);
        ProcessorsChain<String, IBatchStatistics,IProcessor> chain = 
        		new ProcessorsChain<String, IBatchStatistics,IProcessor>();
        chain.add(processor);
        chain.add(writer);

        
        BatchDBProcessor batch = new BatchDBProcessor();
        batch.setProcessorChain(chain);
    	ActivityPrimitive<IInputState,IBatchStatistics> p1 = 
    		new ActivityPrimitive<IInputState,IBatchStatistics>( 
    			InputFileSelection.INPUTFILE,
    			DBWorkflowContext.BATCHSTATS,
    			(IDBProcessor)batch,false);

        p1.setName("Read file and import structures");
        
        Sequence s1 = new Sequence();
        Primitive p2 = new Primitive<FileInputState, SourceDataset> (new Performer<FileInputState, SourceDataset>() {
        	public SourceDataset execute() throws Exception {
        			return new SourceDataset(getTarget().getFilename());
        	};
        }
        );
        s1.addStep(p2);
        s1.addStep(new DatasetSelection(p1,dataset));
        
//        DbSrcDatasetWriter TODO
        setDefinition(new LoginSequence(new InputFileSelection(s1)));
	}
}
