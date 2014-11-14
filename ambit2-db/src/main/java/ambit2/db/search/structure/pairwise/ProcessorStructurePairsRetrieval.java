/* ProcessorStructureRetrieval.java
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

package ambit2.db.search.structure.pairwise;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.QueryExecutor;

public class ProcessorStructurePairsRetrieval extends AbstractDBProcessor<IStructureRecord[], IStructureRecord[]> {
	protected IQueryRetrieval<IStructureRecord[]> query;
	protected QueryExecutor<IQueryObject<IStructureRecord[]>> exec = new QueryExecutor<IQueryObject<IStructureRecord[]>>();
	
	public ProcessorStructurePairsRetrieval() {
		this(new RetrieveStructurePair());
	}
	public ProcessorStructurePairsRetrieval(IQueryRetrieval<IStructureRecord[]> query) {
		this.query = query;
	}	
	public IQueryRetrieval<IStructureRecord[]> getQuery() {
		return query;
	}

	public void setQuery(IQueryRetrieval<IStructureRecord[]> query) {
		this.query = query;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2086055435347535113L;

	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		exec.setCloseConnection(false);
		exec.setConnection(getConnection());
		
	}
	public IStructureRecord[] process(IStructureRecord[] target)
			throws AmbitException {
		
		if (query instanceof RetrieveStructurePair) {
			((RetrieveStructurePair)query).setFieldname(target[0]);
			((RetrieveStructurePair)query).setValue(target[1]);
		} else if (query instanceof AbstractQuery) {
	        ((AbstractQuery)query).setValue(target);
	        //((AbstractQuery)query).setCondition(NumberCondition.getInstance());
		}
        ResultSet rs = null;
        try { 
        	rs = exec.process(query);
        	IStructureRecord[] result = target;
        	while (rs.next()) {
        		result = query.getObject(rs);
        	}
        	return result;
        } catch (Exception x) {
        	throw new AmbitException(query.getSQL(),x);
        } finally {
	        try {
	            exec.closeResults(rs);
	        } catch (SQLException x) {
	        	logger.log(Level.WARNING,x.getMessage(),x);
	        } 
        }

	}

	@Override
	public void close() throws Exception {
		try {exec.setConnection(null); } catch (Exception x) {}
		super.close();
	}

	@Override
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}
