package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

import org.openscience.cdk.interfaces.IBond;

import ambit2.db.search.NumberCondition;

/**
 * Reads structures without precalculated pairwise atom distances from the database.
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryMissingDistances extends AbstractStructureQuery<String, IBond, NumberCondition> {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 8633296656332829455L;
	public static String MISSING_DISTANCES =
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric,null as text from structure left join atom_structure using(idstructure) where (type_structure>\"2D with H\") and (iddistance is null) group by (idstructure)";
	     
	public String getSQL() throws AmbitException {
		return MISSING_DISTANCES;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		return params;
	}
	@Override
	public String toString() {

		return "Structures without calculated pairwise atom distances from database";
	}
	public long getPageSize() {
		return 0;
	}
	@Override
	public void setPageSize(long records) {
	
	}
}
