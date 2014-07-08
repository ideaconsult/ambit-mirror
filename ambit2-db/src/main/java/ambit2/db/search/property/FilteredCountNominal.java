package ambit2.db.search.property;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

public class FilteredCountNominal extends FilteredCount<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8082889518227668528L;

	protected String sql = 
		"select count(idstructure) as c from properties\n"+
		"join property_values using(idproperty)\n"+
		"join property_string using(idvalue_string)\n"+
		"join query_results using(idstructure)\n"+
		"where idvalue_string is not null and name=? %s %s %s";
	protected String sql_query="and idquery=?";
	protected String sql_value= "and value = ?";
	protected String sql_metric= "and metric > ? and metric <= ?";
	
	public String getSQL() throws AmbitException {
		return String.format(sql,
				getQuery() == null?"":sql_query,
				getMetric() == null?"":sql_metric,
				getValue()==null?"":sql_value
				);
		
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> p = super.getParameters();
		if (getValue()!=null)
		p.add(new QueryParam<String>(String.class,getValue().getMinValue()));
		return p;
	}

}
