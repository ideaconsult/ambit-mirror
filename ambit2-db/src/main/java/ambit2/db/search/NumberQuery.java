package ambit2.db.search;


public abstract class NumberQuery<F> extends AbstractQuery<F,Double,NumberCondition> {

	protected Double maxValue;
	public Double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}
	
}
