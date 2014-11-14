/* PropertyStatsString.java
 * Author: nina
 * Date: Apr 9, 2009
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

package ambit2.db.search.property;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;

public class PropertyStatsString extends AbstractQuery<Property,String, StringCondition,String> 
																implements IQueryRetrieval<String>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6364182865662239013L;
	public static String sql = 
		"select distinct(value)  from property_values join property_string as f using (idvalue_string)\n"+
		"join properties using(idproperty) where %s %s %s limit 25";
	public static String where_name = "name=? ";
	public static String where_value = "value %s ? ";	
	public PropertyStatsString() {
		super();
		setCondition(StringCondition.getInstance("regexp ^"));
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();		
		if (getFieldname()!=null) 
			params.add(new QueryParam<String>(String.class, getFieldname().getName()));
		if (getValue()!= null)
			params.add(new QueryParam<String>(String.class, getValue()));	
		if (params.size()==0) return null;
		return params;
	}
	@Override
	public String getValue() {
		if ("".equals(super.getValue())) return null;
		return getCondition().getParam(super.getValue());

	}
	public String getSQL() throws AmbitException {
		String a1 = (getFieldname()==null)?"":where_name;
		String a2 = (getValue()==null)?"":where_value;
		String a3 = ((getFieldname()!=null)&&(getValue()!=null)?"and":"");
		return String.format(String.format(sql,a1,a3,a2),getCondition());
	}

	public String getObject(ResultSet rs) throws AmbitException {
		try {
			return rs.getString(1);
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	public double calculateMetric(String object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}

}
