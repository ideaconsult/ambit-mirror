package ambit2.db.chart;

import java.awt.image.BufferedImage;
import java.sql.SQLException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.jdbc.JDBCXYDataset;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;

public class PropertiesChartGenerator extends ChartGenerator<SourceDataset> {
	protected static final String sql_dataset = 
		"select a,b from\n"+
		"(\n"+
		"select value_num as a,idchemical from struc_dataset \n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where idproperty = %d and id_srcdataset=%d\n"+
		"and value_num is not null\n"+
		"group by idchemical,value_num\n"+
		") as X\n"+
		"join\n"+
		"(\n"+
		"select value_num as b,idchemical from struc_dataset \n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where idproperty = %d and id_srcdataset=%d\n"+
		"and value_num is not null\n"+
		"group by idchemical,value_num\n"+
		") as Y\n"+
		"using(idchemical)\n";
	
	protected static final String sql_query = 
		"select a,b from\n"+
		"(\n"+
		"select value_num as a,idchemical from query_results\n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where idproperty = %d and idquery=%d\n"+
		"group by idchemical,value_num\n"+
		") as X\n"+
		"join\n"+
		"(\n"+
		"select value_num as b,idchemical from query_results\n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where idproperty = %d and idquery=%d\n"+
		"group by idchemical,value_num\n"+
		") as Y\n"+
		"using(idchemical)\n";	
	
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

	public BufferedImage process(SourceDataset target) throws AmbitException {
		  if ((propertyX == null) || (propertyY == null)) throw new AmbitException("Property not defined");

		  JFreeChart chart = null;

	      try
	      {
	    	  JDBCXYDataset dataset =  new JDBCXYDataset(
	        		 getConnection(),
	        		 String.format(sql_dataset,
	        				 propertyX.getId(),target.getId(),
	        				 propertyY.getId(),target.getId()
	        				 )); 
	         

	         chart =
	            ChartFactory.createScatterPlot( 
	            		target.toString(),
	            		propertyX.getName(),
	            		propertyY.getName(), // chart title
	                                     dataset,
	                                     PlotOrientation.HORIZONTAL,
	                                         true,      // legend displayed
	                                         true,      // tooltips displayed
	                                         false );   // no URLs
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
