package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.AmbitUser;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.EQCondition;
import ambit2.db.search.QueryParam;

public class QueryStructureByQualityPairLabel extends 	AbstractStructureQuery<AmbitUser, Integer, EQCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3232148472829083139L;
	/*
	public final static String sql = 
		"select ? as idquery,idchemical,idstructure1,min(same) as selected,sum(same) as metric from quality_pair\n"+
		"%s\n"+
		"group by idchemical\n";
	*/
	public final static String sql = 
		"select ? as idquery,idchemical,idstructure1,same as selected,same as metric from quality_pair\n"+
		"%s\n"+
		"order by idchemical\n";	
	public final static String where = " where same %s ?";
	
	/*
select selected,idchemical,pairs,consensus,pairs-consensus as diff, consensus/pairs from (
SELECT idchemical,count(*) pairs,min(same) selected,sum(same) consensus FROM quality_pair q
group by idchemical
) as d
order by diff,1/pairs
	 */

	public QueryStructureByQualityPairLabel() {
		setFieldname(new AmbitUser("quality"));
		setValue(null);
		setCondition(EQCondition.getInstance());
	}
	
	public QueryStructureByQualityPairLabel(Integer label) {
		setFieldname(new AmbitUser("quality"));
		setValue(label);
		setCondition(EQCondition.getInstance());
	}	

	public String getSQL() throws AmbitException {
		if (getValue()==null)
			return String.format(sql,"");
		else
			if (getValue()!=null)
				return  String.format(String.format(sql,where),getCondition().getSQL());
			else
				return  String.format(sql,"");
	}

	public List<QueryParam> getParameters() throws AmbitException {
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if (getValue()!=null)
			params.add(new QueryParam<Integer>(Integer.class, getValue()));
		return params;
	}
	@Override  
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getFieldname() + "quality");
		if (getValue()!=null) {
			b.append(getCondition().toString());
			b.append(getValue());
		}
		return b.toString();
	}
	@Override
	public Object retrieveValue(ResultSet rs) throws SQLException {
		if (rs.getObject(5)!=null)
			return new QLabel(QUALITY.values()[rs.getInt(5)-1]);
		else return null;
	}
	

}
