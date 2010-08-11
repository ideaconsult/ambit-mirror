package ambit2.db.chart;

import java.awt.image.BufferedImage;
import java.sql.SQLException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.jdbc.JDBCCategoryDataset;

import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;

public class BarChartGeneratorDataset extends BarChartGenerator<SourceDataset> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5297256309410169743L;
	protected static final String sql = 
		"SELECT ifnull(value_num,value) as v,count(distinct(structure.idchemical)) as num_chemicals\n"+
		"FROM struc_dataset join structure using(idstructure) join property_values using(idstructure) " +
		"left join property_string using(idvalue_string) join properties using(idproperty)\n"+
		"where idproperty=%d and id_srcdataset=%d\n"+
		"group by v\n";	
	public BufferedImage process(SourceDataset target) throws AmbitException {
		  if (propertyX == null) throw new AmbitException("Property not defined");

		  JFreeChart chart = null;

	      try
	      {
	    	  CategoryDataset barDataset =  new JDBCCategoryDataset(getConnection(),
		        		 String.format(getSQL(),propertyX.getId(),getID(target))); 
		         

	    	  chart =
		            ChartFactory.createBarChart(
		            		propertyX.getName() , // chart title
		            		propertyY==null?"":propertyY.getName(),
		            		propertyX.getName(),
		            		barDataset,
		            		PlotOrientation.VERTICAL,
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
	@Override
	protected int getID(SourceDataset target) {
		return target.getId();
	}
	@Override
	protected String getSQL() {
		return sql;
	}

}
