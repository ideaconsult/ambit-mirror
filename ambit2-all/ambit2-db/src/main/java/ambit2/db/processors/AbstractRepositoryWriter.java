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
import java.util.logging.Level;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.processors.ProcessorException;
import ambit2.db.UpdateExecutor;
import ambit2.db.search.QueryExecutor;

public abstract class AbstractRepositoryWriter<Target, Result> extends AbstractDBProcessor<Target, Result> {
    /**
     * 
     */
    private static final long serialVersionUID = 7620071794813020111L;

    public enum OP {
	CREATE, READ, UPDATE, DELETE
    };

    protected OP operation = OP.CREATE;
    protected UpdateExecutor<IQueryUpdate> exec;
    protected QueryExecutor<IQueryObject> queryexec;

    public AbstractRepositoryWriter() {
	exec = new UpdateExecutor<IQueryUpdate>();
	exec.setUseCache(true);
	queryexec = new QueryExecutor<IQueryObject>();
	queryexec.setCache(true);
    }

    @Override
    public void close() throws Exception {
	try {
	    exec.close();
	} catch (Exception x) {
	    logger.log(Level.FINEST, x.getMessage(), x);
	}
	try {
	    queryexec.close();
	} catch (Exception x) {
	    logger.log(Level.FINEST, x.getMessage(), x);
	}
	super.close();
    }

    public OP getOperation() {
	return operation;
    }

    public void setOperation(OP operation) {
	this.operation = operation;
    }

    @Override
    public synchronized void setConnection(Connection connection) throws DbAmbitException {
	super.setConnection(connection);
	exec.setConnection(connection);
	queryexec.setConnection(connection);
    }

    @Override
    public void open() throws DbAmbitException {
	try {
	    prepareStatement(getConnection());
	} catch (SQLException x) {
	    throw new DbAmbitException(null, x);
	}
    }

    protected void prepareStatement(Connection connection) throws SQLException {

    }

    public Result transaction(Target target) throws SQLException, AmbitException {
	Result result = null;
	try {
	    connection.setAutoCommit(false);
	    switch (getOperation()) {
	    case CREATE: {
		result = create(target);
		break;
	    }
	    case READ: {
		result = read(target);
		break;
	    }
	    case DELETE: {
		result = delete(target);
		break;
	    }
	    case UPDATE: {
		result = update(target);
		break;
	    }
	    default:
		throw new SQLException("OperationNotSupported " + getOperation());
	    }
	    connection.commit();
	} catch (OperationNotSupportedException x) {
	    connection.rollback();
	} catch (SQLException x) {
	    connection.rollback();
	} finally {

	}
	return result;

    }

    public Result write(Target arg0) throws SQLException, OperationNotSupportedException, AmbitException {
	return create(arg0);
    }

    public Result create(Target arg0) throws SQLException, OperationNotSupportedException, AmbitException {
	return write(arg0);
    }

    public Result delete(Target arg0) throws SQLException, OperationNotSupportedException, AmbitException {
	throw new OperationNotSupportedException(getOperation().toString());
    }

    public Result read(Target arg0) throws SQLException, OperationNotSupportedException, AmbitException {
	throw new OperationNotSupportedException(getOperation().toString());
    }

    public Result update(Target arg0) throws SQLException, OperationNotSupportedException, AmbitException {
	throw new OperationNotSupportedException(getOperation().toString());
    }

    public Result process(Target target) throws AmbitException {
	try {
	    switch (getOperation()) {
	    case CREATE:
		return create(target);
	    case READ:
		return read(target);
	    case DELETE:
		return delete(target);
	    case UPDATE:
		return update(target);
	    default:
		throw new AmbitException("OperationNotSupported " + getOperation());
	    }
	} catch (OperationNotSupportedException x) {
	    throw new ProcessorException(this, x);
	} catch (SQLException x) {
	    throw new ProcessorException(this, x);
	}
    }
}
