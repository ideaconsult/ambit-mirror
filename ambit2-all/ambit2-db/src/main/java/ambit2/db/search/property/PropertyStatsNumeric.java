/* PropertyStatsNumeric.java
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
import ambit2.base.data.PropertyStats;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;

public class PropertyStatsNumeric extends AbstractQuery<Property,String, StringCondition,PropertyStats> 
													implements IQueryRetrieval<PropertyStats>{

	public static String sql = 
		"SELECT min(value_num) as m1,max(value_num) as m2,avg(value_num) as m3 from property_values join properties using(idproperty) where name=? and value_num is not null\n"
		;

		
		/**
	 * 
	 */
	private static final long serialVersionUID = 8369867048140756850L;
	public PropertyStatsNumeric() {
		super();
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
	}
	public List<QueryParam> getParameters() throws AmbitException {
			if (getFieldname()!=null) {
				List<QueryParam> params = new ArrayList<QueryParam>();
				params.add(new QueryParam<String>(String.class, getFieldname().getName()));				

				return params;
			} else throw new AmbitException("Property not specified!");
			
	}
	public String getSQL() throws AmbitException {
			return sql;
	}
	public PropertyStats getObject(ResultSet rs) throws AmbitException {
		try {
			PropertyStats p = new PropertyStats();
			p.setMin(rs.getDouble(1));
			p.setMax(rs.getDouble(2));
			p.setAvg(rs.getDouble(3));
			//p.setCount(rs.getLong(4));
			
			return p;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

	public String getFieldID() {
			return null;
	}
	public String getValueID() {
			return null;
	}
	public Class getFieldType() {
			return Property.class;
	}
	public Class getValueType() {
			return PropertyStats.class;
	}
	public double calculateMetric(PropertyStats object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}

}
