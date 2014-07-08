package ambit2.db.search.structure.byproperty;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.structure.QueryDatasetByID;

public class QueryStructureByPropertyInDataset extends	QueryStructureByProperty<QueryDatasetByID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2211443189914356410L;
	protected static String sql_dataset =
		"select ? as idquery,s.idchemical,s.idstructure,1 as selected,preference as metric,type_structure as text from structure s\n"+
		"join struc_dataset using(idstructure)\n"+
		"where s.idchemical %s\n"+
		"(select idchemical from property_values \n"+
		"where idproperty in (%s))\n"+
		"and struc_dataset.id_srcdataset=?";	
	
	
	@Override
	protected void addStructureParam(List<QueryParam> params)
			throws AmbitException {
		if ((getValue()==null) || (getValue().getValue() <=0)) throw new AmbitException("No structures defined");
		
		params.add(new QueryParam<Integer>(Integer.class, getValue().getValue()));
	}

	@Override
	protected String getSQLStructureQuery() throws AmbitException {
		return "";
	}

	@Override
	protected String getSQLTemplate() throws AmbitException {
		return sql_dataset;
	}

}
