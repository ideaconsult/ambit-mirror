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
import ambit2.base.data.SelectionBean;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.DbReader;
import ambit2.db.processors.BitSetGenerator;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.processors.FP1024Writer.FPTable;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.MissingFingerprintsQuery;
import ambit2.db.update.qlabel.smarts.SMARTSAcceleratorWriter;
import ambit2.smarts.processors.SMARTSPropertiesGenerator;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBProcessorPerformer;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;
import ambit2.workflow.calculation.CalculationDescriptors;
import ambit2.workflow.calculation.CalculationFingerprints;
import ambit2.workflow.calculation.CalculationSmartsData;
import ambit2.workflow.calculation.CalculationStructuralKeys;
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
			@Override
			public Sequence getSequence() {
				return new CalculationFingerprints();
			}
		},
		StructuralKeys {
			@Override
			public String toString() {
				return "Structural keys (used to speed up SMARTS searching)";
			}
			@Override
			public Sequence getSequence() {
				return new CalculationStructuralKeys();
			}			
		},
		SMARTSAccelerator {
			@Override
			public String toString() {
				return "SMARTS accelerator data";
			}
			@Override
			public Sequence getSequence() {
				return new CalculationSmartsData();
			}
		},	
		Descriptors {
			@Override
			public String toString() {
				return "Descriptors";
			}
			@Override
			public Sequence getSequence() {
				return new CalculationDescriptors();
			}			
		},

		Completed {
			@Override
			public String toString() {
				return "Quit, calculations are completed.";
			}		
			@Override
			public Sequence getSequence() {
				return null;
			}
		};
		public abstract Sequence getSequence();
	};
	public DBUtilityWorkflow() {
        
        Sequence seq=new Sequence();
        seq.setName("[Calculator]");    	
        seq.addStep(getCalculationOptionsSequence());

        
        //setDefinition(new LoginSequence(new DatasetSelection(seq)));
        setDefinition(new LoginSequence(seq));
     

	}
	public While getCalculationOptionsSequence() {
		
		
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
                CALC_MODE.SMARTSAccelerator.getSequence(),
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
                CALC_MODE.Descriptors.getSequence(),
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
                CALC_MODE.StructuralKeys.getSequence(),
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
                CALC_MODE.Fingerprints.getSequence(),
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
	
	@Override
	public String toString() {
	return "Database utility";
	}
}

