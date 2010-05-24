package ambit2.db.chart;

import java.awt.image.BufferedImage;

import ambit2.db.AbstractDBProcessor;



public abstract class ChartGenerator<T> extends AbstractDBProcessor<T,BufferedImage> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5231855179501628510L;
	protected int width = 400;
	protected int height= 400;
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}


}
