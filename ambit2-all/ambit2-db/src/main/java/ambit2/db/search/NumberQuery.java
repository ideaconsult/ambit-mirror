package ambit2.db.search;


public abstract class NumberQuery<F,ResultType> extends AbstractQuery<F,Double,NumberCondition,ResultType> {

	protected Double maxValue;
	public Double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}
	
}
