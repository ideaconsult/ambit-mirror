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

package ambit2.workflow.library;

import ambit2.db.SourceDataset;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;

import com.microworkflow.process.Activity;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.WorkflowContext;

public class DatasetSelection extends Sequence {
	
	public DatasetSelection(Activity onSuccess, WorkflowContext context) {
        this(onSuccess,getInitialDataset(context));
	}
	protected static SourceDataset getInitialDataset(WorkflowContext context) {
    	Object ol = context.get(DBWorkflowContext.DATASET);
    	if ((ol == null) || !(ol instanceof SourceDataset)) {
    		ol = new SourceDataset();
    	}        
    	return (SourceDataset)ol;
	}
	public DatasetSelection(Activity onSuccess) {
		this(onSuccess,new SourceDataset());
	}
	public DatasetSelection(Activity onSuccess, SourceDataset dataset) {
        
        UserInteraction<SourceDataset> input = new UserInteraction<SourceDataset>(
        		dataset,DBWorkflowContext.DATASET,"Dataset configuration");
        input.setName("Dataset configuration");

        Conditional verify = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
                    	Object object = context.get(DBWorkflowContext.DATASET);
                    	if ((object != null) && (object instanceof SourceDataset)) {
                    		return true; 
                    	} 
                    	return false;
                    }
                }, 
                onSuccess,
                null);
        verify.setName("Verify dataset");
        setName("[Dataset configuration]");
        
        addStep(input);
        addStep(verify);
        
	}	
}
