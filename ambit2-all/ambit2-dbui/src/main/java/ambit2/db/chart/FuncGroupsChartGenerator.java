package ambit2.db.chart;

import java.awt.Image;
import java.sql.SQLException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.jdbc.JDBCCategoryDataset;
import org.jfree.data.jdbc.JDBCPieDataset;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
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
		"SELECT name,count(distinct(idchemical)) as 'Number of chemicals' FROM\n"+
		"query_results join property_values using(idstructure) join properties using(idproperty) join catalog_references using(idreference)\n"+
		"where title='ambit2.descriptors.FunctionalGroupDescriptor' and idquery=%d\n"+
		"group by idproperty order by 'Number of chemicals' desc ) as M";
	

	public Image process(IStoredQuery target) throws AmbitException {
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
