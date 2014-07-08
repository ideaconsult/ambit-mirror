package ambit2.db.search.structure.byproperty;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.structure.QueryStructureByID;

public class QueryStructureByPropertyInCompounds  extends	QueryStructureByProperty<QueryStructureByID> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5478831682253837878L;
	protected static String sql_compounds =

		"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n" +	
		"FROM structure s1\n"+
		"LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical  AND (1E10*s1.preference+s1.idstructure) > (1E10*s2.preference+s2.idstructure)\n"+
		"where s2.idchemical is null\n"+	
		"and s1.idchemical %s\n"+
		"(select idchemical from property_values\n"+
		"where idproperty in (%s))\n"+
		"and s1.idchemical in (%s)";	
	@Override
	protected void addStructureParam(List<QueryParam> params)
			throws AmbitException {
		if (getValue()==null) throw new AmbitException("No structures defined");
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
