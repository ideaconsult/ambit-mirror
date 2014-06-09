package ambit2.db.substance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.IFacet;
import ambit2.db.search.QueryParam;
import ambit2.db.update.dataset.QueryCount;

public class QueryCountEndpoints  extends QueryCount {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3168195512257461022L;
	public QueryCountEndpoints(String facetURL) {
		super(facetURL);
		setPageSize(Integer.MAX_VALUE);
	}
	/**
	 * 
	 */
	
	protected static String sql = 
		"SELECT e.endpoint,count(*),group_concat(distinct(unit)),e.endpointhash FROM substance_experiment e group by e.endpoint,e.hash with rollup";
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
	@Override
	public IFacet<String> getObject(ResultSet rs) throws AmbitException {
		try {
			facet.setValue(rs.getString(1) + " " + rs.getString(3));
			facet.setCount(rs.getInt(2));
			return facet;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}
