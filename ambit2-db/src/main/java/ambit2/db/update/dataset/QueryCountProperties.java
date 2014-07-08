package ambit2.db.update.dataset;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

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
