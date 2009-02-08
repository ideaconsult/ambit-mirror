/* DbReader.java
 * Author: nina
 * Date: Feb 8, 2009
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

package ambit2.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.batch.IBatchStatistics;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.QueryExecutor;

public class DbReader<ResultType> extends AbstractBatchProcessor<IQueryRetrieval<ResultType>, ResultType>
								  implements IDBProcessor<IQueryRetrieval<ResultType>, IBatchStatistics>
									{

	protected ResultSet resultSet;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -509334177829567145L;

	public DbReader() {
		// TODO Auto-generated constructor stub
	}
	public Iterator<ResultType> getIterator(final IQueryRetrieval<ResultType> query)
			throws AmbitException {
		QueryExecutor<IQueryObject<ResultType>> exec = new QueryExecutor<IQueryObject<ResultType>>();
		setResultSet(exec.process(query));
		return new Iterator<ResultType>() {
			public boolean hasNext() {
				try {
					return getResultSet().next();
				} catch (SQLException x) {
					onError(null,null,batchStatistics,x);
					return false;
				}
			}
			public ResultType next() {
				try {
					return query.getObject(getResultSet());
				} catch (AmbitException x) {
					onError(null,null,batchStatistics,x);
					return null;
				}
			}
			public void remove() {
				onError(null,null,batchStatistics,new AmbitException("Undefined operation remove"));
				
			}
		};
			
	};
	@Override
	public void afterProcessing(IQueryRetrieval<ResultType> target,
			Iterator<ResultType> iterator) throws AmbitException {
		
		try {
			if (resultSet != null)
				resultSet.close();
		} catch (SQLException x) {

			throw new AmbitException(x);
		}
		super.afterProcessing(target,iterator);
	}
	
	public ResultSet getResultSet() {
		return resultSet;
	}
	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}







}
