package ambit2.plugin.dbtools;

import ambit2.base.data.SelectionBean;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReader;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.processors.quality.QualityLabelWriter;
import ambit2.db.processors.quality.QualityValueWriter;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryStructureByQuality;
import ambit2.db.search.structure.QueryStructureByValueQuality;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Activity;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.While;
import com.microworkflow.process.Workflow;

public class StructureQualityWorkflow extends Workflow {
	protected static final String SELECTION_QUALITY="SELECTION_QUALITY";
	protected static final String DESCRIPTORS_NEWQUERY = "ambit2.plugin.dbtools.StructureQualityWorkflow.DESCRIPTORS_NEWQUERY";
	protected enum QUALITY_MODE {
		Fingerprints {
			@Override
			public String toString() {
				return "Structures consistency";
			}
		},
		CAS {
			@Override
			public String toString() {
				return "CAS numbers consistency";
			}
		},	
		Completed {
			@Override
			public String toString() {
				return "Quit, calculations are completed.";
			}		
		}
	};
	public StructureQualityWorkflow() {
        Sequence seq=new Sequence();
        seq.setName("[Quality]");    	
        seq.addStep(getCalculationOptionsSequence(
        		addStructureQualityVerifier(),
        		addValueQualityVerifier()
        		));
        
        //setDefinition(new LoginSequence(new DatasetSelection(seq)));
        setDefinition(new LoginSequence(seq));
     

	}
	public While getCalculationOptionsSequence(
				Activity structure,
				Activity values
				) {
		
		
		While loop = new While();
		Sequence body = new Sequence();
		final SelectionBean<QUALITY_MODE> selection = new SelectionBean<QUALITY_MODE>(
				QUALITY_MODE.values(),
				"Quality");

		UserInteraction<SelectionBean<QUALITY_MODE>> selectKey = new UserInteraction<SelectionBean<QUALITY_MODE>>(
				selection, SELECTION_QUALITY, "??????");
		selectKey.setName("Select quality verification method");

        
        Conditional valuesCondition = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				Object o = getContext().get(SELECTION_QUALITY);
	    				if (o instanceof SelectionBean)
	    					return QUALITY_MODE.CAS.equals(((SelectionBean)o).getSelected());
	    				else return false;
                    }
                }, 
                values,
                null);        
        
        Conditional strucCondition = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				Object o = getContext().get(SELECTION_QUALITY);
	    				if (o instanceof SelectionBean)
	    					return QUALITY_MODE.Fingerprints.equals(((SelectionBean)o).getSelected());
	    				else return false;
                    }
                }, 
                structure,
                valuesCondition);
        
        loop.setTestCondition(new TestCondition() {
        	@Override
        	public boolean evaluate() {
    			Object o = getContext().get(SELECTION_QUALITY);
    			boolean result = (o instanceof SelectionBean)? 
    				!QUALITY_MODE.Completed.equals(((SelectionBean<QUALITY_MODE>)o).getSelected())
    				:false;
    			
    			getContext().put(SELECTION_QUALITY, null);
    			return result;
        	}
        });
        
        loop.setName("Calculate");	            
        body.addStep(selectKey);
        body.addStep(strucCondition);
        loop.setBody(body);
		return loop;
	}	
	protected Sequence addStructureQualityVerifier() {
			Sequence seq = new Sequence();
			Primitive query = new Primitive(DBWorkflowContext.QUERY,DBWorkflowContext.QUERY,
					new Performer() {
				@Override
				public Object execute() throws Exception {
					return new QueryStructureByQuality();  //without quality labels
				}
			});
			seq.addStep(query);
			ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
				new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
			p.add(new ProcessorStructureRetrieval());		
			p.add(new QualityLabelWriter());
			
			DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
			batch.setProcessorChain(p);
			ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap = 
				new ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
					DBWorkflowContext.QUERY,
					DBWorkflowContext.BATCHSTATS,
					batch,false);
		    ap.setName("Structure quality verifier");	
		    seq.addStep(ap);
		    return seq;
	}	

	protected Sequence addValueQualityVerifier() {
		Sequence seq = new Sequence();
		Primitive query = new Primitive(DBWorkflowContext.QUERY,DBWorkflowContext.QUERY,
				new Performer() {
			@Override
			public Object execute() throws Exception {
				return new QueryStructureByValueQuality();  //without quality labels
			}
		});
		seq.addStep(query);
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p.add(new QualityValueWriter());
		
		DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
		batch.setProcessorChain(p);
		ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap = 
			new ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
				DBWorkflowContext.QUERY,
				DBWorkflowContext.BATCHSTATS,
				batch,false);
	    ap.setName("CAS quality verifier");	
	    seq.addStep(ap);
	    return seq;
}	
}
