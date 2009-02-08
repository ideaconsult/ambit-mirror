package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;

public class QueryStored extends AbstractQuery<String,IStoredQuery,EQCondition,IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1485698692883271843L;
	public final static String sqlField = 
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from query_results where idquery = ?";
	
	public String getSQL() throws AmbitException {
		return sqlField;
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<Integer>(Integer.class, getValue().getId()));
		return params;
	}

}
