package ambit2.db.chart;

import ambit2.base.data.SourceDataset;

public class PieChartGeneratorDataset extends PieChartGenerator<SourceDataset> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3387186079863251884L;
	protected static final String sql = 
		"SELECT ifnull(value_num,value) as v,count(distinct(structure.idchemical)) as num_chemicals\n"+
		"FROM struc_dataset join structure using(idstructure) join property_values using(idstructure) " +
		"left join property_string using(idvalue_string) join properties using(idproperty)\n"+
		"where idproperty=%d and id_srcdataset=%d\n"+
		"group by v\n";	
	
	@Override
	protected int getID(SourceDataset target) {
		return target.getId();
	}
	@Override
	protected String getSQL() {
		return sql;
	}
}
