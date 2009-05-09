package ambit2.workflow.library;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.SelectionBean;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.base.processors.StructureRecordsAppender;
import ambit2.core.io.IInputState;
import ambit2.db.processors.BatchDBProcessor;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.structure.QueryField;
import ambit2.workflow.DBProcessorPerformer;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;

import com.microworkflow.process.Conditional;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.While;

/**
 * Encapsulates the initial definition/selection of chemical structure(s)
 * @author nina
 *
 */
public class DefineStructure extends While {
	protected final String SELECTION = "DefineStructure.SELECTION";
	protected final String MORESTRUCTURES = "DefineStructure.MORESTRUCTURES";
	protected enum ADD_STRUCTURE {
		another_structure {
			@Override
			public String toString() {
				return "Define another target structure";
			}
		},

		clear_start_over {
			@Override
			public String toString() {
				return "Remove target structure(s) and start again";
			}
		},	
		no_more {
			@Override
			public String toString() {
				return "No, the set of target structures is complete";
			}
		},		
	};	
	protected enum DEFINE_MODE {
		structure_find {
			@Override
			public String toString() {
				return "Find structure";
			}
		},
		structure_draw {
			@Override
			public String toString() {
				return "Draw structure";
			}
		},
		structure_load {
			@Override
			public String toString() {
				return "Load from file";
			}
		},	
		do_nothing {
			@Override
			public String toString() {
				return "Reuse current target structure(s)";
			}
		}			
	};
	public DefineStructure() {
        //add more
		SelectionBean<ADD_STRUCTURE> more = new SelectionBean<ADD_STRUCTURE>(
				ADD_STRUCTURE.values(),"Define structure"
				);

        UserInteraction<SelectionBean<ADD_STRUCTURE>> ui_more = new UserInteraction<SelectionBean<ADD_STRUCTURE>>(
        		more,
        		MORESTRUCTURES,"Add more target structures");
        
		Sequence load = getFileLoadSequence();
		load.addStep(ui_more);
		
        Conditional loadCondition = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				Object o = getContext().get(SELECTION);
	
	    				if (o instanceof SelectionBean)
	    					return DEFINE_MODE.structure_load.equals(((SelectionBean)o).getSelected());
	    				else {
	    					return false;
	    				}
                    }
                }, 
                load,
                ui_more);
        loadCondition.setName("Load from file?");   		
		
		Sequence draw = new UserInteraction<StructureRecord>(
        		new StructureRecord(),
        		DBWorkflowContext.RECORD,
        		"Draw structure").addStep(new SequenceAppendRecord()).addStep(ui_more);
        
        Conditional drawCondition = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				Object o = getContext().get(SELECTION);
	    				if (o instanceof SelectionBean)
	    					return DEFINE_MODE.structure_draw.equals(((SelectionBean)o).getSelected());
	    				else return false;
                    }
                }, 
                draw,
                loadCondition);
        drawCondition.setName("Draw structure diagram?");   
        
        Sequence q = new Sequence();
        q.addStep(new QuerySelection());
        q.addStep(new QueryExecution(new QueryField(),DBWorkflowContext.QUERY_POPUP,null));
        UserInteraction browse = new UserInteraction(
        		true,DBWorkflowContext.USERINTERACTION,DBWorkflowContext.QUERY_POPUP,"Browse results");
        q.addStep(browse);        
        q.addStep(new SequenceAppendQueryResults(DBWorkflowContext.QUERY_POPUP));
        q.addStep(ui_more);
		
        Conditional findCondition = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				Object o = getContext().get(SELECTION);
	    				if (o instanceof SelectionBean)
	    					return DEFINE_MODE.structure_find.equals(((SelectionBean)o).getSelected());
	    				else return false;
                    }
                }, 
                q,
                drawCondition);
        findCondition.setName("Find target structure in database?");        
        
        //find, draw,load
		SelectionBean<DEFINE_MODE> mode = new SelectionBean<DEFINE_MODE>(
				DEFINE_MODE.values(),"Define target structure"
				);

        UserInteraction<SelectionBean<DEFINE_MODE>> defineMode = new UserInteraction<SelectionBean<DEFINE_MODE>>(
        		mode,
        		SELECTION,"Select how to define structure");
        
        
        Sequence body = new Sequence();
        body.addStep(defineMode);
        body.addStep(findCondition);
        setBody(body);

        setTestCondition(new TestCondition() {
        	@Override
        	public boolean evaluate() {
    			Object o = getContext().get(MORESTRUCTURES);
    			if (o instanceof SelectionBean) 
    		
    				switch (((SelectionBean<ADD_STRUCTURE>)o).getSelected()) {
    				case another_structure: {
    					return true;}
    				case no_more: {
    					return false;
    					}
    				case clear_start_over: {
    					Object records = getContext().get(DBWorkflowContext.RECORDS);
    					try {
    						if (records != null) ((List)records).clear();
    					} catch (Exception x) {
    						
    					}
    					getContext().put(DBWorkflowContext.RECORDS,null);
    					return true;
    				}
    				default: return true;
    				
    				}
    			else return true;
        	}
        });
        
        setName("Define target structure(s)");
	}
	
	protected Sequence getFileLoadSequence() {
		
		final StructureRecordsAppender writer = new StructureRecordsAppender();
		
        final ProcessorsChain<String, IBatchStatistics,IProcessor> chain = 
    		new ProcessorsChain<String, IBatchStatistics,IProcessor>();
        chain.add(writer);		
        
        final BatchDBProcessor batch = new BatchDBProcessor();
        batch.setProcessorChain(chain);
        
        DBProcessorPerformer<BatchDBProcessor,IInputState,IBatchStatistics> performer = 
        				new DBProcessorPerformer<BatchDBProcessor,IInputState,IBatchStatistics>(batch,true) {
        	@Override
        	public IBatchStatistics execute() throws Exception {
	   			Object o = getContext().get(DBWorkflowContext.RECORDS);
    			List<IStructureRecord> records = null;
    			if ((o==null) || !(o instanceof List)) {
    				records = new ArrayList<IStructureRecord>();
    			} else records = (List<IStructureRecord>)o;        		
        		writer.setRecords(records);
        		getContext().put(DBWorkflowContext.RECORDS,records);
        		return super.execute();
        	}
        };
        
    	Sequence seq = new Sequence();
    	seq.addStep(new Primitive<IInputState,IBatchStatistics>( 
    			InputFileSelection.INPUTFILE,
    			DBWorkflowContext.BATCHSTATS,
    			performer));
    		

    	return new InputFileSelection(seq);
	}
}
