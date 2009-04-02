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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.xindice.core.objects.Types;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.ProcessorException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.QueryParam;


public abstract class StatementExecutor<Q extends IStatement,Results> extends AbstractDBProcessor<Q,Results> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6014775151480425590L;
	public static void setParameters(PreparedStatement ps, List<QueryParam> params) throws SQLException {
		if (params != null)
			for (int i=0; i < params.size(); i++) {
				//if (params.get(i).getValue()== null) throw new SQLException("Null parameter found at "+(i+1));
				Class clazz = params.get(i).getType();
				if (Integer.class.equals(clazz)) {
					if (params.get(i).getValue()==null)
						ps.setNull(i+1, Types.INT);
					else
						ps.setInt(i+1, ((Integer)params.get(i).getValue()).intValue());
				} else
				if (Long.class.equals(clazz)) {
					if (params.get(i).getValue()==null)
						ps.setNull(i+1, Types.LONG);
					else					
						ps.setLong(i+1, ((Long)params.get(i).getValue()).longValue());
				} else
				if (Double.class.equals(clazz)) {
					if (params.get(i).getValue()==null)
						ps.setNull(i+1, Types.DOUBLE);
					else					
						ps.setDouble(i+1, ((Double)params.get(i).getValue()).doubleValue());
				} else
				if (String.class.equals(clazz)) {
					if (params.get(i).getValue()==null)
						ps.setNull(i+1, Types.STRING);
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
			x.printStackTrace();
			throw new ProcessorException(this,x);
		}
	}
	
	protected abstract Results execute(Connection c,Q target) throws SQLException, AmbitException ;


	public void closeResults(Results rs) throws SQLException {
	}
	@Override
	public void close() throws SQLException {
		closeResults(null);		
		super.close();
	}
	
}
