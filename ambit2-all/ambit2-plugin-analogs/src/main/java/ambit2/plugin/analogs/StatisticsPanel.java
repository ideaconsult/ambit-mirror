package ambit2.plugin.analogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.beans.PropertyChangeEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRelation;
import ambit2.db.processors.QueryStatisticsProcessor;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.property.QueryPairwiseTanimoto;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.WorkflowContextListenerPanel;

public class StatisticsPanel extends WorkflowContextListenerPanel {
	protected JTextPane textPane = new JTextPane();
	protected QueryStatisticsProcessor processor = new QueryStatisticsProcessor();
	protected QueryExecutor executor = new QueryExecutor();
	protected DefaultCategoryDataset dataset = new DefaultCategoryDataset();

	/**
	 * 
	 */
	private static final long serialVersionUID = -2531508548151305489L;
    public StatisticsPanel(WorkflowContext wfcontext) {
        super(null);
        setLayout(new BorderLayout());
        setWorkflowContext(wfcontext);
        add(new JScrollPane(textPane),BorderLayout.CENTER);
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        add(chartPanel,BorderLayout.SOUTH);
       // addWidgets(controlsPosition,mode);
    }   
    private JFreeChart createChart(final CategoryDataset dataset) {
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
            "Source chemical(s) homogeneilty",         // chart title
            "Pairwise Tanimoto",               // domain axis label
            "Frequency",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        
        // set up gradient paints for series...
        final GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, Color.blue, 
            0.0f, 0.0f, Color.lightGray
        );

        renderer.setSeriesPaint(0, gp0);


        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2.0)
        );
        // OPTIONAL CUSTOMISATION COMPLETED.
        
        return chart;
        
    }    
    protected void animate(PropertyChangeEvent arg0) {
        if (DBWorkflowContext.STOREDQUERY.equals(arg0.getPropertyName())) {
            Object o = arg0.getNewValue();
 
            if (o instanceof IStoredQuery) {
                try {
                    setQuery((IStoredQuery)o);
                	
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        } else if (DBWorkflowContext.DBCONNECTION_URI.equals(arg0.getPropertyName())) {
            	if (arg0.getNewValue() == null) 
            	try {
            		processor.setConnection(null);
            		executor.setConnection(null);
            		//tableModel.setConnection(null);
            		
            	} catch (Exception x) {
            		x.printStackTrace();
            	}
        } else   if (DBWorkflowContext.PROFILE.equals(arg0.getPropertyName())) {
        	//tableModel.setProfile(DBWorkflowContext.PROFILE,(Profile)getWorkflowContext().get(DBWorkflowContext.PROFILE));
        //} else   if (DBWorkflowContext.CALCULATED.equals(arg0.getPropertyName())) {
        //	tableModel.setProfile(DBWorkflowContext.CALCULATED,(Profile)getWorkflowContext().get(DBWorkflowContext.CALCULATED));
        } else   if (DBWorkflowContext.ENDPOINTS.equals(arg0.getPropertyName())) {
        	//tableModel.setProfile(DBWorkflowContext.ENDPOINTS,(Profile)getWorkflowContext().get(DBWorkflowContext.ENDPOINTS));
        }
    }
    public synchronized void setQuery(IStoredQuery query) throws AmbitException {
    	try {
	        Connection c = ((DataSource)getWorkflowContext().get(DBWorkflowContext.DATASOURCE)).getConnection();
	        processor.setConnection(c);
	        textPane.setText(processor.process(query).toString());
	        executor.setConnection(c);
	        QueryPairwiseTanimoto tanimoto = new QueryPairwiseTanimoto();
	        tanimoto.setFieldname(query);
	        ResultSet rs = executor.process(tanimoto);
	        int max = 20;
	        int[] histogram = new int[max+1]; 
	        for (int i=0; i < max+1; i++) histogram[i] = 0;
	        while (rs.next()) {
	        	IStructureRelation<Double> relation = tanimoto.getObject(rs);
	        	//System.out.print(d);
	        	//System.out.print(" ->");
	        	histogram[(int)(relation.getRelation()*max)] ++;
	        	//System.out.println((int)(d*max));
	        }
	        dataset.clear();
	        for (int i=0; i < max+1; i++) 
	        	dataset.addValue( histogram[i], "", new Double(((double)i)/(double)max));
	        
	        executor.closeResults(rs);
    	} catch (SQLException x) {
    		throw new AmbitException(x);
    	}
        
    }
	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}
	
}
