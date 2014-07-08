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

import ambit2.db.search.IStoredQuery;

/**
 * chart number of chemicals with functional groups
 * @author nina
 *
 */
public class FuncGroupsChartGenerator extends ChartGenerator<IStoredQuery> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2988570985723641965L;
	protected static String sql = 
		"SELECT * FROM ("+
		"SELECT name,count(distinct(query_results.idchemical)) as 'Number of chemicals' FROM\n"+
		"query_results join property_values using(idstructure) join properties using(idproperty) join catalog_references using(idreference)\n"+
		"where title='ambit2.descriptors.FunctionalGroupDescriptor' and idquery=%d\n"+
		"group by idproperty order by 'Number of chemicals' desc ) as M";
	

	public BufferedImage process(IStoredQuery target) throws AmbitException {
		  JFreeChart pieChart = null;

	      try
	      {
	    	  JDBCCategoryDataset pieDataset =  new JDBCCategoryDataset(getConnection(),
	        		 String.format(sql,target.getId())); 
	         

	         pieChart =
	            ChartFactory.createBarChart(target.toString(),"Functional groups","Number of chemicals",
	            					 // chart title
	                                         pieDataset,
	                                         PlotOrientation.VERTICAL,
	                                         true,      // legend displayed
	                                         true,      // tooltips displayed
	                                         false );   // no URLs
	         return pieChart.createBufferedImage(width,height);

	      }
	      catch (SQLException sqlEx)   {
	    	 logger.log(Level.SEVERE,sqlEx.getSQLState(),sqlEx);
	         return null;
	      }

	}	

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub

	}

}
