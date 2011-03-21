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
	protected boolean legend = false;
	protected String param = null;
	
	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public ChartGenerator() {
		super();
	}
	
	public ChartGenerator(int width,int height) {
		super();
		setWidth(width);
		setHeight(height);
	}
	public boolean hasLegend() {
		return legend;
	}
	public void setLegend(boolean legend) {
		this.legend = legend;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		if (width>0)
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		if (height>0)
		this.height = height;
	}


}
