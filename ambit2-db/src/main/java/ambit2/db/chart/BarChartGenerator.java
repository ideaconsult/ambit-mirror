package ambit2.db.chart;

import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;

public abstract class BarChartGenerator<T extends ISourceDataset> extends ChartGenerator<T> {

	protected Double minX = null;
	public Double getMinX() {
		return minX;
	}

	public void setMinX(Double minX) {
		this.minX = minX;
	}

	protected Double maxX = null;
	public Double getMaxX() {
		return maxX;
	}

	public void setMaxX(Double maxX) {
		this.maxX = maxX;
	}

	protected Property propertyX;
	public Property getPropertyX() {
		return propertyX;
	}

	public void setPropertyX(Property propertyX) {
		this.propertyX = propertyX;
	}

	public Property getPropertyY() {
		return propertyY;
	}

	public void setPropertyY(Property propertyY) {
		this.propertyY = propertyY;
	}

	protected Property propertyY;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 81990940570873057L;

	protected abstract int getID(T target);
	protected abstract String getSQL(T target);
	
	

	public void open() throws DbAmbitException {

		
	}

}