package ambit2.db.chart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.sql.SQLException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.urls.StandardPieURLGenerator;
import org.jfree.data.general.PieDataset;
import org.jfree.data.jdbc.JDBCPieDataset;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;

/**
 * Generates pie chart with number of chemicals per group
 * @author nina
 *
 */
public abstract class PieChartGenerator<T extends ISourceDataset> extends ChartGenerator<T> {
	protected Property property;
	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}
	/*
	protected static final String sql = 
		"SELECT value,count(distinct(query_results.idchemical)) as num_chemicals\n"+
		"FROM query_results join property_values using(idstructure) join property_string using(idvalue_string) join properties using(idproperty)\n"+
		"where name='%s' and idquery=%d\n"+
		"group by value\n";
		*/
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4301713904733096802L;
	
	protected abstract int getID(T target);
	protected abstract String getSQL();
	
	public BufferedImage process(T target) throws AmbitException {
		  if (property == null) throw new AmbitException("Property not defined");
		  JFreeChart pieChart = null;

	      try
	      {
	         PieDataset pieDataset =  new JDBCPieDataset(getConnection(),
	        		 String.format(getSQL(),property.getId(),getID(target))); 
	         
	         
	         pieChart =
	            ChartFactory.createPieChart3D( property.getName() , // chart title
	                                         pieDataset,
	                                         true,      // legend displayed
	                                         true,      // tooltips displayed
	                                         true );   // no URLs

	         ((PiePlot) pieChart.getPlot()).setBackgroundPaint(Color.white);
	         ((PiePlot) pieChart.getPlot()).setToolTipGenerator(new StandardPieToolTipGenerator() {
	        	 @Override
	        	public String generateToolTip(PieDataset dataset, Comparable key) {
	        		return super.generateToolTip(dataset, key);
	        	}
	         });
	         ((PiePlot) pieChart.getPlot()).setURLGenerator(new StandardPieURLGenerator(){
	        	 @Override
	        	public String generateURL(PieDataset dataset, Comparable key,
	        			int pieIndex) {
	        		return String.format(String.format("%s %d",key,pieIndex)); 
	        		//super.generateURL(dataset, key, pieIndex);
	        	}
	         }) ;
	         
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
