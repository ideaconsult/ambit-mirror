/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.plugin.dbtools;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.SelectionBean;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReader;
import ambit2.db.processors.BitSetGenerator;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.processors.ProcessorMissingDescriptorsQuery;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.processors.FP1024Writer.FPTable;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.MissingFingerprintsQuery;
import ambit2.db.update.qlabel.smarts.SMARTSAcceleratorWriter;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.smarts.processors.SMARTSPropertiesGenerator;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBProcessorPerformer;
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

public class DBUtilityWorkflow extends Workflow {
	protected static final String SELECTION_CALC="SELECTION_CALC";
	protected static final String DESCRIPTORS_NEWQUERY = "ambit2.plugin.dbtools.DBUtilityWorkflow.DESCRIPTORS_NEWQUERY";
	protected enum CALC_MODE {
		Fingerprints {
			@Override
			public String toString() {
				return "Fingerprints (1024 bit hashed fingerprints used for similarity search and prescreening)";
			}
		},
		StructuralKeys {
			@Override
			public String toString() {
				return "Structural keys (used to speed up SMARTS searching)";
			}
		},
		SMARTSAccelerator {
			@Override
			public String toString() {
				return "SMARTS accelerator data";
			}
		},	
		Descriptors {
			@Override
			public String toString() {
				return "Descriptors";
			}
			
		},

		Completed {
			@Override
			public String toString() {
				return "Quit, calculations are completed.";
			}		
		}
	};
	public DBUtilityWorkflow() {
		//fingerprints
		Sequence fingerprints = addCalculationFP();

		//struc keys
		Sequence strucKeys = addCalculationStructuralKeys();
		Sequence smartsAccelerator = addCalculationSMARTSData();
		
		//descriptors
		Sequence descriptorSequence = new Sequence();
	    DescriptorsFactory factory = new DescriptorsFactory();
	    Profile<Property> descriptors;
	    try {
	    	descriptors = factory.process(null);
	    } catch (Exception x) {
	    	x.printStackTrace();
	    	descriptors = new Profile<Property>();
	    }
	    UserInteraction<Profile<Property>> defineDescriptors = new UserInteraction<Profile<Property>>(
        		descriptors,
        		DBWorkflowContext.DESCRIPTORS,
        		"Select descriptor(s)");	
	    descriptorSequence.addStep(defineDescriptors);
        Primitive q = new Primitive(DBWorkflowContext.DESCRIPTORS,DESCRIPTORS_NEWQUERY,new Performer() {
        	@Override
        	public Object execute() throws Exception {
                //QueryDataset q = new QueryDataset("Default");
        		ProcessorMissingDescriptorsQuery p = new ProcessorMissingDescriptorsQuery();
        		//TODO set scope - dataset
        		//scope - entire db, query, dataset!! to be used elsewhere
        		return p.process((Profile)getTarget());
        	}
        	@Override
        	public String toString() {
        
        		return "Select descriptors";
        	}
        	
        });
        q.setName("Select descriptor(s)");
        descriptorSequence.addStep(q);	    
        descriptorSequence.addStep(addCalculationD());        
        
        Sequence seq=new Sequence();
        seq.setName("[Calculator]");    	
        seq.addStep(getCalculationOptionsSequence(
        		descriptorSequence,
        		fingerprints,
        		strucKeys,
        		smartsAccelerator
        		));
        
        //setDefinition(new LoginSequence(new DatasetSelection(seq)));
        setDefinition(new LoginSequence(seq));
     

	}
	public While getCalculationOptionsSequence(
				Activity descriptors, 
				Activity fingerprints, 
				Activity struckeys,
				Activity smartsAccelerator) {
		
		
		While loop = new While();
		Sequence body = new Sequence();
		final SelectionBean<CALC_MODE> selection = new SelectionBean<CALC_MODE>(
				CALC_MODE.values(),
				"Calculate");

		UserInteraction<SelectionBean<CALC_MODE>> selectKey = new UserInteraction<SelectionBean<CALC_MODE>>(
				selection, SELECTION_CALC, "??????");
		selectKey.setName("Select what to calculate");

        Conditional smartsCondition = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				Object o = getContext().get(SELECTION_CALC);
	    				if (o instanceof SelectionBean)
	    					return CALC_MODE.SMARTSAccelerator.equals(((SelectionBean)o).getSelected());
	    				else return false;
                    }
                }, 
                smartsAccelerator,
                null);
        
        Conditional descriptorCondition = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				Object o = getContext().get(SELECTION_CALC);
	    				if (o instanceof SelectionBean)
	    					return CALC_MODE.Descriptors.equals(((SelectionBean)o).getSelected());
	    				else return false;
                    }
                }, 
                descriptors,
                smartsCondition);
        
        Conditional skeysCondition = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				Object o = getContext().get(SELECTION_CALC);
	    				if (o instanceof SelectionBean)
	    					return CALC_MODE.StructuralKeys.equals(((SelectionBean)o).getSelected());
	    				else return false;
                    }
                }, 
                struckeys,
                descriptorCondition);
        
        Conditional fingerprintsCondition = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				Object o = getContext().get(SELECTION_CALC);
	    				if (o instanceof SelectionBean)
	    					return CALC_MODE.Fingerprints.equals(((SelectionBean)o).getSelected());
	    				else return false;
                    }
                }, 
                fingerprints,
                skeysCondition);
        
        loop.setTestCondition(new TestCondition() {
        	@Override
        	public boolean evaluate() {
    			Object o = getContext().get(SELECTION_CALC);
    			boolean result = (o instanceof SelectionBean)? 
    				!CALC_MODE.Completed.equals(((SelectionBean<CALC_MODE>)o).getSelected())
    				:false;
    			
    			getContext().put(SELECTION_CALC, null);
    			return result;
        	}
        });
        
        loop.setName("Calculate");	            
        body.addStep(selectKey);
        body.addStep(fingerprintsCondition);
        loop.setBody(body);
		return loop;
	}	
	protected Sequence addCalculationFP() {
			Sequence seq = new Sequence();
			Primitive query = new Primitive(DBWorkflowContext.QUERY,DBWorkflowContext.QUERY,
					new Performer() {
				@Override
				public Object execute() throws Exception {
					return new MissingFingerprintsQuery();
				}
			});
			seq.addStep(query);
			ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
				new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
			p.add(new ProcessorStructureRetrieval());		
			p.add(new BitSetGenerator(FPTable.fp1024));
			p.add(new FP1024Writer());
			DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
			batch.setProcessorChain(p);
			ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap = 
				new ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
					DBWorkflowContext.QUERY,
					DBWorkflowContext.BATCHSTATS,
					batch,false);
		    ap.setName("Fingerprint calculations");	
		    seq.addStep(ap);
		    return seq;
	}	
	protected Sequence addCalculationStructuralKeys() {
		Sequence seq = new Sequence();
		Primitive query = new Primitive(DBWorkflowContext.QUERY,DBWorkflowContext.QUERY,
				new Performer() {
			@Override
			public Object execute() throws Exception {
				return new MissingFingerprintsQuery(FPTable.sk1024);
			}
		});
		seq.addStep(query);		
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p.add(new ProcessorStructureRetrieval());		
		p.add(new BitSetGenerator(FPTable.sk1024));
		p.add(new FP1024Writer(FPTable.sk1024));
		DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
		batch.setProcessorChain(p);
		ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap = 
			new ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
				DBWorkflowContext.QUERY,
				DBWorkflowContext.BATCHSTATS,
				batch,false);
	    ap.setName("Structural keys calculations");
	    seq.addStep(ap);
	    return seq;
}		
	
	protected Sequence addCalculationSMARTSData() {
		Sequence seq = new Sequence();
		Primitive query = new Primitive(DBWorkflowContext.QUERY,DBWorkflowContext.QUERY,
				new Performer() {
			@Override
			public Object execute() throws Exception {
				return new MissingFingerprintsQuery(FPTable.smarts_accelerator);
			}
		});
		seq.addStep(query);		
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p.add(new ProcessorStructureRetrieval());		
		p.add(new SMARTSPropertiesGenerator());
		p.add(new SMARTSAcceleratorWriter());
		DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
		batch.setProcessorChain(p);
		ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap = 
			new ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
				DBWorkflowContext.QUERY,
				DBWorkflowContext.BATCHSTATS,
				batch,false);
	    ap.setName("SMARTS properties");
	    seq.addStep(ap);
	    return seq;
}		
	//TODO extract in a class and reuse in other workflows
	protected Primitive addCalculationD() {

		final DescriptorsCalculator calculator = new DescriptorsCalculator();
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p1.add(new ProcessorStructureRetrieval());		
		p1.add(calculator);
		
		DbReader<IStructureRecord> batch1 = new DbReader<IStructureRecord>();
		batch1.setProcessorChain(p1);
		
		DBProcessorPerformer<DbReader<IStructureRecord>,IQueryRetrieval<IStructureRecord>,IBatchStatistics> performer = 
			new DBProcessorPerformer<DbReader<IStructureRecord>,IQueryRetrieval<IStructureRecord>,IBatchStatistics>(batch1,false) {
			public IBatchStatistics execute() throws Exception {
				Object o = getContext().get(DBWorkflowContext.DESCRIPTORS);
				calculator.setDescriptors((Profile)o);
				return super.execute();
			}
		};	
		
		Primitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap1 = 
			new Primitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
				DESCRIPTORS_NEWQUERY,
				DBWorkflowContext.BATCHSTATS,
				performer);
	    ap1.setName("Descriptor calculations");		
	    return ap1;
	}
	@Override
	public String toString() {
	return "Database utility";
	}
}

