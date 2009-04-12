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
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.ProcessorException;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryStoredResults;

/**
 * Inserts into query table idstructure numbers from a query identified by {@link StoredQuery}. Example:
 * <pre>
 * insert ignore into query_results (idquery,idstructure,selected) select 1,idstructure,1 from structure limit 100,200
 * </pre>
 * @author nina

 */
public class ProcessorCreateQuery  extends AbstractDBProcessor<IQueryObject,IStoredQuery> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6765755816334059911L;
	protected final String sql = "insert into query (idquery,idsessions,name,content) values (null,?,?,?)";
	
    public void open() throws DbAmbitException {
        // TODO Auto-generated method stub
        
    }
	public IStoredQuery process(IQueryObject target) throws AmbitException {
		if (target == null) throw new AmbitException("Undefined query!");
		if (target instanceof QueryStoredResults) {
			return ((QueryStoredResults) target).getFieldname();
		}
		Connection c = getConnection();		
		try {
			StoredQuery result = new StoredQuery(-1);
			result.setQuery(target);
			result.setName(target.toString());
			
			connection.setAutoCommit(false);	
			
			PreparedStatement s = c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			s.setInt(1,getSession().getId().intValue());
			if (result.getName().length()>45)
				s.setString(2,result.getName().substring(0,44));
			else
				s.setString(2,result.getName());
            s.setString(3,result.getSQL());
			if (s.executeUpdate()>0) {
				ResultSet rss = s.getGeneratedKeys();
				while (rss.next())
					result.setId(new Integer(rss.getInt(1)));
				rss.close();
			

				PreparedStatement sresults = c.prepareStatement(result.getSQL());
				//System.out.println(result.getSQL());
				List<QueryParam> params = result.getParameters();
				//System.out.println(params);				
				QueryExecutor.setParameters(sresults, params);
				System.out.println(sresults);								
				int rows = sresults.executeUpdate();
				result.setRows(rows);
				sresults.close();
				if (rows > 0)
					connection.commit();
				else 
					connection.rollback();
			}
			s.close();
			close();
			return result;
		} catch (Exception x) {
			try {
				c.rollback();
			} catch (SQLException xx){
				
			}
			throw new ProcessorException(this,x);
		}
	}

}


