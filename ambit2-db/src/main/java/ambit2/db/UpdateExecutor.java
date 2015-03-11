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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.IStoredProcStatement;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.i.query.QueryParam;


@SuppressWarnings("unchecked")
public class UpdateExecutor<Q extends IQueryUpdate> extends StatementExecutor<Q, Integer> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -4621464032598050969L;

    @Override
    public void close() throws Exception {
	super.close();
	try {
	    setConnection(null);
	} catch (Exception x) {
	}
    }

    @Override
    protected Integer execute(Connection c, Q target) throws SQLException, AmbitException {
	int count = 0;
	if (target == null)
	    throw new AmbitException();
	String[] sql = target.getSQL();
	if (sql == null)
	    return 0;
	for (int i = 0; i < sql.length; i++) {
	    if (sql[i] == null)
		continue;
	    PreparedStatement statement = null;
	    try {
		List<QueryParam> params = target.getParameters(i);
		statement = getCachedStatement(sql[i]);
		if (statement == null) {
		    if (target.isStoredProcedure())
			statement = c.prepareCall(sql[i]);
		    else if (target.returnKeys(i))
			statement = c.prepareStatement(sql[i], Statement.RETURN_GENERATED_KEYS);
		    else {
			statement = c.prepareStatement(sql[i]);
		    }
		} else {
		    statement.clearParameters();
		}
		setParameters(statement, params);
		logger.log(Level.FINEST, statement.toString());

		if (target instanceof IStoredProcStatement) {
		    ((IStoredProcStatement) target).registerOutParameters((CallableStatement) statement);
		    count += statement.executeUpdate();
		    ((IStoredProcStatement) target).getStoredProcedureOutVars((CallableStatement) statement);
		    if (count == 0)
			count++;
		} else
		    count += statement.executeUpdate();
		if (target.returnKeys(i)) {
		    // TODO if on duplicate is used two generated keys are
		    // returned! http://bugs.mysql.com/bug.php?id=42309
		    ResultSet keys = statement.getGeneratedKeys();
		    try {
			while (keys.next()) {
			    target.setID(i, keys.getInt(1));
			    break;
			}
		    } catch (Exception x) {

		    } finally {
			if (keys != null)
			    keys.close();
		    }
		}
		;
	    } catch (SQLException x) {
		throw x;
	    } finally {
		/**
		 * Statements, returning generated keys were not cached, due to
		 * MySQL bug, causing memory leak :
		 * http://bugs.mysql.com/bug.php?id=44056 Connector/J claims bug
		 * to be fixed since 5.1.8 Prepared statements caching back
		 * again.
		 * 
		 */
		// if (target.returnKeys(i))
		// try { statement.close();} catch (Exception x) {} finally
		// {statement = null;}
		// else
		if (useCache)
		    addStatementToCache(sql[i], statement);
	    }
	}
	return count;
    }

}
