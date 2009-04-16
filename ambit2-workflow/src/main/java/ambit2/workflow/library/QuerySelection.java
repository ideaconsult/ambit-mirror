/* QuerySelection.java
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

import nplugins.core.Introspection;
import ambit2.base.data.ClassHolder;
import ambit2.base.data.SelectionBean;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;

/**
 * Displays a set of available queries and then returns the selected one in DBWorkflowContext.QUERY
 * Normally should be followed by {@link QueryExecution}
 * @author nina
 *
 */
public class QuerySelection extends Sequence {
	protected static final String SELECTION="QuerySelection.SELECTION";
	public QuerySelection() {
		SelectionBean<ClassHolder> selection = new SelectionBean<ClassHolder>(
				new ClassHolder[] {
						new ClassHolder("ambit2.db.search.structure.QueryField","Text properties (CAS, Name, etc.)","",""),
						new ClassHolder("ambit2.db.search.structure.QueryFieldNumeric","Numeric properties (Descriptors, measured data, IDs)","",""),
						new ClassHolder("ambit2.db.search.structure.QueryDataset","Dataset","Retrieve all the compounds from a dataset",""),
						new ClassHolder("ambit2.db.search.structure.QueryStructure","SMILES, Molecular formula, InChI","Search by SMILES, Inchi, Formula",""),
						new ClassHolder("ambit2.db.search.structure.QuerySimilarityStructure","Similarity, substructure, exact structure","Search for similar structures",""),
						new ClassHolder("ambit2.db.search.structure.QueryStoredResults","Previous search results","Display results from previous queries",""),						
				},"Search by"
				);

        addStep(new UserInteraction<SelectionBean<ClassHolder>>(
        		selection,SELECTION,"Select type of query")
        		);
       
        Performer<SelectionBean<ClassHolder>,AbstractStructureQuery> performer = new Performer<SelectionBean<ClassHolder>,AbstractStructureQuery>() {
    		public AbstractStructureQuery execute() throws Exception {
    			
    			ClassHolder ch = getTarget().getSelected();
    			Object o = Introspection.loadCreateObject(ch.getClazz());
    			if (o instanceof AbstractStructureQuery)
    				return (AbstractStructureQuery)o;
    			else throw new Exception(o.getClass().getName() + " not expected");
    		}        	
        };
    	Primitive<SelectionBean<ClassHolder>,AbstractStructureQuery> p = 
    		new Primitive<SelectionBean<ClassHolder>,AbstractStructureQuery>( 
    			SELECTION,
    			DBWorkflowContext.QUERY,performer);
    	p.setName("Assign selected query type");
    	addStep(p);
	}
	@Override
	public String toString() {
		return "Select type of query";
	}
}
