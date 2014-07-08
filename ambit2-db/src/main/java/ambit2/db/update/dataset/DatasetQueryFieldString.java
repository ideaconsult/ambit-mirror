package ambit2.db.update.dataset;

import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.StringCondition;

public class DatasetQueryFieldString extends DatasetQueryFieldGeneric<String,StringCondition> {

	public final static String sqlField = 
		"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n" +	
		"FROM structure s1\n"+
		
		"join struc_dataset using(idstructure) join property_values using(idstructure)\n"+
		"join property_string using(idvalue_string) where\n"+
		" id_srcdataset = ? and idproperty = ? and value = ?";

	public final static String sqlField_Rdatasets = 
		"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n" +	
		"FROM structure s1\n"+
		"join query_results using(idstructure) join property_values using(idstructure)\n"+
		"join property_string using(idvalue_string) where\n"+
		"idquery = ? and idproperty = ? and value = ?";	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4878852850705024770L;

	@Override
	protected QueryParam<String> createSearchValueParam() {
		return new QueryParam<String>(String.class, getValue().getValue());
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
