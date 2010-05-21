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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.AbstractPropertyRetrieval.SearchMode;


/**
 * Search for a structure with property with given name and value
 * @author nina
 *
 */
public class QueryField extends AbstractStructureQuery<Property,String, StringCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5810564793012596407L;
	protected boolean retrieveProperties = false;
	protected boolean caseSensitive = true;
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	public boolean isRetrieveProperties() {
		return retrieveProperties;
	}
	public void setRetrieveProperties(boolean retrieveProperties) {
		this.retrieveProperties = retrieveProperties;
	}

	protected static String queryField = "%s %s ? and"; //name namecondition value
	protected static String queryValueCaseSensitive = "value %s ?"; 
	protected static String queryValueCaseInsensitive = "lower(value) %s lower(?)"; 
	
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
	public final static String sqlField = 
		"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n" +	
		"FROM structure s1\n"+
		"LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical  AND (1E10*s1.preference+s1.idstructure) > (1E10*s2.preference+s2.idstructure)\n"+
		"join (\n"+
		"select distinct(structure.idchemical)\n"+
		"from structure\n"+
		"join  property_values using(idstructure)\n"+
		"join property_string using (idvalue_string)\n"+
		"join properties using(idproperty)\n"+
		"where %s %s\n"+
		") a on a.idchemical=s1.idchemical\n"+
		"where s2.idchemical is null\n";
	
	public final static String sqlFieldProperties = 
	"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text,-1 as idproperty,name,comments,value\n" +	
	"FROM structure s1\n"+
	"LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical  AND (1E10*s1.preference+s1.idstructure) > (1E10*s2.preference+s2.idstructure)\n"+
	"join (\n"+
	"select idchemical,group_concat(distinct value SEPARATOR ';') as value,group_concat(distinct name) as name,group_concat(distinct comments) as comments\n"+
	"from structure\n"+
	"join  property_values using(idstructure)\n"+
	"join property_string using (idvalue_string)\n"+
	"join properties using(idproperty)\n"+
	"where %s %s\n"+
	"group by idchemical\n"+
	") a on a.idchemical=s1.idchemical\n"+
	"where s2.idchemical is null\n";
	
	protected StringCondition nameCondition;
	protected SearchMode searchMode = SearchMode.name;
	
	public StringCondition getNameCondition() {
		return nameCondition;
	}
	public void setNameCondition(StringCondition nameCondition) {
		this.nameCondition = nameCondition;
	}
	
	public boolean isSearchByAlias() {
		return searchMode==SearchMode.alias;
	}

	public void setSearchByAlias(boolean value) {
		searchMode = value?SearchMode.alias:SearchMode.name;
	}	

	public QueryField() {
		setFieldname(null);
		setCondition(StringCondition.getInstance("="));
		//setNameCondition(StringCondition.getInstance(StringCondition.C_LIKE));
		setNameCondition(StringCondition.getInstance(StringCondition.C_STARTS_WITH));
	}
	public String getSQL() throws AmbitException {
		
		String whereName = ((getFieldname() ==null) || "".equals(getFieldname().getName()))?"":
				String.format(queryField,searchMode.getSQL(),getNameCondition().getSQL().toString());
		return String.format(
				isRetrieveProperties()?sqlFieldProperties:sqlField,
				whereName,
				String.format(isCaseSensitive()?queryValueCaseSensitive:queryValueCaseInsensitive,getCondition().getSQL())
				);
		/*
		if ((getFieldname() ==null) || "".equals(getFieldname().getName()))
			return String.format(isRetrieveProperties()?sqlAnyFieldProperties:sqlAnyField,
					isChemicalsOnly()?group:"",
					isChemicalsOnly()?where_group:"",							
					getCondition().getSQL(),"");
		else
			return String.format(isRetrieveProperties()?sqlFieldProperties:sqlField,
					isChemicalsOnly()?group:"",
					isChemicalsOnly()?where_group:"",
					searchMode.getSQL(),
					getNameCondition().getSQL().toString(),
					getCondition().getSQL(),
					"");
			*/		
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue() == null) throw new AmbitException("Parameter not defined!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if ((getFieldname() ==null) || "".equals(getFieldname().getName()))
			;
		else
			params.add(new QueryParam<String>(String.class, isSearchByAlias()?getFieldname().getLabel():getFieldname().getName()));

		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}
	@Override
	public String toString() {
		if ((getFieldname()==null) && (getValue()==null)) return "Search by text properties";
		else return super.toString();

	}	
	//idproperty,name,comments,value
	//7,8,9,10
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		
		try {
			IStructureRecord record = super.getObject(rs);
			if (isRetrieveProperties()) {
				Property p = Property.getInstance(rs.getString(8),"","");
				p.setId(rs.getInt(7));
				p.setUnits(rs.getString(3));
				p.setLabel(rs.getString(9));
				record.setProperty(p,rs.getString(10));
			}
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}	
}


