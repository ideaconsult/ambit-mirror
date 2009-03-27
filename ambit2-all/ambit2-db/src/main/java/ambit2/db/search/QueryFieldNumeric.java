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

package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;

public class QueryFieldNumeric extends AbstractStructureQuery<String,Number,NumberCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1096544914170183549L;

	public final static String sqlField = 
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure\n"+
		"join property_values using(idstructure) join %s as f using (idvalue,idtype)\n"+
		"join properties using(idproperty) where\n"+
		"name=? and value %s ?";
	
	public final static String sqlAnyField = 
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure join property_values using(idstructure) "+
		"join %s as f using (idvalue,idtype) where value %s ?";
	
	protected Double maxValue;
	public Double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}
	
	public QueryFieldNumeric() {
		setFieldname("");
		setCondition(NumberCondition.getInstance("="));
	}
	public String getSQL() throws AmbitException {
		String cd = null;
		if ((getFieldname() ==null) || "".equals(getFieldname()))
			cd = String.format(sqlAnyField,intValue()?"property_int":"property_number",getCondition().getSQL());
		else
			cd = String.format(sqlField,intValue()?"property_int":"property_number",getCondition().getSQL());
		
		if (NumberCondition.getInstance(NumberCondition.between).equals(getCondition()))
			cd = cd + " and ?";		
		return cd;
	}
	protected boolean intValue() {
		return (getValue() instanceof Integer);
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue() == null) throw new AmbitException("Parameter not defined!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if (!"".equals(getFieldname()))
			params.add(new QueryParam<String>(String.class, getFieldname()));
		if (intValue())
			params.add(new QueryParam<Number>(Integer.class, getValue()));
		else
			params.add(new QueryParam<Number>(Double.class, getValue()));	
		
		if (NumberCondition.between.equals(getCondition().getSQL()))
			params.add(new QueryParam<Double>(Double.class, getMaxValue()));
		
		return params;
	}
	@Override
	public void setValue(Number value) {
		super.setValue(value);
	}
}
