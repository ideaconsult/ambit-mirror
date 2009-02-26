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

package ambit2.plugin.pbt;

import ambit2.plugin.pbt.PBTWorkBook.WORKSHEET_INDEX;

import com.microworkflow.process.Activity;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.Workflow;

public class PBTWorkflow extends Workflow {
	public PBTWorkflow() {
		
        Sequence seq=new Sequence();
        seq.setName(toString());
        Activity prev = null;
        for (int i=WORKSHEET_INDEX.values().length-1;i>0;i--) {
        	TestCondition test = new WorksheetCompleted(WORKSHEET_INDEX.values()[i]);
        	Conditional c = new Conditional(test,prev);
        	c.setName(WORKSHEET_INDEX.values()[i].toString());
        	prev = c;
       	
        }
        seq.addStep(prev);
        setDefinition(seq);        	

		}
	@Override
	public String toString() {
		return "PBT";
	}
}

class WorksheetCompleted extends TestCondition {
	WORKSHEET_INDEX index = WORKSHEET_INDEX.SUBSTANCE;
	public WorksheetCompleted(WORKSHEET_INDEX index) {
		this.index = index;
	}
	@Override
	public boolean evaluate() {
		Object o = getContext().get(PBTWorkBook.PBT_WORKBOOK);
		if ((o==null) || !(o instanceof PBTWorkBook)) return false;
		return ((PBTWorkBook)o).isCompleted(index);
		
	}
}
