package ambit2.plugin.search;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import ambit2.base.data.Property;
import ambit2.base.data.Range;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.UpdateExecutor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.results.AmbitRows;
import ambit2.db.results.CrossViewPropertyMetric;
import ambit2.db.results.RowsModel;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.property.PropertyRange;
import ambit2.db.search.property.QueryMetricRange;
import ambit2.db.search.property.TemplateQuery;
import ambit2.db.update.storedquery.FilteredSelectStoredQuery;
import ambit2.workflow.DBWorkflowContext;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.microworkflow.process.WorkflowContext;
import com.microworkflow.ui.WorkflowContextListenerPanel;

public class StatisticsPanel extends WorkflowContextListenerPanel {
	//protected PropertyStatsString propertyValuesQuery;
	protected PropertyRange propertyNumRange;
	protected AmbitRows<Property> properties;
	protected AmbitRows<Range<Double>> metricBins;
	protected AmbitRows propertyValues;
	protected QueryExecutor executor = new QueryExecutor();
	protected DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	protected CrossViewPropertyMetric crossView = new CrossViewPropertyMetric();
	protected PresentationModel<CrossViewPropertyMetric> model; 
	protected JTable browser_table;
	protected FilteredSelectStoredQuery select = new FilteredSelectStoredQuery();
	protected UpdateExecutor updater = new UpdateExecutor();
	/**
	 * 
	 */
	private static final long serialVersionUID = -2531508548151305489L;
    public StatisticsPanel(WorkflowContext wfcontext) {
        super(null);
        propertyValues = new AmbitRows();
      //  propertyValuesQuery = new PropertyStatsString();
        propertyNumRange = new PropertyRange();
        properties = new AmbitRows<Property>() {
        	@Override
        	protected synchronized IQueryRetrieval createNewQuery(
        			Property target) throws AmbitException {
        		propertyNumRange.setFieldname(target);
        		//propertyValuesQuery.setFieldname(target);
        		//propertyValuesQuery.setValue("");
        		return propertyNumRange;
        	}
        };
        properties.setPropertyname("property");
        properties.addPropertyChangeListener(properties.getPropertyname(),propertyValues);
        metricBins = new AmbitRows<Range<Double>>();
        
                
        model = new PresentationModel<CrossViewPropertyMetric>(crossView);
        
        setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
        setWorkflowContext(wfcontext);
        add(createPropertyComponent());
        
        browser_table = new JTable(crossView);
        browser_table.setRowSelectionAllowed(true);
        browser_table.setColumnSelectionAllowed(true);
        browser_table.setCellSelectionEnabled(true);
        add(new JScrollPane(browser_table));
        
        ListSelectionListener listener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) return;
				int row = browser_table.getSelectedRow();
				int col = browser_table.getSelectedColumn();
		        // If cell selection is enabled, both row and column change events are fired
		        if (e.getSource() == browser_table.getSelectionModel()
		              && browser_table.getRowSelectionAllowed()) {
		        	row = browser_table.getSelectedRow();
		        } 
		        if (e.getSource() == browser_table.getColumnModel().getSelectionModel()
		               && browser_table.getColumnSelectionAllowed() ){
		        	col = browser_table.getSelectedColumn();
		        }
	        	if ((browser_table.getSelectedRow()>=0) &&(browser_table.getSelectedColumn()>=0)) {       
	        		Object value = browser_table.getValueAt(row, col);
	        		if ("".equals(value)) 
	        			setRecord(crossView.getRows().getElementAt(row),crossView.getColumns().getElementAt(col-1));
	        	}
	        	
		      }
		};
		browser_table.getSelectionModel().addListSelectionListener(listener);
		browser_table.getColumnModel().getSelectionModel()
	        .addListSelectionListener(listener);
	    
		crossView.setRows(new RowsModel(propertyValues));

		crossView.setColumns(new RowsModel(metricBins));
		
        /*
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        add(chartPanel,BorderLayout.SOUTH);
       // addWidgets(controlsPosition,mode);
        * */
		setPreferredSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        
    }   
    protected void setRecord(Object row,Object col) {
    	if ((row instanceof Range) && (col instanceof Range)) {
    		
    		select.setGroup(crossView.getQuery());
    		select.setObject(crossView.getProperty());
    		select.setMetric((Range)col);
    		select.setValue((Range)row);
    		try {
    			
    			updater.process(select);
    			//force browser to redisplay query - doesn't work
    			//getWorkflowContext().put(DBWorkflowContext.STOREDQUERY, crossView.getQuery());
    		} catch (AmbitException x) {
    			x.printStackTrace();
    		}
    	}
    }
	protected JComponent createPropertyComponent() {
		
		ListModel fieldnames = new RowsModel<Property>(properties) {
			@Override
			public int getSize() {
				return super.getSize()+1;
			}
			@Override
			public Property getElementAt(int index) {
				if (index == 0) return null;
				else
					return super.getElementAt(index-1);
			}
		};
		SelectionInList<Property> p = new SelectionInList<Property>(fieldnames, model.getModel("property"));
		
		p.addPropertyChangeListener("value",new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				try {
					properties.process((Property) evt.getNewValue());
					} catch (Exception x) {
						
					}
				crossView.setProperty((Property)evt.getNewValue());
				
			}
		});		
		
		JComboBox box = BasicComponentFactory.createComboBox(p);
		AutoCompleteDecorator.decorate(box);
		return box;

				

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
            		executor.setConnection(null);
            		properties.setConnection(null);
            		metricBins.setConnection(null);
            		propertyValues.setConnection(null);
            		updater.setConnection(null);
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
	        executor.setConnection(c);
	        crossView.setQuery(query);
	        crossView.setExec(executor);
	        
	        properties.setConnection(c);
    		properties.setQuery(new TemplateQuery());
    		properties.open();	        
    		
    		metricBins.setConnection(c);
    		metricBins.setQuery(new QueryMetricRange(query,5));
    		metricBins.open();
    		
    		propertyValues.setConnection(c);
    		
    		updater.setConnection(c);

    	} catch (SQLException x) {
    		throw new AmbitException(x);
    	}
        
    }    
    /*
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
	        	Double d = tanimoto.getObject(rs);
	        	//System.out.print(d);
	        	//System.out.print(" ->");
	        	histogram[(int)(d*max)] ++;
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
    */
	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}
	@Override
	public String toString() {
		return "Filter";
	}
	
}
