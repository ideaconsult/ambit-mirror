/**
 * Created on 2005-4-4
 *
 */
package ambit.ui.domain;

import java.awt.Paint;
import java.awt.Shape;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;


/**
 * A custom renderer for a {@link org.jfree.chart.JFreeChart} to have different series with predefined colors 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class MyCustomRenderer extends StandardXYItemRenderer {

    /** The colors. */
    private Paint[] colors;

    /**
     * Creates a new renderer.
     *
     * @param colors  the colors.
     */
    public MyCustomRenderer(Paint[] colors) {
    	super(SHAPES);
        this.colors = colors;
        //setPl                    (true);
		setPlotLines(false);
		setToolTipGenerator(new XYToolTipGenerator() {
			public String generateToolTip(XYDataset arg0, int arg1, int arg2) {
				StringBuffer b = new StringBuffer();
				b.append(arg0.getSeriesKey(0));
				b.append(" Compound # " + (arg2+1));
				return b.toString();
			}
		});		
    }

    /*
     * Returns the paint for an item.  Overrides the default behaviour inherited from
     * AbstractRenderer.
     *
     * @param row  the series.
     *
     * @return The item color.
     */
    /*
    public Paint getItemPaint(int row) {
        return colors[row];
    }
    */
    
    public Paint getSeriesPaint(int series) {

    	return colors[series % (colors.length) ];
    	
        //return colors[datasetIndex % (colors.length)];
    }
    /* (non-Javadoc)
     * @see org.jfree.chart.renderer.AbstractRenderer#getSeriesShape(int)
     */
    public Shape getSeriesShape(int series) {
        return super.getSeriesShape(series);
    }
    public void drawItem(java.awt.Graphics2D g2, XYItemRendererState state, java.awt.geom.Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
//    	isMainDataset = ((MyDatasetGroup) dataset.getGroup()).getTag() == 1;
    	super.drawItem(g2,state,dataArea,info,plot,domainAxis,rangeAxis,dataset,series,item,crosshairState,pass);
    }
    
/*    
    public LegendItem getLegendItem(int datasetIndex, int series) {

        LegendItem result = null;

        XYPlot xyplot = getPlot();
        if (xyplot != null) {
        	System.err.print(datasetIndex);
        	System.err.print(" - ");
        	System.err.println(xyplot.getDatasetCount());
            
            XYDataset dataset = xyplot.getDataset(datasetIndex);
            if (dataset != null) {
                String label = dataset.getSeriesName(series);
                String description = label;
                Shape shape = getSeriesShape(series);
                Paint paint = getSeriesPaint(series);
                Paint outlinePaint = getSeriesOutlinePaint(series);
                Stroke stroke = getSeriesStroke(series);

                result = new LegendItem(
                    label, description, shape, true, paint, stroke, outlinePaint, stroke
                );
            }

        }

        return result;

    } 
*/    
}


