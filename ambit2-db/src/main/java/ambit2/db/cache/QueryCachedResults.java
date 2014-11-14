package ambit2.db.cache;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;

public abstract class QueryCachedResults<T>  extends AbstractQuery<String, IStructureRecord, EQCondition, T> 
								implements IQueryRetrieval<T>{
	
	public static final String sqlField=
		"select %s from query_results " +
		"join query using(idquery) join sessions using(idsessions) where " +
		"sessions.title=? and query.name=? and idchemical=?";
	protected String category;
	protected String what = "metric,text";
	/**
	 * 
	 */
	private static final long serialVersionUID = 4381740451274951526L;


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
	public double calculateMetric(T object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}	
	public String getSQL() throws AmbitException {
		return String.format(sqlField,what);
	}
	@Override
	public String toString() {
		try {
			return String.format("%s %s",getCategory(),getFieldname());
		} catch (Exception x) {
			return "cached results";
		}

	}	
}
