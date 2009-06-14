package ambit2.base.data;

import java.text.NumberFormat;

public class Range<T extends Comparable<T>> {
	protected T minValue = null;
	public T getMinValue() {
		return minValue;
	}
	public void setMinValue(T minValue) {
		this.minValue = minValue;
	}
	protected T maxValue = null;
	
	public Range() {
		
	}
	public Range(T value) {
		setMaxValue(value);
		setMinValue(value);
	}	
	public Range(T minValue,T maxValue) {
		setMaxValue(maxValue);
		setMinValue(minValue);
	}
	
	public T getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(T maxValue) {
		this.maxValue = maxValue;
	}
	@Override
	public String toString() {
		if (getMinValue() instanceof Number) {
			NumberFormat f = NumberFormat.getNumberInstance();
			f.setMinimumFractionDigits(0);
			f.setMaximumFractionDigits(2);
			StringBuilder b = new StringBuilder();
			b.append("(");
			b.append(f.format(getMinValue()));
			b.append(",");
			b.append(f.format(getMaxValue()));
			b.append("]");
			return b.toString();
		} else if (getMinValue().equals(getMaxValue())) return getMaxValue().toString();
		else {
			StringBuilder b = new StringBuilder();
			b.append(getMinValue());
			b.append("-");
			b.append(getMaxValue());
			return b.toString();			
			
		}
	}
}
