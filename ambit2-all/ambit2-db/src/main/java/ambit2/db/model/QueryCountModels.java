package ambit2.db.model;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.update.dataset.QueryCount;

/**
 * Number of models
 * @author nina
 *
 */
public class QueryCountModels extends QueryCount {

	public QueryCountModels(String facetURL) {
		super(facetURL);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 2826870937106055180L;
	protected static String sql_models = 
		"select 'Models',count(*) from models\n";
	@Override
	public String getSQL() throws AmbitException {
		return sql_models;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
}
