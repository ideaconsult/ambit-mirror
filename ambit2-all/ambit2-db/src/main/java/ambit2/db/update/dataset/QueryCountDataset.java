package ambit2.db.update.dataset;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

public class QueryCountDataset extends QueryCount {

	public QueryCountDataset(String facetURL) {
		super(facetURL);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -4268694398905885170L;
	protected static String sql_values = 
		"select 'Number of datasets',count(*) from src_dataset\n";
	
	
	@Override
	public String getSQL() throws AmbitException {
		return sql_values;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
}
