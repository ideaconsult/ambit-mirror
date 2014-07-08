package ambit2.db.update.dataset;

import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.NumberCondition;

public class DatasetQueryFieldNumeric extends DatasetQueryFieldGeneric<Double,NumberCondition> {

	public final static String sqlField = 
		"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n" +	
		"FROM structure s1\n"+
		
		"join struc_dataset using(idstructure) join property_values using(idstructure)\n"+
		"where\n"+
		" id_srcdataset = ? and idproperty = ? and value_num %s ?";

	public final static String sqlField_Rdatasets = 
		"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n" +	
		"FROM structure s1\n"+
		"join query_results using(idstructure) join property_values using(idstructure)\n"+
		"where\n"+
		"idquery = ? and idproperty = ? and value_num %s ?";	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4878852850705024770L;

	public DatasetQueryFieldNumeric() {
		super();
		setCondition(NumberCondition.getInstance("="));
	}
	@Override
	protected QueryParam<Double> createSearchValueParam() {
		return new QueryParam<Double>(Double.class, getValue().getValue().doubleValue());
	}
	@Override
	protected String getSqlDataset() {
		return String.format(sqlField,getCondition().getSQL());
	}
	@Override
	protected String getSqlRDataset() {
		return String.format(sqlField_Rdatasets,getCondition().getSQL());
	}
}
