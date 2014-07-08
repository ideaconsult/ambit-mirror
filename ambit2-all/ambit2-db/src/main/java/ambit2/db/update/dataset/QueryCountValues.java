package ambit2.db.update.dataset;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

public class QueryCountValues extends QueryCount {

	public QueryCountValues(String facetURL) {
		super(facetURL);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -4680217024248382081L;
	protected static String sql_values = 
		"select 'Property values',count(*) from property_values\n";
	@Override
	public String getSQL() throws AmbitException {
		return sql_values;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
}
