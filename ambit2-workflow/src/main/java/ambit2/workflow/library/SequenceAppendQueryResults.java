/* SequenceAppendQueryResults.java
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.IRetrieval;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;

/**
 * Appends IStructureRecords from the query into List<IStructureRecord>
 * @author nina
 *
 */
public class SequenceAppendQueryResults extends Sequence {
	public SequenceAppendQueryResults(String queryTag, boolean all) {

		/*
		 * Creates QueryStoredResults from a IStoredQuery
		 */
		Primitive<IStoredQuery, IQueryRetrieval<IStructureRecord>> query = new Primitive<IStoredQuery, IQueryRetrieval<IStructureRecord>>(
				queryTag,
				DBWorkflowContext.QUERY,
				new Performer<IStoredQuery, IQueryRetrieval<IStructureRecord>>() {
					@Override
					public IQueryRetrieval<IStructureRecord> execute() throws Exception {
						QueryStoredResults q = new QueryStoredResults();
						q.setFieldname(getTarget());
						return q;
					}
				}
				) {
			@Override
			public synchronized String getName() {
				return "Append records";
			};
		};	
		
		addStep(query);
		
		if (all) {
			QueryPerformer<IQueryRetrieval<IStructureRecord>, List<IStructureRecord>, IStructureRecord> performer = 
						new QueryPerformer<IQueryRetrieval<IStructureRecord>, List<IStructureRecord>, IStructureRecord>() {
				
				@Override
				protected List<IStructureRecord> process(
						IQueryRetrieval<IStructureRecord> query, ResultSet resultset)
						throws Exception {
								return null;
				}
				@Override
				protected List<IStructureRecord> retrieve(
						IRetrieval<IStructureRecord> query, ResultSet resultset)
						throws Exception {
		   			Object o = getContext().get(getResultKey());
	
	    			List<IStructureRecord> records = null;
	    			if ((o==null) || !(o instanceof List)) {
	    				records = new ArrayList<IStructureRecord>();
	    			} else records = (List<IStructureRecord>)o;
	    			
	    			RetrieveStructure struc = new RetrieveStructure();
	    			QueryExecutor<RetrieveStructure> q = new QueryExecutor<RetrieveStructure> ();
	    			Connection c = getConnection();
	    			try {
		    			q.setConnection(c);
		    			q.open();
	    			
				        while (resultset.next()) 
				        	try {
				        		if (resultset.getBoolean("selected")) {
					        		struc.setValue(query.getObject(resultset));
					        		ResultSet rs = q.process(struc);
					        		while (rs.next())
					        			records.add((IStructureRecord)struc.getObject(rs).clone());
					        		q.closeResults(rs);
				        		}
				        	} catch (Exception x) {
				        		x.printStackTrace();
				        	};
	    			} finally {
	    				try {q.close();c.close();} catch (Exception x) {}
	    			}
			        getContext().put(DBWorkflowContext.RECORD,null);
			        return records;
				}
			};
			
			Primitive<IQueryRetrieval<IStructureRecord>, List<IStructureRecord>> retrieveStrucs = new Primitive<IQueryRetrieval<IStructureRecord>, List<IStructureRecord>>(
					DBWorkflowContext.QUERY,
					DBWorkflowContext.RECORDS,
					performer
					) {
				@Override
				public synchronized String getName() {
					return "Append records";
				};
			};
			
			addStep(retrieveStrucs);
		} else {
			
				QueryPerformer<IQueryRetrieval<IStructureRecord>, IStructureRecord, IStructureRecord> performer = 
					new QueryPerformer<IQueryRetrieval<IStructureRecord>, IStructureRecord, IStructureRecord>() {
			
					@Override
					protected IStructureRecord process(
							IQueryRetrieval<IStructureRecord> query, ResultSet resultset)
							throws Exception {
									return null;
					}
					@Override
					protected IStructureRecord retrieve(
							IRetrieval<IStructureRecord> query, ResultSet resultset)
							throws Exception {
			   			Object o = getContext().get(getResultKey());
						IStructureRecord record = null;
						
						RetrieveStructure struc = new RetrieveStructure();
						QueryExecutor<RetrieveStructure> q = new QueryExecutor<RetrieveStructure> ();
						Connection c = getConnection();
						try {
			    			q.setConnection(c);
			    			q.open();
						
					        while (resultset.next()) 
					        	try {
					        		if (resultset.getBoolean("selected")) {
						        		struc.setValue(query.getObject(resultset));
						        		ResultSet rs = q.process(struc);
						        		while (rs.next()) {
						        			record = (IStructureRecord)struc.getObject(rs).clone();
						        			break;
						        		}
						        		q.closeResults(rs);
					        		}
					        	} catch (Exception x) {
					        		x.printStackTrace();
					        	};
						} finally {
							try {q.close();c.close();} catch (Exception x) {}
						}

				        getContext().put(DBWorkflowContext.RECORD,null);
				        return record;
					}
				};			
			Primitive<IQueryRetrieval<IStructureRecord>, IStructureRecord> retrieveStrucs = new Primitive<IQueryRetrieval<IStructureRecord>, IStructureRecord>(
					DBWorkflowContext.QUERY,
					DBWorkflowContext.RECORD,
					performer
					) {
				@Override
				public synchronized String getName() {
					return "Append records";
				};
			};			
			addStep(retrieveStrucs);
		}

	
	
	}

}
