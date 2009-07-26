package ambit2.db.chart;

import java.awt.Image;
import java.sql.SQLException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.PieDataset;
import org.jfree.data.jdbc.JDBCPieDataset;
import org.jfree.data.jdbc.JDBCXYDataset;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.IStoredQuery;

public class PropertiesChartGenerator extends ChartGenerator<IStoredQuery> {
	protected static final String sql = 
		"select a,b from\n"+
		"(\n"+
		"select value_num as a,idchemical from query_results\n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where name = '%s' and idquery=%d\n"+
		"group by idchemical,value_num\n"+
		") as X\n"+
		"join\n"+
		"(\n"+
		"select value_num as b,idchemical from query_results\n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where name = '%s' and idquery=%d\n"+
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

	public Image process(IStoredQuery target) throws AmbitException {
		  if ((propertyX == null) || (propertyY == null)) throw new AmbitException("Property not defined");

		  JFreeChart chart = null;

	      try
	      {
	    	  JDBCXYDataset dataset =  new JDBCXYDataset(
	        		 getConnection(),
	        		 String.format(sql,
	        				 propertyX.getName(),target.getId(),
	        				 propertyY.getName(),target.getId()
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
	         System.err.println("Error trying to acquire JDBCPieDataset.");
	         System.err.println("Error Code: " + sqlEx.getErrorCode());
	         System.err.println("SQLSTATE:   " + sqlEx.getSQLState());
	         sqlEx.printStackTrace();
	         return null;
	      }

	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
