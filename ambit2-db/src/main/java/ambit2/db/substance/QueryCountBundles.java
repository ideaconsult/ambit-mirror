package ambit2.db.substance;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.update.dataset.QueryCount;

public class QueryCountBundles extends QueryCount {

	public QueryCountBundles(String facetURL) {
		super(facetURL);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -4268694398905885170L;
	protected static String sql_values = 
		"select 'Number of substance datasets',count(*) from bundle\n";
	
	
	@Override
	public String getSQL() throws AmbitException {
		return sql_values;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
}
