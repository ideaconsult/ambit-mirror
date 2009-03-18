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
	protected final String sql = 
					"select name,idreference,idproperty,idstructure,value,idtype from properties join\n"+
					"(\n"+
					"select idstructure,idproperty,value,idtype from values_int where idstructure=?\n"+
					"union\n"+
					"select idstructure,idproperty,value,idtype from values_number where idstructure=?\n"+
					"union\n"+
					"select idstructure,idproperty,value,idtype from values_string where idstructure=?\n"+
					") as L using (idproperty)\n";
	protected final String where = "where name=?";
	
	public String getSQL() throws AmbitException {
		if ("".equals(getFieldname()))
			return sql
;
		else
			return sql + where;	
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		for (int i=0; i < 3; i++) {
			params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
		}
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
