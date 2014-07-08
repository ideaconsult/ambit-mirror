package ambit2.db.chart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.sql.SQLException;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.jdbc.JDBCCategoryDataset;
import org.jfree.ui.RectangleInsets;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;

public class BarChartGeneratorDataset extends BarChartGenerator<ISourceDataset> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5297256309410169743L;
	protected static final String sql_dataset = 
		"SELECT ifnull(value_num,value) as v,count(distinct(idstructure)) as num_chemicals\n"+
		"FROM struc_dataset join property_values using(idstructure) " +
		"left join property_string using(idvalue_string) join properties using(idproperty)\n"+
		"where idproperty=%d and id_srcdataset=%d\n"+
		"group by v\n";	
	
	protected static final String sql_query = 
			"SELECT ifnull(value_num,value) as v,count(distinct(idstructure)) as num_chemicals\n"+
			"FROM query_results join property_values using(idstructure) " +
			"left join property_string using(idvalue_string) join properties using(idproperty)\n"+
			"where idproperty=%d and idquery=%d\n"+
			"group by v\n";	
	

	protected String getCategoryTitle(ISourceDataset target) {
		return propertyX.getName();
	}

	protected String getChartTitle(ISourceDataset target) {
		return "";//String.format("Dataset %s",target.getName()==null?target.getID():target.getName());
	}
	
	public BufferedImage process(ISourceDataset target) throws AmbitException {
		  if (propertyX == null) throw new AmbitException("Property not defined");

		  JFreeChart chart = null;

	      try
	      {
	    	  JDBCCategoryDataset barDataset =  new JDBCCategoryDataset(getConnection(),getSQL(target));
	    	
	    	  chart =  ChartFactory.createBarChart(
		            		getChartTitle(target), // chart title
		            		getCategoryTitle(target),
		            		"Number of compounds",
		            		barDataset,
		            		PlotOrientation.VERTICAL,
		            							legend && !thumbnail,      // legend displayed
		                                         true,      // tooltips displayed
		                                         true );   //  URLs
	    	  
	    	  
	    	  BarRenderer r = (BarRenderer) chart.getCategoryPlot().getRenderer();

	         
	          r.setBarPainter(new StandardBarPainter());
	          r.setDrawBarOutline(true);
	          r.setBaseOutlinePaint(new Color(109,68,149));
	          r.setShadowVisible(false);
	          
	    	  
	    	  r.setSeriesPaint(0,new Color(177,225,241));
	    	  
	    	  chart.getPlot().setBackgroundPaint(Color.white);
	    	  chart.getPlot().setInsets(new RectangleInsets(0,0,0,0));
		      chart.setTextAntiAlias(true);
		      chart.setAntiAlias(true);
	          if (thumbnail) {
			         chart.getCategoryPlot().getDomainAxis().setVisible(false);
			         chart.getCategoryPlot().getRangeAxis().setVisible(false);
			         chart.setTitle("");
		         }
		         	    	  
	         return chart.createBufferedImage(width,height);

	      }
	      catch (SQLException sqlEx)    // checked exception
	      {
	         throw new AmbitException(sqlEx);
	      }

	}
	@Override
	protected int getID(ISourceDataset target) {
		return target.getID();
	}
	@Override
	protected String getSQL(ISourceDataset target) {
		String sql =  target instanceof SourceDataset?sql_dataset:sql_query;
		return String.format(sql,propertyX.getId(),getID(target)); 
	}

	
}
