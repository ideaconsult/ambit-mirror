package ambit2.plugin.dbtools;

import ambit2.base.data.AmbitUser;
import ambit2.base.data.QLabel;
import ambit2.base.data.SelectionBean;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.db.DbReader;
import ambit2.db.SourceDataset;
import ambit2.db.processors.AbstractUpdateProcessor;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.processors.QualityStatisticsProcessor;
import ambit2.db.processors.AbstractRepositoryWriter.OP;
import ambit2.db.processors.FP1024Writer.FPTable;
import ambit2.db.processors.quality.FPStructureWriter;
import ambit2.db.processors.quality.QualityLabelWriter;
import ambit2.db.processors.quality.QualityValueWriter;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.MissingFingerprintsQuery;
import ambit2.db.search.structure.QueryDataset;
import ambit2.db.search.structure.QueryStructure;
import ambit2.db.search.structure.QueryStructureByQuality;
import ambit2.db.search.structure.QueryStructureByValueQuality;
import ambit2.db.update.qlabel.CreateQLabelPair;
import ambit2.db.update.qlabel.DeleteStructureQLabel;
import ambit2.db.update.qlabel.DeleteValueQLabel;
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
	public enum QUALITY_MODE {
		Structure_Fingerprints {
			@Override
			public String toString() {
				return "Assign quality labels for pair of structures";
			}
			@Override
			public Activity getActivity() {
				return addStructureFingerprints();
			}
		},		
		ConsensusReport {
			@Override
			public String toString() {
				return "Report Consensus labels summary ";
			}
			@Override
			public Activity getActivity() {
				return printReport();
			}
		},		
		/*
		StructureType {
			@Override
			public String toString() {
				return "Assigns structure type (1D, 2D, 3D, Markush)";
			}
			@Override
			public Activity getActivity() {
				return addStructureType();
			}
		},		
		
		Structures {
			@Override
			public String toString() {
				return "Structures consistency";
			}
			@Override
			public Activity getActivity() {
				return addStructureQualityVerifier();
			}
		},
		Remove_StructuresLabel {
			@Override
			public String toString() {
				return "Remove quality labels for structures";
			}
			@Override
			public Activity getActivity() {
				return addStructureLabelRemover();
			}
		},

		CAS {
			@Override
			public String toString() {
				return "CAS numbers consistency";
			}
			@Override
			public Activity getActivity() {
				return addValueQualityVerifier();
			}
		},	

		Remove_ValuesLabel {
			@Override
			public String toString() {
				return "Remove quality labels for properties";
			}
			@Override
			public Activity getActivity() {
				return addValueLabelRemover();
			}
		},			
	*/
		Completed {
			@Override
			public String toString() {
				return "Quit, calculations are completed.";
			}		
		};
		public  Activity getActivity() {
			return null;
		}
	};
	public StructureQualityWorkflow() {
        Sequence seq=new Sequence();
        seq.setName("[Quality]");    	
        seq.addStep(getCalculationOptionsSequence());
        
        //setDefinition(new LoginSequence(new DatasetSelection(seq)));
        setDefinition(new LoginSequence(seq));
     

	}
	public While getCalculationOptionsSequence() {
		
		
		While loop = new While();
		Sequence body = new Sequence();
		final SelectionBean<QUALITY_MODE> selection = new SelectionBean<QUALITY_MODE>(
				QUALITY_MODE.values(),
				"Quality");

		UserInteraction<SelectionBean<QUALITY_MODE>> selectKey = new UserInteraction<SelectionBean<QUALITY_MODE>>(
				selection, SELECTION_QUALITY, "??????");
		selectKey.setName("Select quality verification method");

		Activity prevActivity = null;
		Activity start = null; 
        for (QUALITY_MODE m : QUALITY_MODE.values()) {
        	Activity activity = m.getActivity();
            Conditional condition = new Conditional(
                    new ModeCondition(m), 
                    activity,
                    prevActivity);
            prevActivity = condition;
            start = condition;
        }
        
        
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
        
        loop.setName("Process");	            
        body.addStep(selectKey);
        body.addStep(start);
        loop.setBody(body);
		return loop;
	}	
	
	protected static Activity printReport() {

	    return new ActivityPrimitive(DBWorkflowContext.BATCHSTATS,DBWorkflowContext.BATCHSTATS,
	    		new QualityStatisticsProcessor()
	    );

	}		
	
	protected static Sequence addStructureFingerprints() {
		Sequence seq = new Sequence();
		Primitive query = new Primitive(DBWorkflowContext.QUERY,DBWorkflowContext.QUERY,
				new Performer() {
			@Override
			public Object execute() throws Exception {
				return new MissingFingerprintsQuery(FPTable.fp1024_struc);  //without quality labels
			}
		});
		seq.addStep(query);
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p.add(new ProcessorStructureRetrieval());		
		p.add(new FPStructureWriter());
		
		DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
		batch.setProcessorChain(p);
		ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap = 
			new ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
				DBWorkflowContext.QUERY,
				DBWorkflowContext.BATCHSTATS,
				batch,false);
	    ap.setName("Generate Structure Fingerprints");	
	    seq.addStep(ap);
	    
	    
	    ActivityPrimitive<AmbitUser, String> q = new ActivityPrimitive<AmbitUser, String>(
	    		new AbstractUpdateProcessor<AmbitUser, String>(OP.CREATE,new CreateQLabelPair())
	    		);
	    q.setName("Generate quality labels for pairs of structures");
	    seq.addStep(q);
	    
	    ActivityPrimitive m = new ActivityPrimitive(DBWorkflowContext.BATCHSTATS,DBWorkflowContext.BATCHSTATS,
	    		new QualityStatisticsProcessor()
	    );
	    seq.addStep(m);
	    return seq;
}		
	
	
	protected static Sequence addStructureQualityVerifier() {
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
/*
 * TODO
	protected static Sequence addStructureType() {
		Sequence seq = new Sequence();
		Primitive query = new Primitive(DBWorkflowContext.QUERY,DBWorkflowContext.QUERY,
				new Performer() {
			@Override
			public Object execute() throws Exception {
				return new QueryDataset((SourceDataset)null);  //without quality labels
			}
		});
		seq.addStep(query);
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p.add(new ProcessorStructureRetrieval());		
		p.add(new StructureTypeProcessor());
		
		DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
		batch.setProcessorChain(p);
		ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap = 
			new ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
				DBWorkflowContext.QUERY,
				DBWorkflowContext.BATCHSTATS,
				batch,false);
	    ap.setName("Structure type");	
	    seq.addStep(ap);
	    return seq;
   }	
   */
	protected static Sequence addValueQualityVerifier() {
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
	protected static Sequence addValueLabelRemover() {
		Sequence seq = new Sequence();
		
		QLabel label = new QLabel(null);
		label.setUser(new AmbitUser("quality"));
		DeleteValueQLabel query = new DeleteValueQLabel();
		query.setGroup(null);
		query.setObject(label);
		AbstractUpdateProcessor<Integer, QLabel> processor = new AbstractUpdateProcessor<Integer,QLabel>();
		processor.setQueryDelete(query);
		processor.setOperation(OP.DELETE);
		
		seq.addStep(new ActivityPrimitive<Integer, QLabel>(processor));
//		ap.setName("Remove property quality labels");	

	    return seq;
	}	
	protected static Sequence addStructureLabelRemover() {
		Sequence seq = new Sequence();
		
		QLabel label = new QLabel(null);
		label.setUser(new AmbitUser("quality"));
		DeleteStructureQLabel query = new DeleteStructureQLabel();
		query.setGroup(null);
		query.setObject(label);
		AbstractUpdateProcessor<IStructureRecord, QLabel> processor = new AbstractUpdateProcessor<IStructureRecord,QLabel>();
		processor.setQueryDelete(query);
		processor.setOperation(OP.DELETE);
		
		seq.addStep(new ActivityPrimitive<IStructureRecord, QLabel>(processor));
//		ap.setName("Remove property quality labels");	

	    return seq;
	}
	@Override
	public String toString() {
		return "Structure quality";
	}
}
class ModeCondition extends TestCondition {
	StructureQualityWorkflow.QUALITY_MODE mode;
	public ModeCondition(StructureQualityWorkflow.QUALITY_MODE mode) {
		this.mode = mode;
	}
	@Override
	public boolean evaluate() {
			Object o = getContext().get(StructureQualityWorkflow.SELECTION_QUALITY);
			if (o instanceof SelectionBean)
				return mode.equals(((SelectionBean)o).getSelected());
			else return false;
	}
	
}

