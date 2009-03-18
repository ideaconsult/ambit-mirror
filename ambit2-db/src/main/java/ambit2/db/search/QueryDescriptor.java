package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.exceptions.AmbitException;

public class QueryDescriptor extends AbstractStructureQuery<String,Double,NumberCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8053931040773141051L;
	public final static String sqlField = 
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from values_int_float join properties as d using (idproperty) join structure using(idstructure) where d.name=? and value ";
	
	protected Double maxValue;
	public Double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}
	public String getSQL() throws AmbitException {
		if (NumberCondition.getInstance(NumberCondition.between).equals(getCondition()))
			return sqlField +  getCondition().getSQL() + " ? and ?";
		else 
			return sqlField +  getCondition().getSQL() + " ?";
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<String>(String.class, getFieldname()));
		params.add(new QueryParam<Double>(Double.class, getValue()));
		if (NumberCondition.between.equals(getCondition().getSQL()))
			params.add(new QueryParam<Double>(Double.class, getMaxValue()));
		return params;
	}
	@Override
	public String toString() {
		if (NumberCondition.getInstance(NumberCondition.between).equals(getCondition()))
			return super.toString() + " and " + getMaxValue();
		else
			return super.toString();
	}

}
