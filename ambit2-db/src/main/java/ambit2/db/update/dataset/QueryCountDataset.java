package ambit2.db.update.dataset;

import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;

public class QueryCountDataset extends QueryCount {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4268694398905885170L;
	protected static String sql_values = 
		"select count(*) from src_dataset\n";
	@Override
	public String getSQL() throws AmbitException {
		return sql_values;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
}
