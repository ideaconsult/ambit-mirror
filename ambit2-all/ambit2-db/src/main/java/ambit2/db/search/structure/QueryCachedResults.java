package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

/**
 * Returns results of a previously executed query (e.g. smarts)
 * @author nina
 *
 */
public class QueryCachedResults extends AbstractQuery<String, IStructureRecord, EQCondition, Boolean> 
																implements IQueryRetrieval<Boolean>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4597129739347497000L;
	public static final String sqlField=
		"select metric from query_results " +
		"join query using(idquery) join sessions using(idsessions) where " +
		"sessions.title=? and query.name=? and idchemical=?";
	protected String category;
	

	public QueryCachedResults() {
		setCondition(EQCondition.getInstance());
	}
	public QueryCachedResults(String category,String key, IStructureRecord record) {
		this();
		setFieldname(key);
		setValue(record);
	}	
	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname()==null) || (getValue()==null) || (getCategory()==null)) throw new AmbitException("Undefined parameters");
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getCategory()));
		params.add(new QueryParam<String>(String.class, getFieldname()));
		params.add(new QueryParam<Integer>(Integer.class, getValue().getIdchemical()));
		return params;
	}

	public String getSQL() throws AmbitException {
		return sqlField;
	}
	@Override
	public String toString() {
		try {
			return String.format("%s %s",getCategory(),getFieldname());
		} catch (Exception x) {
			return "cached results";
		}

	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}	
	public double calculateMetric(Boolean object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}
	public Boolean getObject(ResultSet rs) throws AmbitException {
		try {
			return rs.getBoolean(1);
		} catch (SQLException x) { throw new AmbitException(x);}
	}
}