package ambit2.db.chart;

import ambit2.db.search.IStoredQuery;

public class PieChartGeneratorQuery extends PieChartGenerator<IStoredQuery> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7892301646137116739L;
	protected static final String sql = 
		"SELECT ifnull(value_num,value) as v,count(distinct(property_values.idchemical)) as num_chemicals\n"+
		"FROM query_result join property_values using(idstructure) " +
		"left join property_string using(idvalue_string) join properties using(idproperty)\n"+
		"where idproperty=%d and idquery=%d\n"+
		"group by v\n";	
	
	@Override
	protected int getID(IStoredQuery target) {
		return target.getId();
	}
	@Override
	protected String getSQL() {
		return sql;
	}
}
