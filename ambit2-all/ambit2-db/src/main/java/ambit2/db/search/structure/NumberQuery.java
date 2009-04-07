package ambit2.db.search.structure;

import ambit2.db.search.NumberCondition;


public abstract class NumberQuery<F> extends AbstractStructureQuery<F,Double,NumberCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1495455998753952442L;
	protected Double maxValue;
	public Double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}
	
}
