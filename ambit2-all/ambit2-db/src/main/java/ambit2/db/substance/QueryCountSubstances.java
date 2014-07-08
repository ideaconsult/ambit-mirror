package ambit2.db.substance;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.update.dataset.QueryCount;

public class QueryCountSubstances extends QueryCount {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3168195512257461022L;
	public QueryCountSubstances(String facetURL) {
		super(facetURL);
	}
	/**
	 * 
	 */
	
	protected static String sql_substances = 
		"select 'Substances',count(*) from substance\n";
	@Override
	public String getSQL() throws AmbitException {
		return sql_substances;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
}
