package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.property.AbstractPropertyRetrieval.SearchMode;

public class RetrieveField<ResultType> extends AbstractQuery<Property,IStructureRecord,EQCondition,ResultType> implements IQueryRetrieval<ResultType> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7818288709974026824L;
	protected boolean addNew = false;
	public boolean isAddNew() {
		return addNew;
	}

	public void setAddNew(boolean addNew) {
		this.addNew = addNew;
	}

	protected SearchMode searchMode = SearchMode.name;
	public SearchMode getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(SearchMode searchMode) {
		this.searchMode = searchMode;
	}
	protected boolean chemicalsOnly = false; 
	
	public boolean isChemicalsOnly() {
		return chemicalsOnly;
	}

	public void setChemicalsOnly(boolean chemicalsOnly) {
		this.chemicalsOnly = chemicalsOnly;
	}

	public boolean isSearchByAlias() {
		return searchMode==SearchMode.alias;
	}

	public void setSearchByAlias(boolean value) {
		searchMode = value?SearchMode.alias:SearchMode.name;
	}

	public boolean isSearchByID() {
		return searchMode==SearchMode.idproperty;
	}

	public void setSearchByID(boolean value) {
		searchMode = value?SearchMode.idproperty:SearchMode.name;
	}	
	protected final String sql_structure = 
		"select name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,value_num,title,url,-1,id,units,comments from property_values \n"+
		"left join property_string using(idvalue_string) \n"+
		"join properties using(idproperty) join catalog_references using(idreference) \n"+
		"where idstructure=? %s";
	
	protected final String sql_structure_novalue = 
		"select name,idreference,idproperty,idstructure,null,null,title,url,idchemical,null,units,comments from structure \n"+
		"join  properties join catalog_references using(idreference)\n"+
		"where idstructure=? %s";
	
	protected final String sql_chemical = 
		"select name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,value_num,title,url,idchemical,id,units,comments from property_values \n"+
		"left join property_string using(idvalue_string) \n"+
		"join properties using(idproperty) join catalog_references using(idreference) \n"+
		"where idchemical=? %s group by comments,idvalue_string";
	
	protected final String sql_chemical_novalue = 
		"select name,idreference,idproperty,idstructure,null,null,title,url,idchemical,null,units,comments from structure \n"+
		"join  properties join catalog_references using(idreference)\n"+
		"where idchemical=? %s order by idstructure limit 1";
				
	protected final String where = "and %s=?";

	protected String sql() {
		return isChemicalsOnly()?
				(addNew?sql_chemical_novalue:sql_chemical):
				(addNew?sql_structure_novalue:sql_structure);
	}
	public String getSQL() throws AmbitException {
		return String.format(
				sql(),
				(getFieldname()==null || "".equals(getFieldname()))
				?"":String.format(where,searchMode.getSQL())		
				);
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, isChemicalsOnly()?getValue().getIdchemical():getValue().getIdstructure()));
		if (getFieldname()!=null)
		switch (searchMode) {
		case alias: {
			params.add(new QueryParam<String>(String.class, getFieldname().getLabel()));	
			break;
		}
		case name: {
			params.add(new QueryParam<String>(String.class, getFieldname().getName()));
			break;
		}
		case idproperty: {
			params.add(new QueryParam<Integer>(Integer.class, getFieldname().getId()));
			break;
		}
		}

		return params;		
	}

	public ResultType getObject(ResultSet rs) throws AmbitException {
		try {
			Object value = rs.getObject(5);
			
			if (value == null) {
				value = rs.getObject(6);
				value = value==null?Double.NaN:rs.getFloat(6);

			}
			else {
				if (NaN.equals(value.toString())) {

				} else {
					value = rs.getString(5);

				}
			}
			return (ResultType)value;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	public double calculateMetric(Object object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}
	@Override
	public String toString() {
		return getFieldname()==null?"Property":getFieldname().getName();

	}
	
}
