package ambit2.db.chart;

import java.awt.image.BufferedImage;

import net.idea.modbcum.p.AbstractDBProcessor;



public abstract class ChartGenerator<T> extends AbstractDBProcessor<T,BufferedImage> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5231855179501628510L;
	protected int width = 400;
	protected int height= 400;
	protected boolean legend = false;
	protected String param = null;
	protected boolean thumbnail = false;
	protected boolean logX = false;
	public boolean isLogX() {
		return logX;
	}

	public void setLogX(boolean logX) {
		this.logX = logX;
	}
	protected boolean logY = false;
	
	public boolean isLogY() {
		return logY;
	}

	public void setLogY(boolean logY) {
		this.logY = logY;
	}

	public boolean isThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(boolean thumbnail) {
		this.thumbnail = thumbnail;
		this.width = 100;
		this.height = 100;
	}

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
