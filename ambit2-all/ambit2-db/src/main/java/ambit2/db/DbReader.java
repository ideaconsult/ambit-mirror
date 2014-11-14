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
import java.util.logging.Logger;

import net.idea.modbcum.i.IDBProcessor;
import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;
import ambit2.db.search.QueryExecutor;

public class DbReader<ResultType> extends AbstractBatchProcessor<IQueryRetrieval<ResultType>, ResultType>
								  implements IDBProcessor<IQueryRetrieval<ResultType>, IBatchStatistics>
									{
	protected enum cached_results {TRUE,FALSE,NOTCACHED};
	protected ResultSet resultSet;
	protected QueryExecutor<IQueryObject<ResultType>> executor;
	protected boolean handlePrescreen = false;
	public boolean isHandlePrescreen() {
		return handlePrescreen;
	}
	public void setHandlePrescreen(boolean handlePrescreen) {
		this.handlePrescreen = handlePrescreen;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -509334177829567145L;

	public DbReader() {

	}
	/**
	 * Returns cached query results or NOTCACHED if not in the cache. Default implementation returns NOTCACHED.
	 * @param query
	 * @param object
	 * @return
	 */
	protected cached_results getCached(String category,String key, ResultType object) {
		return cached_results.NOTCACHED;
	}
	/**
	 * Does nothing, otherwise should cache the query result
	 * @param query
	 * @param object
	 */
	protected void cache(String category,String key, ResultType object,boolean ok) {

	}	
	/**
	 * 
	 * @param query
	 * @param object
	 * @return
	 * @throws AmbitException
	 */
	protected boolean prescreen(IQueryRetrieval<ResultType> query, ResultType object) throws AmbitException {
		cached_results result = getCached(query.getCategory(),query.getKey(),object);
		switch (result) {
		case NOTCACHED: {
			boolean ok = query.calculateMetric(object)>0;
			if (query.getKey()!=null)
			cache(query.getCategory(),query.getKey(),object,ok);
			return ok;
		}
		case TRUE: {
			return true;
		}
		case FALSE: {
			return false;
		}
		default: {
			throw new AmbitException("Invalid "+result);
		}
		}
	}
	public Iterator<ResultType> getIterator(final IQueryRetrieval<ResultType> query)
			throws AmbitException {
		if (executor==null) {
			executor = new QueryExecutor<IQueryObject<ResultType>>();
			executor.setCloseConnection(false);
			executor.setCache(true);
			executor.setConnection(connection);
		}
		setResultSet(executor.process(query));
		return new Iterator<ResultType>() {
			protected long counter= 0;
			protected ResultType cachedRecord = null;
			
			public boolean hasNext() {
				try {
					cachedRecord = null;
					if (handlePrescreen && query.isPrescreen()) {
						try {
							
							counter++;
							long max = (query.getPageSize()>0)?query.getPageSize():1000000;
							if (counter > max) return false;
							boolean loop=getResultSet().next();
							long attemptsStart = System.currentTimeMillis();
							while (loop) {

								cachedRecord = query.getObject(getResultSet());
								if (prescreen(query, cachedRecord)) return loop;
								else {
									cachedRecord = null;
									if ((System.currentTimeMillis() - attemptsStart)>60000) {
										loop = false; 
										break;
									} else	loop=getResultSet().next();
								}
							}
							return loop;
						} catch (AmbitException x) {
							Logger.getLogger(getClass().getName()).severe(x.getMessage());
							return false;
						}
					} else
						return getResultSet().next();
				} catch (SQLException x) {
					onError(null,null,batchStatistics,x);
					return false;
				}
			}
			public ResultType next() {
				try {
					return cachedRecord==null?query.getObject(getResultSet()):cachedRecord;
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
	public void beforeProcessing(IQueryRetrieval<ResultType> target)
			throws AmbitException {
		super.beforeProcessing(target);

	}
	@Override
	public void afterProcessing(IQueryRetrieval<ResultType> target,
			Iterator<ResultType> iterator) throws AmbitException {
		
		try {
			executor.closeResults(getResultSet());
			resultSet = null;
		} catch (SQLException x) {

			throw new AmbitException(x);
		}
		super.afterProcessing(target,iterator);
	}
	
	@Override
	public void close() throws Exception {
		try { if (resultSet!=null) {resultSet.close(); resultSet=null; }} catch (Exception x) {}
		try { if (executor!=null) {executor.close(); executor=null; }} catch (Exception x) {}
		super.close();
	}
	protected ResultSet getResultSet() {
		return resultSet;
	}
	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}


}
