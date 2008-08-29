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

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.ValueLatchPair;

public class DatasetSelection extends Primitive {
	public DatasetSelection() {
		super(DBWorkflowContext.DATASET,DBWorkflowContext.DATASET,new Performer() {
	            @Override
	            public Object execute() {
	                Object o = getTarget();
	                if (o == null) {
	                    ValueLatchPair<SourceDataset> latch = new ValueLatchPair<SourceDataset>(new SourceDataset());
	                    context.put(DBWorkflowContext.USERINTERACTION,latch);
	                    //This is a blocking operation!
	                    try {
	                        SourceDataset li = latch.getLatch().getValue();
	                        context.put(DBWorkflowContext.USERINTERACTION,null);
	                        return li;
	                    } catch (InterruptedException x) {
	                        context.put(DBWorkflowContext.ERROR, x);
	                        context.put(DBWorkflowContext.USERINTERACTION,null);
	                        return null;
	                    }
	                    
	                } else return o;    
	            }
	        }); 
	    setName("datasource");   
	}
	
}
