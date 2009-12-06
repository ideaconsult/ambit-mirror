package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

public class QueryStructureByQualityPairLabel extends 	AbstractStructureQuery<String, String, StringCondition> {
	public static enum CONSENSUS_LABELS {Consensus,Majority,Unconfirmed,Ambiguous,Unknown};
	/**
	 * 
	 */
	private static final long serialVersionUID = -3232148472829083139L;

	public final static String sql = 
		"select ? as idquery,idchemical,idstructure,case 1 when (q.label='Consensus') then 1 else 0 end as selected,cast(q.label as unsigned) as metric,q.label from\n"+
		"quality_chemicals q join structure using(idchemical) where q.label = ? %s\n"+
		"order by idchemical\n";	
	public final static String where = " and text = ?";

	public QueryStructureByQualityPairLabel() {
		this("Consensus");
	}
	
	public QueryStructureByQualityPairLabel(String label) {
		setFieldname(label);
		setValue(null);
		setCondition(StringCondition.getInstance("="));
	}	

	public String getSQL() throws AmbitException {
		String result = null;
		if (getValue()==null)
			result = String.format(sql,"");
		else
			if (getValue()!=null)
				result =   String.format(sql,where);
			else
				result =  String.format(sql,"");
		//System.out.println(result);
		return result;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		/*
		"select ? as idquery,idchemical,idstructure,case 1 when (q.label='Consensus') then 1 else 0 end as selected,cast(q.label as unsigned) as metric from\n"+
		"quality_chemicals q join structure using(idchemical) where q.label = ? %s\n"+
		"order by idchemical\n";	
		 */
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if (getFieldname()==null) throw new AmbitException("No label!");
		params.add(new QueryParam<String>(String.class,getFieldname()));
		
		if (getValue()!=null)
			params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}
	@Override  
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getFieldname());
		b.append(getValue()==null?"":getValue());
		if (getValue()!=null) {
			b.append(getCondition().toString());
			b.append(getValue());
		}
		return b.toString();
	}
	@Override
	public Object retrieveValue(ResultSet rs) throws SQLException {
		if (rs.getObject(5)!=null)
			
			return CONSENSUS_LABELS.values()[rs.getInt(5)-1];
		else return null;
	}
	

}
