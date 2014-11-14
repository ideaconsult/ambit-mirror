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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.processors.ProcessorException;
import ambit2.db.SessionID;
import ambit2.db.update.queryfolder.CreateQueryFolder;

public class ProcessorCreateSession extends AbstractDBProcessor<SessionID, SessionID> {

		/**
	 * 
	 */
	private static final long serialVersionUID = -4591852127943879992L;
		
		
		public void open() throws DbAmbitException {
			// TODO Auto-generated method stub
			
		}
		public SessionID process(SessionID target) throws AmbitException {
			//if ((target!=null) && (target.getId()!=null) && (target.getId()>0)) return target;
			if (target==null) target = new SessionID();
			Connection c = getConnection();
			try {
				connection.setAutoCommit(false);
				
				PreparedStatement s = c.prepareStatement(CreateQueryFolder.sql_current_user,Statement.RETURN_GENERATED_KEYS);
				s.setNull(1,Types.INTEGER);
				s.setString(2,target.getName());
				boolean ok = s.execute();
				if (s.getUpdateCount()>0) {
					ResultSet rss = s.getGeneratedKeys();
					while (rss.next()) {
						target.setId(rss.getInt(1));
						break;
					}
					rss.close();
				}
				connection.commit();
				s.close();
				
				close();
				return target;
			} catch (SQLException x) {
				try {
					connection.rollback();
				} catch (SQLException xx) {
					logger.log(Level.WARNING,x.getMessage(),x);
				}
				throw new ProcessorException(this,x);
			} catch (Exception x) {
				throw new ProcessorException(this,x);
			}
		}
		

}


