/* AmbitRows.java
 * Author: Nina Jeliazkova
 * Date: May 5, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
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
import java.sql.SQLException;

import ambit2.core.exceptions.AmbitException;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.QueryExecutor;

import com.sun.rowset.CachedRowSetImpl;

public abstract class AmbitRows<T, Q extends IQueryObject> extends CachedRowSetImpl {
	protected Q query;
    public AmbitRows() throws SQLException {
        super();
    }
    public abstract T getObject() throws AmbitException ;
    
    public void open(Connection connection) throws AmbitException {
    	if (query == null) throw new AmbitException("Query not defined!");
    	QueryExecutor<Q> executor = new QueryExecutor<Q>();
    	executor.setConnection(connection);
    	executor.open();
    	try {
    		populate(executor.process(query));
    	} catch (SQLException x) {
    		throw new AmbitException(x);
    	}
    }
	public Q getQuery() {
		return query;
	}
	public void setQuery(Q query) {
		this.query = query;
	}
    
	

}
