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

package ambit2.workflow;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import ambit2.repository.StructureRecord;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Workflow;
import com.microworkflow.ui.IWorkflowFactory;

public class WorkflowFactory extends Hashtable <String,Workflow> implements IWorkflowFactory {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6456499162628345432L;
	protected String theTask;

	public WorkflowFactory() {
        Primitive test = new Primitive("Query", "Result",
                new Performer() {
                    public Object execute() {

                        List<StructureRecord> records = new ArrayList<StructureRecord>();
                        for (int i=0; i < 100000; i++)
                        	records.add(new StructureRecord(i,i,null,null));
                        return records;
                    }
                });
        test.setName("Test");
        theTask = "test";
        Workflow wf = new Workflow();
        wf.setDefinition(test);
        put(theTask,wf);
	}
	public Workflow getWorkflow() {
		return get(theTask);
	}
	public String getTheTask() {
		return theTask;
	}

	public void setTheTask(String theTask) {
		this.theTask = theTask;
	}
	
}


