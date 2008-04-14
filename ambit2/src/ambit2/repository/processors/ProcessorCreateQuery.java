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
import java.util.List;

import weka.gui.SetInstancesPanel;

import ambit2.repository.AbstractDBProcessor;
import ambit2.repository.IQuery;
import ambit2.repository.ProcessorException;
import ambit2.repository.QueryID;
import ambit2.repository.QueryParam;

/**
 * Inserts into query table idstructure numbers from a query identified by {@link QueryID}. Example:
 * <pre>
 * insert ignore into query_results (idquery,idstructure,selected) select 1,idstructure,1 from structure limit 100,200
 * </pre>
 * @author nina
 *
 * @param <Target>
 */
public class ProcessorCreateQuery<Q extends IQuery>  extends AbstractDBProcessor<Q,Q> {

	protected final String sql = "insert into query (idsessions,name) values (?,?)";
	
	public Q process(Q target) throws ProcessorException {
		Connection c = getConnection();		
		try {
			target.setRows(0);
			connection.setAutoCommit(false);	
			
			PreparedStatement s = c.prepareStatement(sql);
			s.setInt(1,getSession().getId().intValue());
			s.setString(2,target.getName());
			System.out.println(s);
			if (s.executeUpdate()>0) {
				ResultSet rss = s.getGeneratedKeys();
				while (rss.next())
					target.setId(new Integer(rss.getInt(1)));
				rss.close();
			

				PreparedStatement sresults = c.prepareStatement(target.getSQL());

				List<QueryParam> params = target.getParameters();
				if (params != null)
					for (int i=0; i < params.size(); i++) {
						Class clazz = params.get(i).getType();
						if (Integer.class.equals(clazz))
							sresults.setInt(i+1, ((Integer)params.get(i).getValue()).intValue());
						if (String.class.equals(clazz))
							sresults.setString(i+1, params.get(i).getValue().toString());					
					}
				System.out.println(sresults);								
				int rows = sresults.executeUpdate();
				target.setRows(rows);
				sresults.close();
				if (rows > 0)
					connection.commit();
				else 
					connection.rollback();
			}
			s.close();
			close();
			return target;
		} catch (SQLException x) {
			try {
				c.rollback();
			} catch (SQLException xx){
				
			}
			throw new ProcessorException(x);
		}
	}

}


