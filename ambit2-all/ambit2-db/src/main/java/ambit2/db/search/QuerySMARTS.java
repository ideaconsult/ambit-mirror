package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.smarts.query.ISmartsPattern;

/**
 * Select structures by querying fungroups table by smarts
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QuerySMARTS extends AbstractStructureQuery<String,ISmartsPattern,EQCondition> {
	public final static String sqlField = 
		"select ? as idquery,idchemical,-1,1 as selected,1 as metric from funcgroups join struc_fgroups as f using (idfuncgroup) where smarts = ?";
	public String getSQL() throws AmbitException {
		return sqlField;
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getValue().getSmarts()));
		return params;
	}
}
