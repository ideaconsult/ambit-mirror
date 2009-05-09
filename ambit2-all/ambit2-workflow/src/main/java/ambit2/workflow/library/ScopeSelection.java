/* ScopeSelection.java
 * Author: nina
 * Date: Apr 19, 2009
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
import ambit2.db.processors.ProcessorSetQueryScope;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.search.structure.SCOPE;
import ambit2.db.search.structure.ScopeQuery;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;

public class ScopeSelection extends Sequence {
	//protected String SELECTION="QueryExecution.SELECTION";
	public ScopeSelection(SCOPE scope) {
        if (scope == null) {
	        UserInteraction<ScopeQuery> ui_scope = new UserInteraction<ScopeQuery>(
	        		new ScopeQuery(),
	        		DBWorkflowContext.SCOPE,"Search within");
	        addStep(ui_scope);
	        Primitive<ScopeQuery, IQueryRetrieval<IStructureRecord>> setScope = 
	    		new Primitive<ScopeQuery, IQueryRetrieval<IStructureRecord>>(
	    				DBWorkflowContext.SCOPE,DBWorkflowContext.QUERY,
	    				new ScopeSetter());
		    setScope.setName("Set query scope");
		    addStep(setScope);	        
        } else {
	        Primitive<ScopeQuery, IQueryRetrieval<IStructureRecord>> setScope = 
	    		new Primitive<ScopeQuery, IQueryRetrieval<IStructureRecord>>(
	    				DBWorkflowContext.SCOPE,DBWorkflowContext.QUERY,
	    				new ScopeSetter(scope));
		    setScope.setName(scope.toString());
		    addStep(setScope);        	
        }
        
	}
	public ScopeSelection() {
       this(null); 		
	}
	/*
	
	public ScopeSelection() {
        
		SelectionBean<SCOPE> scope = new SelectionBean<SCOPE>(SCOPE.values(),"Search within");
        UserInteraction<SelectionBean<SCOPE>> ui_scope = new UserInteraction<SelectionBean<SCOPE>>(
        		scope,
        		SELECTION,"Search within");
        addStep(ui_scope);

        Primitive<SelectionBean<SCOPE>, IQueryRetrieval<IStructureRecord>> p = 
    		new Primitive<SelectionBean<SCOPE>, IQueryRetrieval<IStructureRecord>>(
    				SELECTION,DBWorkflowContext.SCOPE,
    				new Performer<SelectionBean<SCOPE>, IQueryRetrieval<IStructureRecord>>() {
    					@Override
    					public IQueryRetrieval<IStructureRecord> execute()
    							throws Exception {
    						switch (getTarget().getSelected()) {
    						case scope_entiredb: {
    							return null;
    						}
    						case scope_last_results: {
    							try {
    								IStoredQuery q = (IStoredQuery)getContext().get(DBWorkflowContext.STOREDQUERY);
    								if (q.getId()>0) {
    									QueryStoredResults qr = new QueryStoredResults();
    									qr.setFieldname(q);
    									return qr;
    								} else return null; 
    							} catch (Exception x) {
    								return null;
    							}
    						}
    						default: return null;
    						}
    					}
    				}
    				);
        addStep(p);
        p.setName("Set query scope");
        Primitive<IQueryRetrieval<IStructureRecord>, IQueryRetrieval<IStructureRecord>> setScope = 
        		new Primitive<IQueryRetrieval<IStructureRecord>, IQueryRetrieval<IStructureRecord>>(
        				DBWorkflowContext.SCOPE,DBWorkflowContext.QUERY,
        				new ScopeSetter());
        setScope.setName("Set query scope");
        addStep(setScope);
	}
	*/
}

class ScopeSetter extends Performer<ScopeQuery, IQueryRetrieval<IStructureRecord>> {
	protected ProcessorSetQueryScope processor = new ProcessorSetQueryScope();
	protected ScopeQuery defaultScope = null;
	public ScopeSetter() {
		this(null);
	}
	public ScopeSetter(SCOPE scope) {
		if (scope != null) {
			defaultScope = new ScopeQuery();
			defaultScope.setFieldname(scope);
		}
	}
	@Override
	public IQueryRetrieval<IStructureRecord> execute() throws Exception {

		ScopeQuery query = (defaultScope!=null)?defaultScope:getTarget();

		if (query.getFieldname().equals(SCOPE.scope_last_results)) 
			try {
				IStoredQuery q = (IStoredQuery)getContext().get(DBWorkflowContext.STOREDQUERY);
				if (q==null) query.setFieldname(SCOPE.scope_entiredb);
				else 
					if (q.getId()>0) {
						QueryStoredResults qr = new QueryStoredResults();
						qr.setFieldname(q);
						qr.setValue(true);
						query.setValue(qr);
					}  
			} catch (Exception x) {
				x.printStackTrace();
			}	
		
		processor.setQuery((IQueryRetrieval<IStructureRecord>)getContext().get(DBWorkflowContext.QUERY));
		
		return processor.process(query);
	}
}