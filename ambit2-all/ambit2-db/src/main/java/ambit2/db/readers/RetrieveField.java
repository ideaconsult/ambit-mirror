package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public class RetrieveField<F,V> extends AbstractQuery<String,IStructureRecord,EQCondition,String> implements IQueryRetrieval<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7818288709974026824L;
	protected final String sql_string = "SELECT idstructure,f.name,value from properties as f join values_string using(idproperty) where idstructure=? ";
	protected final String sql_number = "SELECT idstructure,f.name,value from properties as f join values_number using(idproperty) where idstructure=? ";
	public String getSQL() throws AmbitException {
		if ("".equals(getFieldname()))
			return sql_string + " union " + sql_number;
		else
			return sql_string + "and f.name = ?" + " union " + sql_number + "and f.name = ?";	
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
		if (!"".equals(getFieldname()))
			params.add(new QueryParam<String>(String.class, getFieldname()));		
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
		if (!"".equals(getFieldname()))
			params.add(new QueryParam<String>(String.class, getFieldname()));		
		return params;		
	}

	public String getObject(ResultSet rs) throws AmbitException {
		try {
			return rs.getString("value");
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	

}
