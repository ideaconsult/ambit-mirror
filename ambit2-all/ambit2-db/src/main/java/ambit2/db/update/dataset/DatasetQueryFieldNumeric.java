package ambit2.db.update.dataset;

import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;

public class DatasetQueryFieldNumeric extends DatasetQueryFieldGeneric<Double,NumberCondition> {

	public final static String sqlField = 
		"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n" +	
		"FROM structure s1\n"+
		
		"join struc_dataset using(idstructure) join property_values using(idstructure)\n"+
		"where\n"+
		" id_srcdataset = ? and idproperty = ? and value_num = ?";

	public final static String sqlField_Rdatasets = 
		"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n" +	
		"FROM structure s1\n"+
		"join query_results using(idstructure) join property_values using(idstructure)\n"+
		"where\n"+
		"idquery = ? and idproperty = ? and value = ?";	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4878852850705024770L;

	@Override
	protected QueryParam<Double> createSearchValueParam() {
		return new QueryParam<Double>(Double.class, getValue().getValue().doubleValue());
	}
	@Override
	protected String getSqlDataset() {
		return sqlField;
	}
	@Override
	protected String getSqlRDataset() {
		return sqlField_Rdatasets;
	}
}
