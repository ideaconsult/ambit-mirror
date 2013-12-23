package ambit2.db.update.dataset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.AbstractFacet;
import ambit2.base.facet.IFacet;
import ambit2.db.facets.AbstractFacetQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

/**
 * Number of chemicals
 * @author nina
 *
 */
public class QueryCount  extends AbstractFacetQuery<String,String,StringCondition,IFacet<String>>  {

	/**
	 * 
	 */

	protected static String sql_structures = "select 'Number of structures',count(idstructure) from structure\n";

	protected AbstractFacet facet;
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
	

	public String getSQL() throws AmbitException {
		return sql_structures;
	}
	protected String QR_PREFIX = "R";
	
	public QueryCount(String facetURL) {
		super(facetURL);
		 facet = new AbstractFacet(facetURL) {
			 public  String getResultsURL(String[] params) {
				 return params[0] + (url==null?"":url);
			 };
		 };	
		setPageSize(1);
		setPage(0);
	}
	
	@Override
	public double calculateMetric(IFacet<String> object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}
	
	protected int getParam(String key) throws AmbitException {
		
		if (key.startsWith(QR_PREFIX)) {
			key = key.substring(QR_PREFIX.length());
			try {
				return Integer.parseInt(key.toString());
			} catch (NumberFormatException x) {
				throw new AmbitException("Invalid id "+key);
			}
		} else try { //dataset
			return Integer.parseInt(key.toString());
		} catch (NumberFormatException x) {
			throw new AmbitException("Invalid id "+key);
		}
	}	
	
	@Override
	public IFacet<String> getObject(ResultSet rs) throws AmbitException {
		try {
			facet.setValue(rs.getString(1));
			facet.setCount(rs.getInt(2));
			return facet;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	
	
}
