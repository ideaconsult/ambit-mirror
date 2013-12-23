package ambit2.db.update.dataset;

import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;

public class QueryCountProperties extends QueryCount {
	public QueryCountProperties(String facetURL) {
		super(facetURL);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3762221213334168489L;
	protected static String sql_properties = 
		"select 'Features',count(idproperty) from properties\n";
	@Override
	public String getSQL() throws AmbitException {
		return sql_properties;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
}
