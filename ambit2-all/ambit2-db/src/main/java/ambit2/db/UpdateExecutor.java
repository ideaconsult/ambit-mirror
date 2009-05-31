/* UpdateExecutor.java
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.IQueryUpdate;

public class UpdateExecutor<Q extends IQueryUpdate> extends StatementExecutor<Q, Integer> {
	protected Hashtable<String,PreparedStatement> cache = new Hashtable<String,PreparedStatement>();
	/**
	 * 
	 */
	private static final long serialVersionUID = -7041631840539957442L;
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
	@Override
	public void close() throws SQLException {
		super.close();
		try { 
		setConnection(null);
		} catch (Exception x) {}
	}
	@Override
	protected Integer execute(Connection c, Q target) throws SQLException, AmbitException {
		int count = 0;
		String[] sql = target.getSQL();
		if (sql == null) return 0;
		for (int i=0; i < sql.length;i++) {
			if (sql[i] == null) continue;
			PreparedStatement statement = null;
			try {
				List<QueryParam> params = target.getParameters(i);
				statement = getCachedStatement(sql[i]);
				if (statement == null) {
					if (target.returnKeys(i))
						statement = c.prepareStatement(sql[i],Statement.RETURN_GENERATED_KEYS);
					else {
						statement = c.prepareStatement(sql[i]);
					}
				} else {
					statement.clearParameters();
				}
				setParameters(statement, params);
					logger.debug(statement);
					count += statement.executeUpdate();
					if (target.returnKeys(i)) {
						ResultSet keys = statement.getGeneratedKeys();
		                try {
			                while (keys.next()) 
			                	target.setID(i,keys.getInt(1));
		                } catch (Exception x) {
		                	
		                } finally {
		                	if (keys != null) keys.close();
		                }
					};
			} catch (SQLException x) {
				throw x;
			} finally {
				if (target.returnKeys(i))
					try { statement.close();} catch (Exception x) {} finally {statement = null;}
				else
					addStatementToCache(sql[i],statement);						
			}	
		}
		return count;
	}

}
