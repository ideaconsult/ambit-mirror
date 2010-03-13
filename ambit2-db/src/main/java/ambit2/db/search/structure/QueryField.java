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
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.NumberCondition;
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
	
	public boolean isRetrieveProperties() {
		return retrieveProperties;
	}
	public void setRetrieveProperties(boolean retrieveProperties) {
		this.retrieveProperties = retrieveProperties;
	}
	public final static String sqlField = 
		"select ? as idquery,idchemical,structure.idstructure,if(type_structure='NA',0,1) as selected,1 as metric,null as text from structure\n"+
		"join property_values using(idstructure) join property_string as f using (idvalue_string)"+
		"join properties using(idproperty) %s where %s\n"+
		"%s %s ? and value %s ? %s";
	
	public final static String sqlFieldProperties = 
		"select ? as idquery,idchemical,structure.idstructure,if(type_structure='NA',0,1) as selected,1 as metric,null as text,idproperty,name,comments,value from structure\n"+
		"join property_values using(idstructure) join property_string as f using (idvalue_string)"+
		"join properties using(idproperty) %s where %s\n"+
		"%s %s ? and value %s ? %s";
	
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
	public final static String sqlAnyField = 
		"select ? as idquery,structure.idchemical,structure.idstructure,if(type_structure='NA',0,1) as selected,1 as metric,null as text from structure join property_values using(idstructure) join property_string as f using (idvalue_string) %s where %s value %s ? %s";

	public final static String sqlAnyFieldProperties = 
		"select ? as idquery,structure.idchemical,structure.idstructure,if(type_structure='NA',0,1) as selected,1 as metric,null as text,idproperty,name,comments,value from structure join property_values using(idstructure) join property_string as f using (idvalue_string) %s where %s value %s ? %s";
	
	public QueryField() {
		setFieldname(null);
		setCondition(StringCondition.getInstance("="));
		//setNameCondition(StringCondition.getInstance(StringCondition.C_LIKE));
		setNameCondition(StringCondition.getInstance(StringCondition.C_STARTS_WITH));
	}
	public String getSQL() throws AmbitException {
		
		
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
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue() == null) throw new AmbitException("Parameter not defined!");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if ((getFieldname() ==null) || "".equals(getFieldname().getName()))
			;
		else
			params.add(new QueryParam<String>(String.class, getFieldname().getName()));

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


