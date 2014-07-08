package ambit2.db.chart;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.sql.SQLException;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.ui.RectangleInsets;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;

public class PropertiesChartGenerator extends ChartGenerator<ISourceDataset> {
	protected static final String sql_dataset = 
		"select a,b from\n"+
		"(\n"+
		"select %s as a,idstructure from struc_dataset \n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where idproperty = %d and id_srcdataset=%d\n"+
		"and value_num is not null\n"+
		") as X\n"+
		"join\n"+
		"(\n"+
		"select %s as b,idstructure from struc_dataset \n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where idproperty = %d and id_srcdataset=%d\n"+
		"and value_num is not null\n"+
		") as Y\n"+
		"using(idstructure)\n";
	
	protected static final String sql_query = 
		"select a,b from\n"+
		"(\n"+
		"select %s as a,idstructure from query_results\n"+
		"join property_values using(idstructure) join properties using(idproperty)\n"+
		"where idproperty = %d and idquery=%d\n"+
		") as X\n"+
		"join\n"+
		"(\n"+
		"select %s as b,idstructure from query_results\n"+
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

	protected String getSQL(ISourceDataset target) {
		 String q = target instanceof SourceDataset?sql_dataset:sql_query;
 
		 return String.format(
				 q,
				 logX?"log(value_num)":"value_num",
				 propertyX.getId(),target.getID(),
				 logY?"log(value_num)":"value_num",
				 propertyY.getId(),target.getID()
				 );
	}
	
	protected String getDomainTitle(ISourceDataset target) {
		return logX?String.format("log(%s)", propertyX.getName()):
					String.format("%s", propertyX.getName());
	}
	
	protected String getRangeTitle(ISourceDataset target) {
		return logY?String.format("log(%s)", propertyY.getName()):
			String.format("%s", propertyY.getName());
	}		
	public BufferedImage process(ISourceDataset target) throws AmbitException {
		  if ((propertyX == null) || (propertyY == null)) throw new AmbitException("Property not defined");

		  JFreeChart chart = null;

	      try
	      {
	    	  JDBCXYDataset dataset =  new JDBCXYDataset(
	        		 getConnection(),
	        		getSQL(target)
	        				 ); 
	         

	         chart =
	            ChartFactory.createScatterPlot( 
	            		target.toString(),
	            		getDomainTitle(target),
	            		getRangeTitle(target), 
	                                     dataset,
	                                     PlotOrientation.HORIZONTAL,
	                                         legend && !thumbnail,      // legend displayed
	                                         true,      // tooltips displayed
	                                         true );   // no URLs
	         chart.setAntiAlias(true);
	         chart.setTextAntiAlias(true);
	         chart.getXYPlot().setInsets(new RectangleInsets(0,0,0,0));
	         if (thumbnail) {
		         //chart.getXYPlot().setRenderer(new XYDotRenderer());
	        	 XYShapeRenderer r = new XYShapeRenderer();
	        	 r.setBasePaint(new Color(109,68,149));
	        	 r.setSeriesPaint(0,new Color(109,68,149));
	        	 r.setBaseShape(new Rectangle2D.Double(-1,-1,2,2));
	        	 chart.getXYPlot().setRenderer(r);		         
		         chart.getXYPlot().getDomainAxis().setVisible(false);
		         chart.getXYPlot().getRangeAxis().setVisible(false);
		         chart.setTitle("");
	         } else {
	        	 XYShapeRenderer r = new XYShapeRenderer();
	        	 r.setBasePaint(new Color(109,68,149));
	        	 r.setSeriesPaint(0,new Color(109,68,149));
	        	 r.setBaseShape(new Rectangle2D.Double(-2,-2,3,3));
	        	 chart.getXYPlot().setRenderer(r);
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
