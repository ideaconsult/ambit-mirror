package ambit2.db.chart;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;
import ambit2.db.exceptions.DbAmbitException;

public abstract class BarChartGenerator<T extends ISourceDataset> extends ChartGenerator<T> {

	
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
	protected abstract String getSQL();
	
	

	public void open() throws DbAmbitException {

		
	}

}