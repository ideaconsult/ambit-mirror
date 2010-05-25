package ambit2.db.update.dataset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

/**
 * Number of chemicals
 * @author nina
 *
 */
public class QueryCount  extends AbstractQuery<String, String, StringCondition, String> implements IQueryRetrieval<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3106358743355982496L;
	protected static String sql_structures = 
		"select count(idstructure) from structure\n";
		
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}
	

	public String getSQL() throws AmbitException {
		return sql_structures;
	}
	protected String QR_PREFIX = "R";
	
	public QueryCount() {
		super();
		setPageSize(1);
		setPage(0);
	}
	public double calculateMetric(String object) {
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
	public String getObject(ResultSet rs) throws AmbitException {
		try {
			return rs.getString(1);
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}
