package ambit2.db.chart;

import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.jdbc.JDBCXYDataset;

import ambit2.base.data.Property;
import ambit2.db.search.IStoredQuery;

public class Numeric2SimilarityChartGenerator extends ChartGenerator<IStoredQuery> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7655203169195131809L;

	protected static final String sql = 
		"select metric,value_num from query_results\n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where name = '%s' and idquery=%d and value_num is not null\n";

	
	protected Property property;
	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}


	/**
	 * 
	 */


	public BufferedImage process(IStoredQuery target) throws AmbitException {
		  if ((property == null) || (property == null)) throw new AmbitException("Property not defined");

		  JFreeChart chart = null;

	      try
	      {
	    	  String s =  String.format(sql,
     				 property.getName(),target.getId()
				 );
	    	  JDBCXYDataset dataset =  new JDBCXYDataset(
	        		 getConnection(),s);
	        		
	         

	         chart =    ChartFactory.createScatterPlot( 
	            		target.toString(),
	            		"metric",
	            		property.getName(), // chart title
	                                     dataset,
	                                     PlotOrientation.VERTICAL,
	                                         true,      // legend displayed
	                                         true,      // tooltips displayed
	                                         false );   // no URLs
	         chart.getXYPlot().getDomainAxis().setAutoRange(true);
	         chart.getXYPlot().getRangeAxis().setAutoRange(true);

	         
	         return chart.createBufferedImage(width,height);

	      }
	      catch (SQLException sqlEx)    // checked exception
	      {
	    	 logger.log(Level.SEVERE,sqlEx.getSQLState(),sqlEx);
	         return null;
	      }

	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
}
