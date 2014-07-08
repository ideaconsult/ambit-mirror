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

package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.StringCondition;


/**
 * Search for a structure with property with given name and value
 * @author nina
 *
 */
public class QueryField extends QueryFieldAbstract<String,StringCondition,StringCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5810564793012596407L;

	/**
//FASTER!!!!!!
select idchemical,idstructure from structure
join
(
select distinct(s1.idchemical)
from structure s1
join  property_values using(idstructure)
join property_string using (idvalue_string)
join properties using(idproperty)
where lower(value)='hexane'
) a using(idchemical)
where preferred=1
limit 1

--even faster
SELECT s1.idchemical, s1.idstructure, s1.preference,s1.type_structure,value,name,comments
FROM structure s1
LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical AND s1.preference > s2.preference 
join (
select distinct(idchemical),group_concat(distinct value SEPARATOR ';') as value,group_concat(distinct name) as name,group_concat(distinct comments) as comments
from structure
join  property_values using(idstructure)
join property_string using (idvalue_string)
join properties using(idproperty)
where lower(value) regexp '^benzene'
) a on a.idchemical=s1.idchemical
where s2.idchemical is null;
	 */
	//get the structure with min preference and min idstructure

	public QueryField() {
		setFieldname(null);
		setCondition(StringCondition.getInstance("="));
		//setNameCondition(StringCondition.getInstance(StringCondition.C_LIKE));
		setNameCondition(StringCondition.getInstance(StringCondition.C_STARTS_WITH));
	}
	public String getSQL() throws AmbitException {
		
		String whereName = ((getFieldname() ==null) || "".equals(getFieldname().getName()))?"":
				String.format(queryField,searchMode.getSQL(),getNameCondition().getSQL().toString());
		//no property retrieval for case sensitive queries, because it is slow
		if (isCaseSensitive()) 
			return String.format(
					sqlField,
					whereName,
					String.format(queryValueCaseSensitive,getCondition().getSQL())
					);
		else
			return String.format(
					isRetrieveProperties()?sqlFieldProperties:sqlField_ci,
					whereName,
					String.format(queryValueCaseInsensitive,getCondition().getSQL())
					);
		
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue() == null) throw new AmbitException("Parameter not defined!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if ((getFieldname() ==null) || "".equals(getFieldname().getName()))
			;
		else
			params.add(new QueryParam<String>(String.class, isSearchByAlias()?getFieldname().getLabel():getFieldname().getName()));

		params.add(new QueryParam<String>(String.class, getValue().length()>255?getValue().substring(1,255):getValue()));
		return params;
	}
	@Override
	public String toString() {
		if ((getFieldname()==null) && (getValue()==null)) return "Search by text properties";
		else return super.toString();

	}	

}


