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

	protected static String sql_datasets = 
	"select count(distinct(s1.idchemical))\n"+
	"from structure s1 join struc_dataset d1 on d1.idstructure=s1.idstructure\n"+
	"join structure s2 on s1.idchemical=s2.idchemical\n"+
	"join struc_dataset d2 on d2.idstructure=s2.idstructure\n"+
	"where d1.id_srcdataset=? and d2.id_srcdataset=?\n";
	
	protected String QR_PREFIX = "R";
	
	public QueryCountDatasetIntersection() {
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
	/*
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
	*/
	public String getSQL() throws AmbitException {
		return sql_datasets;
	}

	public String getObject(ResultSet rs) throws AmbitException {
		try {
			return rs.getString(1);
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

}
