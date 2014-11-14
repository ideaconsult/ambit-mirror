/* SearchStoredQueries.java
 * Author: nina
 * Date: Apr 12, 2009
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

package ambit2.db.update.storedquery;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.SessionID;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.StringCondition;

public class SearchStoredQueries  extends  AbstractQuery<SessionID, String, StringCondition, IStoredQuery> 
																	implements IQueryRetrieval<IStoredQuery>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5945313962492375417L;
	public final static String sqlField = "select idquery,count(idstructure) as rows,name,content from query_results join query using(idquery) join sessions using (idsessions) where user_name=SUBSTRING_INDEX(user(),'@',1) %s %s group by idquery order by idquery desc";
	public static final String where_session = "and idsessions = ?";
	public static final String where_query = "and name %s ?";	

	public SearchStoredQueries() {
		setCondition(StringCondition.getInstance("regexp ^"));
	}
	@Override
	public String getValue() {
		if ((super.getValue()==null) ||"".equals(super.getValue())) return null;
		return getCondition().getParam(super.getValue());

	}	
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getFieldname()!=null)
			params.add(new QueryParam<Integer>(Integer.class, getFieldname().getId()));
		if (getValue()!=null)
			params.add(new QueryParam<String>(String.class, getValue()));
		//if (params.size()==0) throw new AmbitException("No query or name defined!");
		return params;
	}

	public String getSQL() throws AmbitException {
		String a1 = (getFieldname()==null)?"":where_session;
		String a2 = (getValue()==null)?"":String.format(where_query,getCondition().getSQL());;

		return String.format(sqlField,a1,a2);
	}

	public IStoredQuery getObject(ResultSet rs) throws AmbitException {
		try {
			StoredQuery q = new StoredQuery();
			q.setId(rs.getInt(1));
			q.setName(rs.getString(3));
			q.setContent(rs.getString(4));
			q.setRows(rs.getInt(2));
			return q;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	public double calculateMetric(IStoredQuery object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}
}
