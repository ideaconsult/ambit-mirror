package ambit2.db.model;

import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.dataset.QueryCount;

/**
 * Number of models
 * @author nina
 *
 */
public class QueryCountModels extends QueryCount {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2826870937106055180L;
	protected static String sql_models = 
		"select count(*) from models\n";
	@Override
	public String getSQL() throws AmbitException {
		return sql_models;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
}
