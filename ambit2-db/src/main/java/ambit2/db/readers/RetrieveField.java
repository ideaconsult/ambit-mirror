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

public class RetrieveField extends AbstractQuery<String,IStructureRecord,EQCondition,Object> implements IQueryRetrieval<Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7818288709974026824L;
	protected final String sql = 
					"select name,idreference,idproperty,idstructure,value_string,value_num,L.idtype from properties join\n"+
					"(\n"+
					"select idstructure,idproperty,null as value_string,value_num,1 as idtype from property_values where idstructure=? and value_num is not null\n"+
					"union\n"+
					"select idstructure,idproperty,value as value_string,null,0 as idtype from property_values join property_string using(idvalue_string) where idvalue_string is not null and idstructure=?\n"+
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
		for (int i=0; i < 2; i++) {
			params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
		}
		if (!"".equals(getFieldname()))
			params.add(new QueryParam<String>(String.class, getFieldname()));		
		return params;		
	}

	public Object getObject(ResultSet rs) throws AmbitException {
		try {
			switch(rs.getInt(7)) {
			case 0: { return rs.getString(5);}
			case 1: { return rs.getFloat(6);}
			default: {
				return rs.getString(5);
			}
			}
			/*
			Object o = rs.getString(5);
			if (o != null)
				return o.toString();
			else return rs.getString(6);
			*/
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
	
	
}
