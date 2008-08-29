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

public class RetrieveField<F,V> extends AbstractQuery<String,IStructureRecord,EQCondition> implements IRetrieval<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7818288709974026824L;
	protected final String sql = "SELECT idstructure,d.name,f.name,value from dictionary as d right join field_names as f using(iddictionary) join structure_fields using(idfieldname) where idstructure=? ";
	public String getSQL() throws AmbitException {
		if ("".equals(getFieldname()))
			return sql;
		else
			return sql + "and f.name = ?";		
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
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
