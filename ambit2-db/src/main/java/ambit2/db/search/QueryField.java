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

package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.exceptions.AmbitException;


/**
 * Search for a structure with property with given name and value
 * @author nina
 *
 */
public class QueryField extends AbstractStructureQuery<String,String, StringCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5810564793012596407L;
	public final static String sqlField = 
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure\n"+
		"join property_values using(idstructure) join property_string as f using (idvalue,idtype)"+
		"join properties using(idproperty) where\n"+
		"name=? and value %s ?";
	public final static String sqlAnyField = 
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure join property_values using(idstructure) join property_string as f using (idvalue,idtype) where value %s ?";
	public QueryField() {
		setFieldname("");
		setCondition(StringCondition.getInstance("="));
	}
	public String getSQL() throws AmbitException {
		if ((getFieldname() ==null) || "".equals(getFieldname()))
			return String.format(sqlAnyField,getCondition().getSQL());
		else
			return String.format(sqlField,getCondition().getSQL());
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue() == null) throw new AmbitException("Parameter not defined!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if (!"".equals(getFieldname()))
			params.add(new QueryParam<String>(String.class, getFieldname()));

		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}
}


