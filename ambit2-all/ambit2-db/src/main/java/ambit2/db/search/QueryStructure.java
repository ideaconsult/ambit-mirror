package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.exceptions.AmbitException;

/**
 * Search for smiles, inchi, formula
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryStructure extends AbstractQuery<String,String,StringCondition> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1446001922520148275L;

	public QueryStructure() {
		setCondition(StringCondition.getInstance("="));
	}

	
	public final static String sqlSMILES = 
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metrics from structure join chemicals using(idchemical) where ";
	
	public String getSQL() throws AmbitException {
		return sqlSMILES + getFieldname() + " " + getCondition().getSQL() + " ?";
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}

}
