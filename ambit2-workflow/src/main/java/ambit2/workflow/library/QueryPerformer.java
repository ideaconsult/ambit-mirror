/* QueryPerformer.java
 * Author: nina
 * Date: Dec 29, 2008
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

package ambit2.workflow.library;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import ambit2.db.readers.IRetrieval;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.QueryExecutor;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.execution.Performer;

/**
 * A {@link Performer}, which executes an {@link IQueryObject}.
 * The query resultset can be processed via {@link #retrieve(IRetrieval, ResultSet)}.
 * @author nina
 *
 * @param <Q> Thequery object
 * @param <Result>  The result
 * @param <RecordValue> value, retrieved from each record 
 */
public abstract class QueryPerformer<Q extends IQueryObject,Result,RecordValue> extends Performer<Q,Result> {

	protected QueryExecutor<IQueryObject> queryExecutor;

	public QueryPerformer() {
		queryExecutor = new QueryExecutor<IQueryObject>();
	}
	@Override
	public Result execute() throws Exception {
		IQueryObject query = getTarget();
		if (query == null)
			throw new Exception("Undefined query");
     	DataSource datasource = (DataSource)get(DBWorkflowContext.DATASOURCE);
		if (datasource == null)
			throw new Exception("Undefined datasource");
        Connection c = datasource.getConnection();     
		if (datasource == null)
			throw new Exception("Undefined db connection");        
        queryExecutor.setConnection(c);            
        queryExecutor.open();
        Result result = null;
        try {
	        ResultSet resultSet = queryExecutor.process(query);
	        if (query instanceof IRetrieval)
	        	result = retrieve((IRetrieval<RecordValue>) query, resultSet);
	        else
	        	result = process(query,resultSet);
	        resultSet.close();
	        return result;
        } catch (Exception x) {
        	throw new Exception();
        } finally {
            queryExecutor.close();            
            if (!c.isClosed()) c.close();	        	
        }
        
	}
	protected abstract Result process(IQueryObject query,ResultSet resultset) throws Exception ;
	protected abstract Result retrieve(IRetrieval<RecordValue> query,ResultSet resultset) throws Exception ;	

}
