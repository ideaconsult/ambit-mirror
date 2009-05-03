/* SearchSequence.java
 * Author: nina
 * Date: Apr 14, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.workflow.library;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.ExecuteAndStoreQuery;
import ambit2.workflow.QueryInteraction;
import ambit2.workflow.UserInteraction;

import com.microworkflow.process.Sequence;

/**
 * Expects a query in getContext(DBWorkflowContext.QUERY) and tries to execute it.
 * The result is into getContext(DBWorkflowContext.STOREDQUERY), which is displayed by QuerResultsPanel  
 * @author nina
 *
 */
public class QueryExecution extends Sequence {
	public QueryExecution(IQueryRetrieval<IStructureRecord> query) {
		this(query,null);
	}
	public QueryExecution(IQueryRetrieval<IStructureRecord> query, String resultTag) {
       
        addStep(new QueryInteraction(query));
        addStep(new ScopeSelection());
    	ExecuteAndStoreQuery p1 = new ExecuteAndStoreQuery(resultTag);
        p1.setName("Search");           
		addStep(p1);
		/*
        UserInteraction<Boolean> browse = new UserInteraction<Boolean>(
        		true,DBWorkflowContext.USERCONFIRMATION,"CONTINUE","Browse results");
        addStep(browse);
	*/
	}
	@Override
	public String toString() {
		return "Browse results";
	}
}
