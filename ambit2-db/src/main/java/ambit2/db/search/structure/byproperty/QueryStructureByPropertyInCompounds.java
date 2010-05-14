package ambit2.db.search.structure.byproperty;

import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.search.structure.QueryStructureByID;

public class QueryStructureByPropertyInCompounds  extends	QueryStructureByProperty<QueryStructureByID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5478831682253837878L;
	protected static String sql_compounds =
		"select ? as idquery,s.idchemical,s.idstructure,1 as selected,preference as metric,type_structure as text from structure s\n"+
		"where s.idchemical %s\n"+
		"(select idchemical from property_values join structure using(idstructure)\n"+
		"where idproperty in (%s))\n"+
		"and s.idchemical in (%s)";
	@Override
	protected void addStructureParam(List<QueryParam> params)
			throws AmbitException {
	}

	@Override
	protected String getSQLStructureQuery() throws AmbitException {
		return String.format("%d", getValue().getValue().getIdchemical());
	}

	@Override
	protected String getSQLTemplate() throws AmbitException {
		return sql_compounds;
	}

}
