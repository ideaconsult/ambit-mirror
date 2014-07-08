package ambit2.db.search.property;

import java.sql.ResultSet;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.PropertyStats;

/*
*/
/*
 * Property
 * Property value - numeric condition
 * Metric - numeric condition
 * Query id - IStoredQuery ?
 * 
 */
public class FilteredCountNumeric extends FilteredCount<Double> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8780107461721242518L;


	protected String sql = 
		"select count(idstructure) as c,avg(value_num) from properties\n"+
		"join property_values using(idproperty)\n"+
		"join query_results using(idstructure)\n"+
		"where value_num is not null and name=? %s %s %s";
	protected String sql_query="and idquery=?";
	protected String sql_value= "and value_num > ? and value_num <= ?";
	protected String sql_metric= "and metric > ? and metric <= ?";


	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> p = super.getParameters();
		if (getValue()!=null) {
			p.add(new QueryParam<Double>(Double.class,getValue().getMinValue()));
			p.add(new QueryParam<Double>(Double.class,getValue().getMaxValue()));
		}
		return p;
	}
	public String getSQL() throws AmbitException {
		return String.format(sql,
				getQuery() == null?"":sql_query,
				getMetric() == null?"":sql_metric,
				getValue()==null?"":sql_value
				);
		
	}
	@Override
	public PropertyStats<Double> getObject(ResultSet rs) throws AmbitException {
		PropertyStats<Double> stats = super.getObject(rs);
		try {
			stats.setAvg(rs.getDouble(2));
		} catch (Exception x) {
			
		}
		return stats;
	}

}
