package ambit2.db.update.dataset;

import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;

public class QueryCountValues extends QueryCount {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4680217024248382081L;
	protected static String sql_values = 
		"select count(*) from property_values\n";
	@Override
	public String getSQL() throws AmbitException {
		return sql_values;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
}
