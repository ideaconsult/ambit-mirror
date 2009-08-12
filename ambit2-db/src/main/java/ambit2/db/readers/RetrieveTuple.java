package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public class RetrieveTuple<ResultType> extends AbstractQuery<Integer,IStructureRecord,EQCondition,ResultType> implements IQueryRetrieval<ResultType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7375755539942986234L;
	protected boolean chemicalsOnly = true;
	public boolean isChemicalsOnly() {
		return chemicalsOnly;
	}

	public void setChemicalsOnly(boolean chemicalsOnly) {
		this.chemicalsOnly = chemicalsOnly;
	}

	protected String sql_chemicals = 
		"select name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,avg(value_num) as value_num,title,url,idchemical,id,idtuple from property_values \n"+
		"left join property_tuples using(id)\n"+
		"left join property_string using(idvalue_string)\n"+
		"join properties using(idproperty)\n"+
		"join structure using (idstructure)\n"+
		"join catalog_references using(idreference)\n"+
		"where idchemical=? %s group by idproperty";
	protected String sql_structure = 
		"select name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,value_num,title,url,-1,id,idtuple from property_values \n"+
		"left join property_tuples using(id)\n"+
		"left join property_string using(idvalue_string)\n"+
		"join properties using(idproperty)\n"+
		"join catalog_references using(idreference)\n"+
		"where idstructure=? %s";
	protected String sql_where = "and idtuple=?";
	protected String sql_tuplenull = "and idtuple is null";
	public double calculateMetric(ResultType object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) throw new AmbitException("Structure not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, isChemicalsOnly()?getValue().getIdchemical():getValue().getIdstructure()));

		if (getFieldname()!=null)
			params.add(new QueryParam<Integer>(Integer.class, getFieldname()));		
		return params;	
	}

	public String getSQL() throws AmbitException {
		if (getValue()==null) throw new AmbitException("Structure not defined");
		return String.format(
				isChemicalsOnly()?sql_chemicals:sql_structure,
				getFieldname()==null?sql_tuplenull:sql_where
				);
	}

	public ResultType getObject(ResultSet rs) throws AmbitException {
		try {
			Object value = rs.getObject(5);
			if (value == null) value = rs.getFloat(6);
			return (ResultType)value;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	
}
