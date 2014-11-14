/* StatementExecutor.java
 * Author: nina
 * Date: Mar 28, 2009
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

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.idea.modbcum.i.IStatement;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.processors.ProcessorException;



public abstract class StatementExecutor<Q extends IStatement,Results> extends AbstractDBProcessor<Q,Results> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9188531629150175036L;
	/**
	 * 
	 */

	protected Hashtable<String,PreparedStatement> cache = new Hashtable<String,PreparedStatement>();
	/**
	 * 
	 */
	protected boolean useCache = false;
	public boolean isUseCache() {
		return useCache;
	}
	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		Iterator<PreparedStatement> p = cache.values().iterator();
		while (p.hasNext())
			try {
			p.next().close();
			} catch (Exception x) {
				
			}
		cache.clear();
		super.setConnection(connection);
	}
	protected PreparedStatement getCachedStatement(String sql) {
		return cache.get(sql);
	}
	protected void addStatementToCache(String sql,PreparedStatement p) {
		if (p != null) 	cache.put(sql,p);
	}	
	
	public static void setParameters(PreparedStatement ps, List<QueryParam> params) throws SQLException {
		if (params != null)
			for (int i=0; i < params.size(); i++) {
				//if (params.get(i).getValue()== null) throw new SQLException("Null parameter found at "+(i+1));
				Class clazz = params.get(i).getType();
				if (Integer.class.equals(clazz)) {
					if (params.get(i).getValue()==null)
						ps.setNull(i+1, Types.INTEGER);
					else
						ps.setInt(i+1, ((Integer)params.get(i).getValue()).intValue());
				} else
				if (Long.class.equals(clazz)) {
					if (params.get(i).getValue()==null)
						ps.setNull(i+1, Types.BIGINT);
					else					
						ps.setLong(i+1, ((Number)params.get(i).getValue()).longValue());
				} else
				if (BigInteger.class.equals(clazz)) {
						if (params.get(i).getValue()==null)
							ps.setNull(i+1, Types.BIGINT);
						else					
							ps.setObject(i+1, params.get(i).getValue());
				} else					
				if (Double.class.equals(clazz)) {
					if (params.get(i).getValue()==null)
						ps.setNull(i+1, Types.DOUBLE);
					else					
						ps.setDouble(i+1, ((Number)params.get(i).getValue()).doubleValue());
				} else
				if (String.class.equals(clazz)) {
					if (params.get(i).getValue()==null)
						ps.setNull(i+1, Types.VARCHAR);
					else					
						ps.setString(i+1, params.get(i).getValue().toString());
				} else
				if (Boolean.class.equals(clazz)) {
					if (params.get(i).getValue()==null)
						ps.setNull(i+1, Types.BOOLEAN);
					else					
						ps.setBoolean(i+1, ((Boolean)params.get(i).getValue()).booleanValue());
				} else
					throw new SQLException("Unsupported type "+clazz);
			}		
	}
	
	public void open() throws DbAmbitException {
	}

	public Results process(Q target) throws AmbitException {
		Connection c = getConnection();		
		if (c == null) throw new AmbitException("no connection");
		try {
			return	execute(c,target);	
		} catch (Exception x) {
			throw new ProcessorException(this,x);
		}
	}
	
	protected abstract Results execute(Connection c,Q target) throws SQLException, AmbitException ;


	public void closeResults(Results rs) throws SQLException {
	}
	@Override
	public void close() throws Exception {
		Iterator<PreparedStatement> p = cache.values().iterator();
		while (p.hasNext())
			try {
			p.next().close();
			} catch (Exception x) {
				
			}
		cache.clear();		
		closeResults(null);		
		super.close();
	}
	
}
