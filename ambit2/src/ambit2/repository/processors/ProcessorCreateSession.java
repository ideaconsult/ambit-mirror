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

package ambit2.repository.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ambit2.repository.AbstractDBProcessor;
import ambit2.repository.ProcessorException;
import ambit2.repository.SessionID;

public class ProcessorCreateSession extends AbstractDBProcessor<SessionID, SessionID> {

		protected final String sql = "insert into sessions (user_name) values (SUBSTRING_INDEX(user(),'@',1))";
		public SessionID process(SessionID target) throws ProcessorException {
			if ((target!=null) && (target.getId()>0)) return target;
			Connection c = getConnection();
			try {
				connection.setAutoCommit(false);
				PreparedStatement s = c.prepareStatement(sql);
				System.out.println(s);
				if (s.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS)>0) {
					ResultSet rss = s.getGeneratedKeys();
					while (rss.next()) 
						target = new SessionID(rss.getInt(1));
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
					xx.printStackTrace();
				}
				throw new ProcessorException(x);
			}
		}
		

}


