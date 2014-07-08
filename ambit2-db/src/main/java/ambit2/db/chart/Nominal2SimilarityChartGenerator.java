package ambit2.db.chart;

import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.jdbc.JDBCCategoryDataset;

import ambit2.base.data.Property;
import ambit2.db.search.IStoredQuery;

/**
 * Interval chart - range of similarity per nominal property (e.g. LLNA class=Moderate -> smilarity range)
 * @author nina
 *
 */
public class Nominal2SimilarityChartGenerator extends ChartGenerator<IStoredQuery> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2946352054029479908L;
	protected Property property;
	
	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	protected static final String sql = 
	"select value,min(metric),max(metric) FROM query_results join property_values using(idstructure) join property_string using(idvalue_string) join properties using(idproperty)\n"+
	"where name='%s' and idquery=%d\n"+
	"group by value";
	
	public BufferedImage process(IStoredQuery target) throws AmbitException {
		  JFreeChart pieChart = null;

	      try
	      {
	    	  JDBCCategoryDataset pieDataset =  new JDBCCategoryDataset(getConnection(),
	    			 
	        		 String.format(sql,property.getName(),target.getId())); 
	         

	         pieChart =
	            ChartFactory.createStackedBarChart(target.toString(),"metric",property.getName(),
	            					 // chart title
	                                         pieDataset,
	                                         PlotOrientation.HORIZONTAL,
	                                         true,      // legend displayed
	                                         true,      // tooltips displayed
	                                         false );   // no URLs
	         return pieChart.createBufferedImage(width,height);

	      }
	      catch (SQLException sqlEx)  {
	    	 logger.log(Level.SEVERE,sqlEx.getSQLState(),sqlEx);
	         return null;
	      }

	}	


	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
