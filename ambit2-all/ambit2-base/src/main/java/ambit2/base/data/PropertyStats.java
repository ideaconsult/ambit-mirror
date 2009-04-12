package ambit2.base.data;

import com.jgoodies.binding.beans.Model;

public class PropertyStats extends Model{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8407181000179501113L;
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getAvg() {
		return avg;
	}
	public void setAvg(double avg) {
		this.avg = avg;
	}
	protected double min;
	protected double max;
	protected double avg;
	protected long count;
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return getMin() + "," + getMax();
	}
}
