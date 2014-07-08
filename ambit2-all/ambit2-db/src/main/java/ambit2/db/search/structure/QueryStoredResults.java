/* QueryStoredResults.java
 * Author: nina
 * Date: Apr 10, 2009
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

package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.EQCondition;
import ambit2.db.search.IStoredQuery;

public class QueryStoredResults extends AbstractStructureQuery<IStoredQuery, Boolean, EQCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4597129739347497000L;
	public static final String sqlField="select %s,idchemical,idstructure,selected, %s,text from query_results %s where %s %s %s %s order by metric %s";
	//public static final String join="join query using(idquery)";
	public static final String join="";
	public static final String where_query = "idquery=?";
	public static final String where_name = "selected = ?";

	public QueryStoredResults() {
		setCondition(EQCondition.getInstance());
		setId(-1);
	}
	public QueryStoredResults(IStoredQuery query) {
		this();
		setFieldname(query);
		setId(-1);
	}	
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getId() > 0)
			params.add(new QueryParam<Integer>(Integer.class, getId()));		
		if (getFieldname()!=null)
			params.add(new QueryParam<Integer>(Integer.class, getFieldname().getId()));
		if (getValue()!=null)
			params.add(new QueryParam<Boolean>(Boolean.class, getValue()));	
		if (params.size()==0) throw new AmbitException("No query or name defined!");
		else return params;
	}

	public String getSQL() throws AmbitException {
		String a1 = (getFieldname()==null)?"":where_query;
		String a2 = (getValue()==null)?"":where_name;
		String j = (getValue()==null)?"":join;
		String a3 = ((getFieldname()!=null)&&(getValue()!=null))?"and":"";
		return String.format(sqlField,
				getId()>0?"?":"idquery",
				chemicalsOnly?"max(metric) as metric":"metric",
				j,a1,a3,a2,
				chemicalsOnly?"group by idchemical":"",
				order_descendant?"desc":"asc");
	}
	@Override
	public String toString() {
		if (fieldname == null) return "Previous search results";
		return super.toString();
	}
}
