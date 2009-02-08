package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IBond;

import ambit2.core.data.IStructureRecord;
import ambit2.core.exceptions.AmbitException;

/**
 * Distance between two atoms, provided as symbols.
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryDistance extends NumberQuery<IBond,IStructureRecord> {
	public final static String sqlField = 
		"select ? as idquery,-1,idstructure,1 as selected,1 as metric from atom_structure join atom_distance using(iddistance) where atom1 = ? and atom2 = ? and distance ";

	public String getSQL() throws AmbitException {
		if (NumberCondition.between.equals(getCondition().getSQL()))
			return sqlField +  getCondition().getSQL() + " ? and ?";
		else 
			return sqlField +  getCondition().getSQL() + " ?";
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getFieldname().getAtom(0).getSymbol()));
		params.add(new QueryParam<String>(String.class, getFieldname().getAtom(1).getSymbol()));
		params.add(new QueryParam<Double>(Double.class, getValue()));
		if (NumberCondition.between.equals(getCondition().getSQL()))
			params.add(new QueryParam<Double>(Double.class, getMaxValue()));
		return params;
	}
	@Override
	public boolean test(Double object) throws AmbitException {
		if (">".equals(getCondition().getSQL())) {
			return getValue().doubleValue() > object.doubleValue();
		} else if (">=".equals(getCondition().getSQL())) {
			return getValue().doubleValue() >= object.doubleValue();	
		} else if ("=".equals(getCondition().getSQL())) {
			return getValue().doubleValue() == object.doubleValue();
		} else if ("<=".equals(getCondition().getSQL())) {
			return getValue().doubleValue() <= object.doubleValue();
		} else if ("<".equals(getCondition().getSQL())) {
			return getValue().doubleValue() < object.doubleValue();
		} else if (NumberCondition.between.equals(getCondition().getSQL())) {
			return (getValue().doubleValue() <= object.doubleValue()) &&
					(getMaxValue().doubleValue() >= object.doubleValue());
		} else
			throw new AmbitException("Undefined condition "+getCondition());
	}

}
