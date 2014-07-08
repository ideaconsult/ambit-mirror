package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

import org.openscience.cdk.interfaces.IBond;

import ambit2.db.search.NumberCondition;

/**
 * Distance between two atoms, provided as symbols.
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QueryDistance extends AbstractStructureQuery<IBond,Double,NumberCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5464266255373802342L;
	public final static String sqlField = 
		"select ? as idquery,-1,idstructure,1 as selected,1 as metric,null as text from atom_structure join atom_distance using(iddistance) where atom1 = ? and atom2 = ? and distance ";

	protected Double maxValue;
	public Double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}	
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
