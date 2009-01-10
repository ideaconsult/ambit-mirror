/* FindSimilarSubstances.java
 * Author: nina
 * Date: Dec 28, 2008
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
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

package ambit2.plugin.analogs;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.core.data.Profile;
import ambit2.core.io.Property;
import ambit2.db.IDBProcessor;
import ambit2.db.SourceDataset;
import ambit2.db.processors.DBProcessorsChain;
import ambit2.db.processors.ProcessorCreateQuery;
import ambit2.db.processors.QueryInfo2Query;
import ambit2.db.readers.IRetrieval;
import ambit2.db.readers.RetrieveDatasets;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryInfo;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;

import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;

/**
 * A placeholder for finding similar substances
 * @author nina
 *
 */
public class FindSimilarSubstances extends Sequence {
	public FindSimilarSubstances() {
		super();
		Primitive<IQueryObject, QueryInfo> retrieve = new Primitive<IQueryObject, QueryInfo>(
				DBWorkflowContext.STRUCTURES,
				DBWorkflowContext.QUERY,
				new QueryInitialization()
				) {
			@Override
			public synchronized String getName() {
				return "Query initialization";
			};		
		};
		
        DBProcessorsChain<QueryInfo, IStoredQuery,IDBProcessor> chain = new DBProcessorsChain<QueryInfo, IStoredQuery,IDBProcessor>();
        chain.add(new QueryInfo2Query());
        chain.add(new ProcessorCreateQuery());
    	ActivityPrimitive<IQueryObject,IStoredQuery> p1 = new ActivityPrimitive<IQueryObject,IStoredQuery>( 
    			DBWorkflowContext.QUERY,
    			DBWorkflowContext.STOREDQUERY,
    				  (IDBProcessor)chain);
        p1.setName("Search");    

        addStep(retrieve);
        addStep(new UserInteraction<QueryInfo>(new QueryInfo(),DBWorkflowContext.QUERY,"Define query"));
        addStep(p1);
	}
}

class QueryInitialization extends QueryPerformer<IQueryObject, QueryInfo,SourceDataset> {
	protected IQueryObject getTarget() {
		return new RetrieveDatasets();
	};
	protected QueryInfo process(IQueryObject query,ResultSet rs) throws Exception {							
		throw new Exception("Not implemented");
	}
	protected QueryInfo retrieve(IRetrieval<SourceDataset> query,
			ResultSet rs) throws Exception {
		QueryInfo q = null;
		if (get(DBWorkflowContext.QUERY)==null) {
			q = new QueryInfo();
			q.setScope(QueryInfo.SCOPE_DATABASE);
		} else {
			q = (QueryInfo) get(DBWorkflowContext.QUERY); 
		}
		q.setMethod(QueryInfo.METHOD_SIMILARITY);
		Object structure = get(DBWorkflowContext.STRUCTURES);
		if ((structure != null) && (structure instanceof IMolecule))
			q.setMolecule((IMolecule)structure);
		
		
		//datasets
		ArrayList<SourceDataset> datasets = new ArrayList<SourceDataset>();
        while (rs.next()) {
        	datasets.add(query.getObject(rs));
        };
        if (datasets.size()>0) {
	        SourceDataset[] ds = new SourceDataset[datasets.size()];
	        QueryInfo.setDatasets(datasets.toArray(ds));
        }
        
        //identifiers
		
        if (get(DBWorkflowContext.PROFILE) != null) {
        	Profile profile = (Profile) get(DBWorkflowContext.PROFILE);
            String[] fn = new String[profile.size()];
            Iterator<Property> p = profile.values().iterator();
            int i=0;
            while (p.hasNext()) {
            	fn[i] = p.next().getName();
            	i++;
            }
            	
            QueryInfo.setFieldnames(fn);
        }
        
        return q;
	};	
}
