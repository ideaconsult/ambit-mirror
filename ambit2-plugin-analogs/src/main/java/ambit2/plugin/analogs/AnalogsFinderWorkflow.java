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

package ambit2.plugin.analogs;

import ambit2.db.IDBProcessor;
import ambit2.db.processors.DBProcessorsChain;
import ambit2.db.processors.ProcessorCreateQuery;
import ambit2.db.processors.QueryInfo2Query;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryInfo;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class AnalogsFinderWorkflow extends Workflow {
	public AnalogsFinderWorkflow() {
        Sequence seq=new Sequence();
        seq.setName("Advanced search");    	
        

        DBProcessorsChain<QueryInfo, IStoredQuery,IDBProcessor> chain = new DBProcessorsChain<QueryInfo, IStoredQuery,IDBProcessor>();
        chain.add(new QueryInfo2Query());
        chain.add(new ProcessorCreateQuery());
    	//ExecuteAndStoreQuery p1 = new ExecuteAndStoreQuery();
    	ActivityPrimitive<IQueryObject,IStoredQuery> p1 = new ActivityPrimitive<IQueryObject,IStoredQuery>( 
    			DBWorkflowContext.QUERY,
    			DBWorkflowContext.STOREDQUERY,
    				  (IDBProcessor)chain);
    	
    		
	
        p1.setName("Search");    
        //TODO fill in queryinfo
        seq.addStep(new UserInteraction<QueryInfo>(new QueryInfo(),DBWorkflowContext.QUERY,"Define query"));
        seq.addStep(p1);
        setDefinition(new LoginSequence(seq));
	}
	
}
