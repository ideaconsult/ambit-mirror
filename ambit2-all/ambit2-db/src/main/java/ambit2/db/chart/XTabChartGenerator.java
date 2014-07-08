package ambit2.db.chart;

import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.general.DefaultKeyedValues2DDataset;
import org.jfree.data.jdbc.JDBCCategoryDataset;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;

/**
<pre>
select group_concat(distinct b),a-mod(a,10), count(*)
from (

		select a,b from
		(
		select value_num as a,idchemical from query_results
		join property_values using(idstructure) join properties using(idproperty)
		where name = "LLNA EC3 %" and idquery=2
		group by idchemical,value_num
		) as X
		join
		(
		select value as b,idchemical from query_results
		join property_values using(idstructure) join property_string using(idvalue_string) join properties using(idproperty)
		where name = "Michael Acceptors" and idquery=2
		group by idchemical,value_num
		) as Y
		using(idchemical)

) as p
group by b,a-mod(a,10)

</pre>
   select   concat(
                 'select  a-mod(a,10)','\n'
             ,   group_concat(distinct
                     concat(
                         ', sum(','\n'
                     ,   '     if(b=\"',value,'\"\n'
                     ,   '       ,  1','\n'
                     ,   '       ,   null','\n'
                     ,   '       )\n'
                     ,   '     )'
                     ,   ' "',value,'"\n'
                     )
                     order by value
                     separator ''
                 )
,',sum(if(b is null,  1,   null)) NA '
,'from (','\n'
,'		select a,b from','\n'
,'		(','\n'
,'		select value_num as a,idchemical from query_results','\n'
,'		join property_values using(idstructure) join properties using(idproperty)','\n'
,'		where name = "LLNA EC3 %" and idquery=2','\n'
,'		group by idchemical,value_num','\n'
,'		) as X','\n'
,'		left join','\n'
,'		(','\n'
,'		select value as b,idchemical from query_results','\n'
,'		join property_values using(idstructure) join property_string using(idvalue_string) join properties using(idproperty)','\n'
,'		where name = "toxTree.tree.cramer.CramerRules" and idquery=2','\n'
,'		group by idchemical,value_num','\n'
,'		) as Y','\n'
,'		using(idchemical)','\n'
,') as p','\n'
,'group by a-mod(a,10)','\n'
             ) statement
    from query_results join property_values using(idstructure) left join property_string using(idvalue_string) join properties using(idproperty)
   where name = "toxTree.tree.cramer.CramerRules" and idquery=2


 * @author nina
 *
 */
public class XTabChartGenerator extends ChartGenerator<ISourceDataset> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2946352054029479908L;
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
	protected double binWidth = 10;
	public double getBinWidth() {
		return binWidth;
	}

	public void setBinWidth(double binWidth) {
		this.binWidth = binWidth;
	}

	protected static final String sql = 
		/*
		"select value_num-mod(value_num,20), count(*) as frequency\n"+
		"from query_results join property_values using(idstructure) join properties using(idproperty)\n"+
		"where name='%s' and idquery=%d and value_num is not null\n"+
		"group by value_num-mod(value_num,20)\n"	;
		*/
	"select sql_dataset_xtab(%d,%d,%d,%f)";
	
	
	public BufferedImage process(ISourceDataset target) throws AmbitException {
		  JFreeChart chart = null;
		  
	      try
	      {
	    	  String statement= "";
	    	  Statement st = getConnection().createStatement();
	    	  ResultSet rs = st.executeQuery(String.format(sql,propertyX.getId(),propertyY.getId(),target.getID(),getBinWidth()));
	    	  while (rs.next()) {
	    		  statement = rs.getString(1);
	    	  }
	    	  rs.close();
	    	  st.close();
	    	  if (statement==null) statement="select 0,0";
	    	  JDBCCategoryDataset dataset =  new JDBCCategoryDataset(getConnection(),statement);
	    	  
	    	  DefaultKeyedValues2DDataset cumulative = new DefaultKeyedValues2DDataset();
	    	  List rowkeys = dataset.getRowKeys();
	    	  for (int row = 0; row < dataset.getRowCount();row++) {
	    		 
		    	  List colKeys = dataset.getColumnKeys();
		    	  double sum = 0;
		    	  double all = 0;
		    	  for (int col=0; col < dataset.getColumnCount(); col++) 
			    		all += dataset.getValue(row, col).doubleValue();
		    	  if (all > 0)
			    	  for (int col=0; col < dataset.getColumnCount(); col++) {
			    		sum += dataset.getValue(row, col).doubleValue();
	   		    	  	cumulative.addValue(100*sum/all, dataset.getRowKey(row), 
	   		    	  		new Double(Math.log10(Double.parseDouble(dataset.getColumnKey(col).toString()))));
	   		    	  	
			    	  }
	
		    	  
	    	  }
;
	    	 
	         chart =
	            ChartFactory.createBarChart(target.toString(),//String.format("%s by %s",propertyX.getName(),propertyY.getName()),
	            		//target.toString()),
	            							propertyX.getName(),
	            							"Cumulative percentage",
	            					 // chart title
	                                         cumulative,
	                                         PlotOrientation.VERTICAL,
	                                         true,      // legend displayed
	                                         true,      // tooltips displayed
	                                         true );   // no URLs
	         final LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
	         final CategoryPlot plot = chart.getCategoryPlot();
	         
	         plot.setRenderer(0, renderer2);

	         //JOptionPane.showMessageDialog(null,new ChartPanel(chart));
	         return chart.createBufferedImage(width,height);

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
