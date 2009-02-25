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

import ambit2.db.search.QueryField;
import ambit2.workflow.ExecuteAndStoreQuery;
import ambit2.workflow.QueryInteraction;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class PBTWorkflow extends Workflow {
	public PBTWorkflow() {
		
    	ExecuteAndStoreQuery p1 = new ExecuteAndStoreQuery();
        p1.setName("Search");    
        Sequence seq=new Sequence();
        seq.setName("Substance search");
        seq.addStep(new QueryInteraction(new QueryField()));
		seq.addStep(p1);
				
		/*
        seq.setName("PBT Assessment");
        String[] pbt = new String[] {"Substance definition","Persistence","Bioaccumulation","Toxicity","Results"};
        for (String a : pbt) {
        	Primitive p = new Primitive(new Performer(){
        		public Object execute() throws Exception { return null;};
        	});
        	p.setName(a);
        	seq.addStep(p);
        }
        */
        setDefinition(seq);        	
        


		setDefinition(new LoginSequence(seq));
	
		}
	@Override
	public String toString() {
		return "PBT";
	}
}
