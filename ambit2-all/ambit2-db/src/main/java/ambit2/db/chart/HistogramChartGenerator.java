package ambit2.db.chart;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;

public class HistogramChartGenerator extends BarChartGeneratorDataset {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8611312548411533271L;

	
	protected final static String histogram_dataset = 
			"select format(a,2) ,sum(if(((%s>=a) and (%s < b)),1,0)) as num_chemicals from property_values\n"+
			"join struc_dataset using(idstructure)\n"+
			"join\n"+
			"(\n"+
			"select m1+d*(m2-m1)/19 as a, m1+(d+1)*(m2-m1)/19 as b,d from\n"+
			"(select min(%s) as m1,max(%s) as m2 from property_values\n"+
			"join struc_dataset using(idstructure)\n"+
			"where idproperty=%d and value_num is not null and id_srcdataset=%d\n"+
			")\n"+
			"as L\n"+
			"join (\n"+
			"select 0 as d\n"+
			"union select 1 as d\n"+
			"union select 2 as d\n"+
			"union select 3 as d\n"+
			"union select 4 as d\n"+
			"union select 5 as d\n"+
			"union select 6 as d\n"+
			"union select 7 as d\n"+
			"union select 8 as d\n"+
			"union select 9 as d\n"+
			"union select 10 as d\n"+
			"union select 11 as d\n"+
			"union select 12 as d\n"+
			"union select 13 as d\n"+
			"union select 14 as d\n"+
			"union select 15 as d\n"+
			"union select 16 as d\n"+
			"union select 17 as d\n"+
			"union select 18 as d\n"+
			"union select 19 as d\n"+
			"union select 20 as d\n"+			
			") as M\n"+
			") as BINS\n"+
			"where idproperty=%d and value_num is not null and id_srcdataset=%d\n"+
			"group by d\n";
	
	protected final static String histogram_query = 
			"select format(a,2),sum(if(((%s>=a) and (%s < b)),1,0)) as num_chemicals from property_values\n"+
			"join query_results using(idstructure)\n"+
			"join\n"+
			"(\n"+
			"select m1+d*(m2-m1)/19 as a, m1+(d+1)*(m2-m1)/19 as b,d from\n"+
			"(select min(%s) as m1,max(%s) as m2 from property_values\n"+
			"join query_results using(idstructure)\n"+
			"where idproperty=%d and value_num is not null and idquery=%d\n"+
			")\n"+
			"as L\n"+
			"join (\n"+
			"select 0 as d\n"+
			"union select 1 as d\n"+
			"union select 2 as d\n"+
			"union select 3 as d\n"+
			"union select 4 as d\n"+
			"union select 5 as d\n"+
			"union select 6 as d\n"+
			"union select 7 as d\n"+
			"union select 8 as d\n"+
			"union select 9 as d\n"+
			"union select 10 as d\n"+
			"union select 11 as d\n"+
			"union select 12 as d\n"+
			"union select 13 as d\n"+
			"union select 14 as d\n"+
			"union select 15 as d\n"+
			"union select 16 as d\n"+
			"union select 17 as d\n"+
			"union select 18 as d\n"+
			"union select 19 as d\n"+
			"union select 20 as d\n"+			
			") as M\n"+
			") as BINS\n"+
			"where idproperty=%d and value_num is not null and idquery=%d\n"+
			"group by d\n";

	@Override
	protected String getSQL(ISourceDataset target) {
		String sql =  target instanceof SourceDataset?histogram_dataset:histogram_query;
		String value_num = logX?"log(value_num)":"value_num";
		return String.format(sql,
				value_num,value_num,
				minX==null?value_num:String.format("%f",minX),
				maxX==null?value_num:String.format("%f",maxX),
				propertyX.getId(),getID(target),propertyX.getId(),getID(target)
				); 
	}

	@Override
	protected String getCategoryTitle(ISourceDataset target) {
		return logX?
				String.format("log(%s) ranges", propertyX.getName()):
			String.format("%s ranges", propertyX.getName());
	}
	

}
