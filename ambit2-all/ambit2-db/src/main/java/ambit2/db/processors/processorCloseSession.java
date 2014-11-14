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

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.processors.ProcessorException;
import ambit2.db.SessionID;

/**
 * Closes session designated by {@link SessionID} by updating the field "completed" in "sessions" table
 * @author nina
 *
 */
public class processorCloseSession extends AbstractDBProcessor<SessionID,SessionID> {
		protected final String sql = "update sessions set completed=current_timestamp where idsessions=? and user_name=(SUBSTRING_INDEX(user(),'@',1))";

		public void open() throws DbAmbitException {
			// TODO Auto-generated method stub
			
		}
		public SessionID process(SessionID target) throws AmbitException {
			try {
				Connection c = getConnection();
				PreparedStatement s = c.prepareStatement(sql);
				s.setInt(1,target.getId().intValue());
				int rows = s.executeUpdate(sql);
				s.close();
				close();
				if (rows == 0) throw new ProcessorException(this,"Fails on closing session "+target);
				return null;
			} catch (Exception x) {
				throw new ProcessorException(this,x);
			}
		}		
}


