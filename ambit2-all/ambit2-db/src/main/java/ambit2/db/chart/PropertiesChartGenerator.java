package ambit2.db.chart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.sql.SQLException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.ui.RectangleInsets;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;

public class PropertiesChartGenerator extends ChartGenerator<ISourceDataset> {
	protected static final String sql_dataset = 
		"select a,b from\n"+
		"(\n"+
		"select value_num as a,idstructure from struc_dataset \n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where idproperty = %d and id_srcdataset=%d\n"+
		"and value_num is not null\n"+
		") as X\n"+
		"join\n"+
		"(\n"+
		"select value_num as b,idstructure from struc_dataset \n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where idproperty = %d and id_srcdataset=%d\n"+
		"and value_num is not null\n"+
		") as Y\n"+
		"using(idstructure)\n";
	
	protected static final String sql_query = 
		"select a,b from\n"+
		"(\n"+
		"select value_num as a,idstructure from query_results\n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where idproperty = %d and idquery=%d\n"+
		") as X\n"+
		"join\n"+
		"(\n"+
		"select value_num as b,idstructure from query_results\n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where idproperty = %d and idquery=%d\n"+
		") as Y\n"+
		"using(idstructure)\n";	
	
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

	public BufferedImage process(ISourceDataset target) throws AmbitException {
		  if ((propertyX == null) || (propertyY == null)) throw new AmbitException("Property not defined");

		  JFreeChart chart = null;

	      try
	      {
	    	  JDBCXYDataset dataset =  new JDBCXYDataset(
	        		 getConnection(),
	        		 String.format(target instanceof SourceDataset?sql_dataset:sql_query,
	        				 propertyX.getId(),target.getID(),
	        				 propertyY.getId(),target.getID()
	        				 )); 
	         

	         chart =
	            ChartFactory.createScatterPlot( 
	            		target.toString(),
	            		propertyX.getName(),
	            		propertyY.getName(), // chart title
	                                     dataset,
	                                     PlotOrientation.HORIZONTAL,
	                                         legend && !thumbnail,      // legend displayed
	                                         true,      // tooltips displayed
	                                         true );   // no URLs

	         if (thumbnail) {
		         chart.getXYPlot().setRenderer(new XYDotRenderer());
		         chart.getXYPlot().getDomainAxis().setVisible(false);
		         chart.getXYPlot().getRangeAxis().setVisible(false);
		         chart.getXYPlot().setInsets(new RectangleInsets(0,0,0,0));
		         chart.setAntiAlias(true);
		         chart.setTitle("");

	         }
	         
	         chart.getPlot().setBackgroundPaint(Color.white) ;
	         ((XYPlot)chart.getPlot()).setRangeGridlinePaint(Color.gray);
	         ((XYPlot)chart.getPlot()).setDomainGridlinePaint(Color.gray);

	         ///ChartUtilities.writeImageMap(writer, name, info, useOverLibForToolTips)
	         return chart.createBufferedImage(width,height);

	      }
	      catch (SQLException sqlEx)    // checked exception
	      {
	         throw new AmbitException(sqlEx);
	      }

	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
