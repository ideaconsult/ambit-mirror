package ambit2.db.search.structure.byproperty;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.structure.QueryStoredResults;

public class QueryStructuresByPropertyInResults extends	QueryStructureByProperty<QueryStoredResults> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8622643751238845834L;
	protected static String sql_query =
		"select ? as idquery,s.idchemical,s.idstructure,1 as selected,preference as metric,type_structure as text from structure s\n"+
		"join query_results using(idstructure)\n"+
		"where s.idchemical %s\n"+
		"(select idchemical from property_values \n"+
		"where idproperty in (%s))\n"+
		"and query_results.idquery=?";
	@Override
	protected void addStructureParam(List<QueryParam> params)
			throws AmbitException {
		if ((getValue()==null) ||
			(getValue().getFieldname()==null) ||
			(getValue().getFieldname().getId()<=0)) throw new AmbitException("No structures defined");
		
		params.add(new QueryParam<Integer>(Integer.class, getValue().getFieldname().getId()));
		
	}

	@Override
	protected String getSQLStructureQuery() throws AmbitException {
		return "";
	}

	@Override
	protected String getSQLTemplate() throws AmbitException {
		return sql_query;
	}

}
