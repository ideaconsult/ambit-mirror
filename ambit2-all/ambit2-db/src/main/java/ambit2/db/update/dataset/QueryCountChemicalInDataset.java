package ambit2.db.update.dataset;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.facet.AbstractFacet;

public class QueryCountChemicalInDataset extends QueryCount<AbstractFacet<String>> {

	public QueryCountChemicalInDataset(String facetURL) {
		super(facetURL);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6955218726720517180L;
	protected static String sql_datasets = 
		"select concat('Chemicals in /dataset/',d1.id_srcdataset),count(distinct(s1.idchemical))\n"+
		"from structure s1 join struc_dataset d1 on d1.idstructure=s1.idstructure\n"+
		"where d1.id_srcdataset=? \n";
		
	protected static String sql_chemicals = 
		"select 'Number of chemicals',count(idchemical) from chemicals\n";
	
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if(getFieldname()==null) return null;
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getParam(getFieldname())));
		return params;
	}
	@Override
	public String getSQL() throws AmbitException {
		return getFieldname()==null?sql_chemicals:sql_datasets;
	}

}
