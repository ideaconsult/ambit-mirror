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
import java.util.logging.Level;

import net.idea.modbcum.i.IDBProcessor;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.processors.ProcessorsChain;
import ambit2.db.SessionID;

public class DBProcessorsChain<Target,Result,P extends IDBProcessor<Target, Result>> extends ProcessorsChain<Target,Result,P> implements IDBProcessor<Target,Result> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2269380236406696708L;
	protected Connection connection;
	protected SessionID sessionID = null;
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) throws DbAmbitException {
		this.connection = connection;
		for (int i=0; i < size();i++)
			get(i).setConnection(connection);

	}
	@Override
	public void add(int index, P element) {
		try {
			element.setConnection(getConnection());
			} catch (DbAmbitException x) {
				logger.log(Level.WARNING,x.getMessage(),x);
			}
		super.add(index, element);
	}
	@Override
	public boolean add(P o) {
		try {
		o.setConnection(getConnection());
		} catch (DbAmbitException x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return super.add(o);
	}
	public SessionID getSession() {
		return sessionID;
	}

	public void open() throws DbAmbitException {
		for (int i=0; i < size();i++) try {
			get(i).open();
		} catch (Exception x) {
			throw new DbAmbitException(x);
		}

	}
	@Override
	public void close() {
		for (int i=0; i < size();i++)
			try {
				get(i).close();
			} catch (Exception x) {
				logger.log(Level.WARNING,x.getMessage(),x);
			}
		super.close();
	}


}
