/* QueryFieldNumeric.java
 * Author: nina
 * Date: Mar 24, 2009
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

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;

public class QueryFieldNumeric extends AbstractStructureQuery<Property,Number,NumberCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1096544914170183549L;

	public final static String sqlField = 
		"SELECT ? as idquery,idchemical,idstructure,1 as selected,value_num as metric FROM properties join property_values using(idproperty) join structure using(idstructure) ";
	public final String where_all = "where value_num is not null and value_num %s ? %s %s \n";
	public final String where_equal = "where value_num is not null and value_num = ? %s\n";

	protected Number maxValue;
	public Number getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Number maxValue) {
		this.maxValue = maxValue;
	}
	
	public QueryFieldNumeric() {
		setFieldname(null);
		setCondition(NumberCondition.getInstance("="));
	}

	public String getSQL() throws AmbitException {
//where value_num %s ? %s %s and value_num is not null\n";
//where abs(value_num - ?) < 1E-4 %s and value_num is not null\n";
		String sql = NumberCondition.getInstance("=").equals(getCondition())?
					String.format(sqlField+where_equal,(isAnyField()?"":"and name=?"))
				:
					String.format(sqlField+where_all,getCondition().getSQL(),
					(NumberCondition.getInstance(NumberCondition.between).equals(getCondition()))?" and ?":"",
					(isAnyField()?"":"and name=?")					
					);
		if (isChemicalsOnly()) return sql + " group by idchemical"; else return sql;
					
	}
	
	protected boolean isAnyField() {
		return (getFieldname() ==null) ||  "".equals(getFieldname().getName());
	}
	
	@Override
	public void setFieldname(Property fieldname) {
		super.setFieldname(fieldname);
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue() == null) throw new AmbitException("Parameter not defined!");
		List<QueryParam> params = new ArrayList<QueryParam>();


			params.add(new QueryParam<Integer>(Integer.class, getId()));
			params.add(new QueryParam<Number>(Double.class, getValue()));
			if (NumberCondition.between.equals(getCondition().getSQL()))
				params.add(new QueryParam<Number>(Double.class, getMaxValue()));
			if ((getFieldname() !=null) && ! "".equals(getFieldname().getName()))
				params.add(new QueryParam<String>(String.class, getFieldname().getName()));

		
		
		return params;
	}
	@Override
	public void setValue(Number value) {
		super.setValue(value);
	}
	
	@Override
	public String toString() {
		if ((getFieldname()==null) && (getValue()==null)) return "Search by numeric properties";
		
		return 
		String.format("%s %s %s %s %s",
				getFieldname()==null?"Any property":getFieldname(),
				getCondition(),
				getValue(),
				(NumberCondition.between.equals(getCondition().toString())?"and":""),
				NumberCondition.between.equals(getCondition().toString())?String.format("%10.4f",getMaxValue()):""
						);

	}
}
