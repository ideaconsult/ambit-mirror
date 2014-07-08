/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.db;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import net.idea.modbcum.i.IDBProcessor;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.processors.DefaultAmbitProcessor;

public abstract class AbstractDBProcessor<Target,Result> extends DefaultAmbitProcessor<Target, Result> implements IDBProcessor<Target,Result> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 735962863075840837L;
	
	protected SessionID sessionID = null;
	protected Connection connection; 
	protected boolean closeConnection = true;
	
	public boolean isCloseConnection() {
		return closeConnection;
	}
	public void setCloseConnection(boolean closeConnection) {
		this.closeConnection = closeConnection;
	}
	public boolean isEnabled() {
		return enabled;
	}
    public AbstractDBProcessor() {
        super();
    }

	public Connection getConnection() {
		return connection;
	}
	

	public void setConnection(Connection connection) throws DbAmbitException {
		if ((this.connection != null) && (this.connection != connection) && isCloseConnection()) try {
			close();
		} catch (SQLException x) {
		    logger.log(Level.SEVERE,x.getMessage(),x);      
        }
		this.connection = connection;
	}

	public void close() throws SQLException {
    		if ((connection != null) && (!connection.isClosed()) && isCloseConnection())
    			connection.close();
    		connection = null;
	}
	
}


