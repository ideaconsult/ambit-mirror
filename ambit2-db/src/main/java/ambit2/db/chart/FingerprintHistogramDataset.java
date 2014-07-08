package ambit2.db.chart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.jdbc.JDBCCategoryDataset;
import org.jfree.ui.RectangleInsets;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;

public class FingerprintHistogramDataset extends ChartGenerator<ISourceDataset> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8511066138461076352L;
	public FingerprintHistogramDataset() {
		super(600,250);
		setParam("fp1024");
	}
	public BufferedImage process(ISourceDataset target) throws AmbitException {

		  JFreeChart chart = null;

	      try
	      {
	    	  JDBCCategoryDataset barDataset =  new JDBCCategoryDataset(getConnection(),
	    			  String.format(getSQL(),target.getID()));
		         

	    	  chart =
		            ChartFactory.createBarChart(
		            		target.getName(), // chart title
		            		"",
		            		"Number of compounds",
		            		barDataset,
		            		PlotOrientation.VERTICAL,
		                                         legend,      // legend displayed
		                                         true,      // tooltips displayed
		                                         false );   // no URLs

	    	  barDataset.setTranspose(false);
	    	  chart.setPadding(new RectangleInsets(0,0,0,0));
	    	  chart.getPlot().setBackgroundPaint(Color.white);
	         return chart.createBufferedImage(width,height);

	      }
	      catch (SQLException sqlEx)    // checked exception
	      {
	         throw new AmbitException(sqlEx);
	      }

	}

	protected int getID(SourceDataset target) {
		return target.getId();
	}

	protected String getSQL() {
		StringBuilder b = new StringBuilder();
		BufferedReader reader = null ;
		try {
			
			InputStream stream = this.getClass().getClassLoader().getResourceAsStream(
					String.format("ambit2/db/sql/%s_stats.sql",param==null?"fp1024":getParam()));
			reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = reader.readLine()) != null) {
				b.append(line);
				b.append("\n");
			}
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		} finally {
			try {reader.close(); } catch (Exception x) {}
		}
		return b.toString();
	}
	@Override
	public void open() throws DbAmbitException {
	}
	public void setParam(String param) {
		if (param==null) this.param = "fp1024";
		else	
		if ("sk1024".equals(param.toLowerCase())) {
			this.param = "sk1024";
		} else this.param = "fp1024";
	}
	
}
