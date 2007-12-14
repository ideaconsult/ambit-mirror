/*
 * Created on 16-Apr-2005
 *
 * @author Nina Jeliazkova
 */
package ambit.ui.domain;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ColorBar;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.ContourPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.contour.ContourDataset;
import org.jfree.data.general.DatasetGroup;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ObjectList;

import ambit.data.AbstractAmbitListListener;
import ambit.data.AbstractAmbitObjectListener;
import ambit.data.AmbitList;
import ambit.data.AmbitListChanged;
import ambit.data.IAmbitListListener;
import ambit.data.AmbitObject;
import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitObjectListener;
import ambit.domain.DataCoverageDescriptors;
import ambit.domain.DataModule;
import ambit.domain.QSARDataset;
import ambit.domain.QSARdatasets;
import ambit.ui.data.AmbitObjectDialog;

/**
 * This class takes care of various visualizations - scatter plot, density contours 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 16-Apr-2005
 */
public class ADGUIListener implements ChartMouseListener {
	protected static final int scatter_plot = 0;
	protected static final int bar_plot = 1;
	
    protected IAmbitObjectListener modelListener = null;
    protected IAmbitListListener testListener = null;    
    
	protected ADChartFrame chartframe = null;
	protected ChartPanel densityPanel = null;
	protected ChartPanel scatterPanel = null;	
	protected AmbitObjectDialog dlgPoint = null;
	protected DataModule dm = null;
	protected IADPlotsType[] plotType = null;

	protected DatasetGroup modelDG=null, testDG = null;
	protected MyCustomRenderer modelXYRenderer = null;
	protected ObjectList testXYRenderer = null;
	protected Paint[] paint = null;
	/**
	 * 
	 */
	public ADGUIListener() {
		super();
		plotType = new  IADPlotsType[] {new ADScatterPlotsType(),new ADBarPlotsType()};
		initGroups();
		initListeners();
	}
	public ADGUIListener(DataModule dm) {
		super();
		this.dm = dm;
		plotType = new  IADPlotsType[] {new ADScatterPlotsType(),new ADBarPlotsType()};
		initGroups();
		initListeners();		
	}

	protected void initGroups() {
	    modelDG = new MyDatasetGroup(1);
	    testDG = new MyDatasetGroup(2);
	}

	protected void initListeners() {
	    modelListener = new AbstractAmbitObjectListener() {
	        /* (non-Javadoc)
             * @see ambit.data.AbstractAmbitObjectListener#ambitObjectChanged(ambit.data.AmbitObjectChanged)
             */
            public void ambitObjectChanged(AmbitObjectChanged event) {
    		    
    			Object src = event.getSource();			
    			AmbitObject ds = event.getObject();
    			if (ds != null) 
    			    if (ds instanceof ADScatterPlotsType) {
    			    	drawChart((QSARDataset)src);
    			    } else if (ds instanceof QSARDataset) {
    						if (densityPanel != null) 
    							drawDensity((QSARDataset)ds);
    						if (scatterPanel != null)
    							drawChart((QSARDataset)ds);
    				    } 
            }
	    };
	    
	    testListener = new AbstractAmbitListListener() {
	        /* (non-Javadoc)
             * @see ambit.data.AbstractAmbitListListener#ambitListChanged(ambit.data.AmbitListChanged)
             */
            public void ambitListChanged(AmbitListChanged event) {
    			AmbitList al = event.getList();
    			if ((al != null) && (al == dm.testData)) {
    				if (scatterPanel != null)
    					drawChart((QSARdatasets)al);				
    			}
            }
            /* (non-Javadoc)
             * @see ambit.data.AbstractAmbitListListener#ambitObjectChanged(ambit.data.AmbitObjectChanged)
             */
            public void ambitObjectChanged(AmbitObjectChanged event) {
                // TODO selected test data

            }
	    };
	}
	/* (non-Javadoc)
	 * @see org.jfree.chart.ChartMouseListener#chartMouseMoved(org.jfree.chart.ChartMouseEvent)
	 */
	public void chartMouseMoved(ChartMouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	public void drawDensity(QSARDataset dataset) {
	    if (chartframe == null) return;
	    if (!dataset.getCoverage().isAssessed()) return;
		if (!(dataset.getCoverage().getMethod() instanceof DataCoverageDescriptors)) return;
		int[] index = new int[2];
		index[0] = plotType[scatter_plot].getXindex(); 
		index [1] = plotType[scatter_plot].getYindex();
		
		ContourDataset ds = new DensityDataset("Model domain", 
				dataset.getCoverage().getMethod(),
				index,
				dataset.getData().getDataRange());

		if (densityPanel == null) {
			NumberAxis xa = new NumberAxis();
			xa.setLabel(dataset.getXname(index[0]));
			xa.setAutoRange(true);
			xa.setAutoRangeIncludesZero(false);
			
			NumberAxis ya = new NumberAxis();
			ya.setLabel(dataset.getXname(index[1]));
			ya.setAutoRange(true);
			ya.setAutoRangeIncludesZero(false);
			
			ContourPlot densityPlot = null;
			try {
			    densityPlot = new ContourPlot(ds,
					xa,ya,
					new ColorBar (dataset.getCoverage().getMethod().getName())
					);			
			} catch (Exception x) {
			    x.printStackTrace();
			    densityPlot = null;
			}
				
			if (densityPlot == null) return;
			//TODO throw exception
			
			JFreeChart densitychart = new JFreeChart(
					dataset.getCoverage().getMethod().toString(), 
						null, densityPlot, false);
			densitychart.getTitle().setFont(new Font("",0,10));
		    densityPanel = new ChartPanel(densitychart);
		    densityPanel.setPreferredSize(new Dimension(450,300));
			chartframe.createFrame(dataset.toString(),densityPanel,10, 320, new Dimension(400,300));
			chartframe.setVisible(true);		    
		    /*
		    densityframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		    //densityframe.setSize(new Dimension(w,h));
		    densityframe.addWindowListener( new WindowAdapter() {
	        	   public void windowClosing( WindowEvent e ){
	        	   	densityframe.setVisible(false);
	        	   	densityframe.dispose();
	        	   	densityframe = null;
	        	   }
	        	   } ); 
	    
	        		    
		    densityframe.pack();
		    densityframe.setVisible(true);
		    */
		} else {
			JFreeChart densitychart = densityPanel.getChart(); 
			ContourPlot densityPlot = (ContourPlot) densitychart.getPlot();
			densityPlot.getColorBar().getAxis().setLabel(dataset.getCoverage().getMethod().getName());
			densityPlot.setDataset(ds);
			densityPlot.getColorBar().configure(densityPlot);			
			densitychart.setTitle(dataset.getCoverage().getMethod().toString());
			densityPlot.getDomainAxis().setLabel(dataset.getXname(index[0]));
			densityPlot.getRangeAxis().setLabel(dataset.getXname(index[1]));
			densityPanel.setVisible(true);
			chartframe.setVisible(true);
		 
		}
	}
	protected void drawChart(QSARdatasets test) {
	    if (scatterPanel == null) return;
	    //testdataset
	    ADXYDataset[] dsTest = null;
	    if (test != null) {
	        dsTest = new ADXYDataset[test.size()];
	        for (int i = 0; i < test.size(); i++) {
	            dsTest[i] = new ADXYDataset(test.getDataSet(i),
	            		(ADScatterPlotsType)plotType[scatter_plot]);
	            dsTest[i].setGroup(testDG);
	        }    
	    }
		JFreeChart scatterchart = scatterPanel.getChart();
		scatterchart.setAntiAlias(true);
		XYPlot xyPlot = (XYPlot) scatterchart.getPlot();

	    for (int i = 1; i < xyPlot.getDatasetCount(); i++) 
	        xyPlot.setDataset(i,null);
		
	    if (dsTest != null) {
	        if (testXYRenderer == null) {
	            testXYRenderer = new ObjectList();
		        paint = ChartColor.createDefaultPaintArray();
	        }
	    
		    
	        for (int i = 0; i < dsTest.length; i++) {
	            xyPlot.setDataset(i+1,dsTest[i]);
	            
	            MyCustomRenderer mp = (MyCustomRenderer)testXYRenderer.get(i);
	            if (mp == null) {
		            Paint[] p1 = new Paint[1];
		            p1[0] = paint[i % paint.length];
		            mp = new MyCustomRenderer(p1);
		            mp.setSeriesShapesFilled(0,new Boolean(false));
		            testXYRenderer.set(i,mp);
	            }
	            xyPlot.setRenderer(i+1,mp);
	        }    
	    }
	    //scatterPanel.setTitle(plotType.getName());
		scatterchart.setTitle(plotType[scatter_plot].getName());
		scatterPanel.setVisible(true);

	}
	protected void drawChart(QSARDataset training) {
	    //training dataset
	    ADXYDataset ds = null;
	    if (training != null) { 
	        ds = new ADXYDataset(training,(ADScatterPlotsType)plotType[scatter_plot]);
	        ds.setGroup(modelDG);
	    }
	    
		if (scatterPanel == null) {
		    if (training == null) return;
			NumberAxis xa = new NumberAxis();
			xa.setLabel(plotType[scatter_plot].getXLabel(training));
			xa.setAutoRange(true);
			xa.setAutoRangeIncludesZero(false);
			
			NumberAxis ya = new NumberAxis();
			ya.setLabel(plotType[scatter_plot].getYLabel(training));
			ya.setAutoRange(true);
			ya.setAutoRangeIncludesZero(false);
			
			
			XYPlot xyPlot = null;
			
			modelXYRenderer = new MyCustomRenderer(
			        new Paint[] {Color.black});
            modelXYRenderer.setSeriesShapesFilled(0,new Boolean(false));			
			
			
			try {
			    xyPlot = new XYPlot(ds,xa,ya,modelXYRenderer);
			    //LegendItemCollection lc = new LegendItemCollection();
			    //lc.add(new LegendItem(ds.getSeriesName(0),new Color(255,0,0)));
			    //xyPlot.setLegendItems(lc);
			    xyPlot.setNoDataMessage("No data available");
			    xyPlot.setBackgroundPaint(Color.white);
			    xyPlot.setDomainGridlinePaint(Color.gray);
			    xyPlot.setRangeGridlinePaint(Color.gray);
			    xyPlot.setDomainCrosshairVisible(true);
			    xyPlot.setRangeCrosshairVisible(true);		
			    xyPlot.setRangeCrosshairLockedOnData(true);		
			    xyPlot.setForegroundAlpha(0.75f);
			    
			} catch (Exception x) {
			    x.printStackTrace();
			    xyPlot = null;
			}
				
			if (xyPlot == null) return;
			//TODO throw exception
			
			JFreeChart scatterchart = new JFreeChart(
						plotType[scatter_plot].getName(), 
						null, xyPlot, true);

			scatterchart.setBackgroundPaint(Color.white);
					//new GradientPaint(0, 0, AmbitColors.BrightClr, 0,                1000, AmbitColors.DarkClr));
			
			scatterchart.getTitle().setFont(new Font("",0,10));
			scatterPanel = new ChartPanel(scatterchart);
			scatterPanel.addChartMouseListener(this);
			scatterPanel.setPreferredSize(new Dimension(300,300));
			scatterPanel.setMinimumSize(new Dimension(100,100));		    
			
			chartframe.createFrame("Data visualization",scatterPanel,10,10,new Dimension(300,300));
			chartframe.setVisible(true);
			/*
		    scatterframe = new ChartFrame(
		    		training.toString(), 
					scatterchart, true);
		    scatterframe.getChartPanel().setPreferredSize(new Dimension(300,300));
		    scatterframe.getChartPanel().setMaximumSize(new Dimension(100,100));		    
		    scatterframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			scatterframe.getChartPanel().addChartMouseListener(this);		
			
		    scatterframe.addWindowListener( new WindowAdapter() {
	        	   public void windowClosing( WindowEvent e ){
	        	       scatterframe.setVisible(false);
	        	       scatterframe.dispose();
	        	       scatterframe = null;
	        	   }
	        	   } ); 
	    
	        		    
		    scatterframe.pack();
		    RefineryUtilities.centerFrameOnScreen(scatterframe);
		    scatterframe.setVisible(true);
			*/
		    
		} else {
			JFreeChart scatterchart = scatterPanel.getChart(); 
			XYPlot xyPlot = (XYPlot) scatterchart.getPlot();
			//LegendItemCollection lc = xyPlot.getLegendItems();
			if (ds != null) { 
			    xyPlot.setDataset(ds);
				xyPlot.getDomainAxis().setLabel(plotType[scatter_plot].getXLabel(training));
				xyPlot.getRangeAxis().setLabel(plotType[scatter_plot].getYLabel(training));
			}    
		    
			scatterchart.setTitle(plotType[scatter_plot].getName());
			scatterPanel.setVisible(true);
			chartframe.setVisible(true);		 
		}
	}
	
		public void showChart(String title, QSARDataset ds, QSARdatasets dss) {
		if (chartframe == null) {
//			if (!dm.modelData.isEmpty()) {
			chartframe = new ADChartFrame(title,dm.modelData,plotType);
			for (int i=0; i < chartframe.getPanelsCount();i++) {
				dm.modelData.addAmbitObjectListener(chartframe.getModelListener(i));
				if (dm.modelData.getCoverage().getMethod() != null)
					dm.modelData.getCoverage().getMethod().addAmbitObjectListener(
							chartframe.getModelListener(i));			
				dm.testData.addAmbitObjectListener(chartframe.getTestListener(i));
			}
			RefineryUtilities.centerFrameOnScreen(chartframe);
//			} else CoreApp.showMessage("No data to show in the chart!\nLoad model first.","Warning");
		} else {
			chartframe.attributePanel[0].updateData(dm.modelData);
		}
		if (chartframe != null)
			chartframe.setVisible(true);
		drawChart(ds);
		drawChart(dss);
	  }
		/** 
		 * Responds to a mouse click on the chart
		 * If CrossHairs are visible and locked to the data this is the way to get info about the clicked point
		 * @see org.jfree.chart.ChartMouseListener#chartMouseClicked(org.jfree.chart.ChartMouseEvent)
		 */
		public void chartMouseClicked(ChartMouseEvent arg0) {
			if (arg0.getEntity() instanceof XYItemEntity) {
				XYItemEntity xy = (XYItemEntity) arg0.getEntity();
				try {
					MyDatasetGroup dg = (MyDatasetGroup) xy.getDataset().getGroup();
					if (dg.isTrainingDataset()) {
						showAmbitPoint(null,"Training data ",dm.modelData,xy.getItem());
					} else if (dg.isTestDataset()) {
						showAmbitPoint(null,"Test data ",
								dm.testData.getDataSet(xy.getSeriesIndex()),
								xy.getItem());
					}
					/*
				System.out.println(xy.getSeriesIndex() + "\t" + xy.getItem()+
						"\tGroup=" + xy.getDataset().getGroup());
						*/
				} catch (ClassCastException x) {
					//not my data :( 
				} catch (Exception x) {
					//smth else went wrong
				}
			}

		}
		protected void showAmbitPoint(
							JComponent component,
							String caption,
							QSARDataset ds, 
							int index) {
			
			AmbitObject point = ds.getData().getItem(index);
			String title = caption + ds.getName() +" #" + (index+1);
			if (point != null) {
				if (dlgPoint == null) {
					dlgPoint = AmbitObjectDialog.createAndShow(
					false,title,component,point, new Dimension(200,200));
					
					dlgPoint.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				    //densityframe.setSize(new Dimension(w,h));
					dlgPoint.addWindowListener( new WindowAdapter() {
			        	   public void windowClosing( WindowEvent e ){
			        	   	dlgPoint.setVisible(false);
			        	   	dlgPoint.dispose();
			        	   	dlgPoint = null;
			        	   }
			        	   } ); 
			    
				} else {
					dlgPoint.setTitle(title);
					dlgPoint.setAmbitObject(point);
					dlgPoint.setVisible(true);
				}
			}
		}

	
    public IAmbitObjectListener getModelListener() {
        return modelListener;
    }
    public IAmbitListListener getTestListener() {
        return testListener;
    }
}
