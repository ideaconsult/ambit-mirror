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

package ambit2.db.processors;

import java.sql.Connection;
import java.sql.SQLException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.ProcessorException;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.exceptions.DbAmbitException;

public abstract class AbstractRepositoryWriter<Target,Result> extends AbstractDBProcessor<Target, Result>  {

    public synchronized void setConnection(Connection connection)  throws DbAmbitException  {
        super.setConnection(connection);
    }       
    public void open() throws DbAmbitException {
        try {
            prepareStatement(getConnection());
        } catch (SQLException x) {
            throw new DbAmbitException(null,x);
        }
    }
   
	protected abstract void prepareStatement(Connection connection) throws SQLException;
	public Result transaction(Target arg0) throws SQLException {
	    Result result = null;
		try {
			connection.setAutoCommit(false);	
			result = write(arg0);
	        connection.commit();			
	    } catch (SQLException x) {
	    	connection.rollback();
	    } finally {
	        
	    }
        return result;
	}	
	public abstract Result write(Target arg0) throws SQLException ;
    
	public Result process(Target target) throws AmbitException {
        try {
            return write(target);
        } catch (SQLException x) {
            throw new ProcessorException(this,x);
        }
	}
}


