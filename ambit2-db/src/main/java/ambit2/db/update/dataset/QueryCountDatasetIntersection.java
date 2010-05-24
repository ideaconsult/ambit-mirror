package ambit2.db.update.dataset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

/**
 * 
 * @author nina
 *
 */
public class QueryCountDatasetIntersection extends AbstractQuery<String, String, StringCondition, String> implements IQueryRetrieval<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2679397674845722288L;

	protected static String sql = 
	"SELECT count(distinct(idchemical)) FROM\n"+
	"(%s) s1\n"+
	"join\n"+
	"(%s) s2\n"+
	"using(idchemical)";
	
	protected static String sql_dataset = "select idchemical from struc_dataset join structure using(idstructure) where id_srcdataset=?";
	protected static String sql_query = "select idchemical from query_results where idquery=?";
	protected String QR_PREFIX = "R";
	
	public double calculateMetric(String object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getParam(getFieldname())));
		params.add(new QueryParam<Integer>(Integer.class, getParam(getValue())));
		return params;
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
	protected String getSubQuery(String key) throws AmbitException {
		
		if (key.startsWith(QR_PREFIX)) {
			key = key.substring(QR_PREFIX.length());
			try {
				Integer.parseInt(key.toString());
				return sql_query;
			} catch (NumberFormatException x) {
				throw new AmbitException("Invalid id "+key);
			}
		} else try { //dataset
			Integer.parseInt(key.toString());
			return sql_dataset;
		} catch (NumberFormatException x) {
			throw new AmbitException("Invalid id "+key);
		}
	}

	public String getSQL() throws AmbitException {
		return String.format(sql, getSubQuery(getFieldname()),getSubQuery(getValue()) );
	}

	public String getObject(ResultSet rs) throws AmbitException {
		try {
			return rs.getString(1);
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

}
