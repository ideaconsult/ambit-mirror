package ambit2.db.update.dataset;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;

public class QueryCountDataset extends QueryCount {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6955218726720517180L;
	protected static String sql_datasets = 
		"select count(distinct(s1.idchemical))\n"+
		"from structure s1 join struc_dataset d1 on d1.idstructure=s1.idstructure\n"+
		"where d1.id_srcdataset=? \n";
		
	protected static String sql_chemicals = 
		"select count(idchemical) from chemicals\n";
	
	public List<QueryParam> getParameters() throws AmbitException {
		if(getFieldname()==null) return null;
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getParam(getFieldname())));
		return params;
	}

	public String getSQL() throws AmbitException {
		return getFieldname()==null?sql_chemicals:sql_datasets;
	}

}
