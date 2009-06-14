package ambit2.base.data;

import java.text.NumberFormat;

import com.jgoodies.binding.beans.Model;

public class PropertyStats<T extends Comparable<T>> extends Model{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8407181000179501113L;
	protected Range<T> range = new Range<T>();
	protected T avg = null;
	protected long count;
	
	public T getMin() {
		return range.getMinValue();
	}
	public void setMin(T min) {
		range.setMinValue(min);
	}
	public T getMax() {
		return range.getMaxValue();
	}
	public void setMax(T max) {
		range.setMaxValue(max);
	}
	public T getAvg() {
		return avg;
	}
	public void setAvg(T avg) {
		this.avg = avg;
	}

	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getCount());
		if (getAvg() != null) {
			b.append(" [");
			b.append(NumberFormat.getInstance().format(getAvg()));
			b.append("]");
		}
		return b.toString();
	}
}
