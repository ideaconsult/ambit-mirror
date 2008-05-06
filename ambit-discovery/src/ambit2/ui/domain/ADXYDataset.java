/**
 * <b>Filename</b> ADXYDataset.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-7-20
 * <b>Project</b> ambit
 */
package ambit2.ui.domain;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;

import ambit2.domain.QSARDataset;

/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-7-20
 */
public class ADXYDataset implements XYDataset {
    protected QSARDataset dataset = null;
    protected ADScatterPlotsType pType = null;
    protected DatasetGroup group = null;
    protected boolean visible = true;
    protected int[][] dPlotIndex = null;
    /**
     * 
     */
    public ADXYDataset(QSARDataset dataset, ADScatterPlotsType pType) {
        super();
        this.dataset = dataset;
        this.pType = pType;
        dPlotIndex = null;
        initDPlotIndex();
    }
    protected void initDPlotIndex() {
        if ((pType.getId() == ADScatterPlotsType._ptDomainPredictedDplot) ||
        		((pType.getId() == ADScatterPlotsType._ptDomainObservedDplot))){
        	int n = dataset.getNpoints();
        	dPlotIndex = new int[n* (n-1) / 2][2];
        	int i = 0;
        	for (int n1 = 0; n1 < n; n1++) 
            	for (int n2 = n1+1; n2 < n; n2++) {
            		dPlotIndex[i][0] = n1;
            		dPlotIndex[i][1] = n2;            		
            		i++;
            	}
        }    	
    }

    /* (non-Javadoc)
     * @see org.jfree.data.xy.XYDataset#getDomainOrder()
     */
    public DomainOrder getDomainOrder() {
        // TODO Auto-generated method stub
        return DomainOrder.NONE;
    }

    /* (non-Javadoc)
     * @see org.jfree.data.xy.XYDataset#getItemCount(int)
     */
    public int getItemCount(int arg0) {
        int n = dataset.getNpoints();
        if ((pType.getId() == ADScatterPlotsType._ptDomainPredictedDplot) ||
        		(pType.getId() == ADScatterPlotsType._ptDomainObservedDplot))
        	return (n*(n-1)/2);
        else return n;
    }

    /* (non-Javadoc)
     * @see org.jfree.data.xy.XYDataset#getX(int, int)
     */
    public Number getX(int series, int point) {
        Double d =  new Double(getXValue(0,point));
        if (d.isNaN()) 
            return null; 
        else return d;
    }
    protected double getXData(int point) {
        try  {
            return dataset.getXData(point,pType.getXindex());
        } catch (Exception x) {
           // visible = false;
            return Double.NaN;
        }
    }
    protected double getCoverage(int point) {
        double d = dataset.getCoverage(point);
        if (new Double(d).isNaN()) {
            return -0.001;
        } else return d;    	
    }
    //TODO smth on nonexisting X data	
    /* (non-Javadoc)
     * @see org.jfree.data.xy.XYDataset#getXValue(int, int)
     */
    public double getXValue(int series, int point) {
        switch (pType.getId()) {
        case ADScatterPlotsType._ptDescriptorScatter : {
            return getXData(point);
        }
        case ADScatterPlotsType._ptDescriptorResidual : {
            return getXData(point);
        }
        case ADScatterPlotsType._ptDescriptorPredicted : {
            return getXData(point);
        }
        case ADScatterPlotsType._ptDescriptorObserved : {
            return getXData(point);
        }
        case ADScatterPlotsType._ptDescriptorDomain : {
            return getXData(point);
        }        
                
        case ADScatterPlotsType._ptObservedPredicted : {
            return dataset.getYObserved(point);
           
        }
        case ADScatterPlotsType._ptPredictedResidual : {
            return dataset.getYPredicted(point);
        }
        case ADScatterPlotsType._ptDomainResidual : {
            double d = dataset.getCoverage(point);
            if (new Double(d).isNaN()) {
                return -0.001;
            } else return d;
        }
        case ADScatterPlotsType._ptDomainPredicted : {
            double d = dataset.getCoverage(point);
            if (new Double(d).isNaN()) {
                return -0.001;
            } else return d;
        }
        case ADScatterPlotsType._ptDomainObserved : {
            double d = dataset.getCoverage(point);
            if (new Double(d).isNaN()) {
                return -0.001;
            } else return d;
        }        
                
        case ADScatterPlotsType._ptDomainPredictedDplot : {
        	/*
        	int n= dataset.getNpoints();
        	int n1 = point / n;
        	int n2 = point % n;
        	*/
        	if (dPlotIndex == null) initDPlotIndex();        	
            return Math.abs(getCoverage(dPlotIndex[point][0])-
				   getCoverage(dPlotIndex[point][1]));
        }       
        case ADScatterPlotsType._ptDomainObservedDplot : {
        	/*
        	int n= dataset.getNpoints();
        	int n1 = point / n;
        	int n2 = point % n;
        	*/
        	if (dPlotIndex == null) initDPlotIndex();        	
            return Math.abs(getCoverage(dPlotIndex[point][0])-
				   getCoverage(dPlotIndex[point][1]));
        }            
        case ADScatterPlotsType._ptDomain : {
            return dataset.getCoverage(point);            
        }
        default: {
            return 0;
        }
        }
    }

    /* (non-Javadoc)
     * @see org.jfree.data.xy.XYDataset#getY(int, int)
     */
    public Number getY(int series, int point) {
        Double d = new Double(getYValue(series,point));
        if (d.isNaN()) 
            return null;
        else
            return d;
    }
    
    /* (non-Javadoc)
     * @see org.jfree.data.xy.XYDataset#getYValue(int, int)
     */
    public double getYValue(int series, int point) {
        switch (pType.getId()) {
        case ADScatterPlotsType._ptDescriptorScatter : {
            try {
            return dataset.getXData(point,pType.getYindex());
            } catch (Exception x) {
                return Double.NaN;
            }
        }
        case ADScatterPlotsType._ptDescriptorResidual : {
            return dataset.getResidual(point);
        }
        case ADScatterPlotsType._ptDescriptorPredicted : {
            return dataset.getYPredicted(point);
        }
        case ADScatterPlotsType._ptDescriptorObserved : {
            return dataset.getYObserved(point);
        }
        case ADScatterPlotsType._ptDescriptorDomain : {
            double d = dataset.getCoverage(point);
            if (new Double(d).isNaN()) {
                return -0.5;
            } else return d;
        }        
        case ADScatterPlotsType._ptObservedPredicted : {
            return dataset.getYPredicted(point);
        }
        case ADScatterPlotsType._ptPredictedResidual : {
            return dataset.getResidual(point);
        }
        case ADScatterPlotsType._ptDomainResidual : {
            double d = dataset.getResidual(point);
            return d;
            /* when model choice compare standardized residuals
            double r = dataset.getCoverage().getRmseAll();
            return d / r;
            */
        }
        case ADScatterPlotsType._ptDomainPredicted : {
            return dataset.getYPredicted(point);
        }
        case ADScatterPlotsType._ptDomainObserved : {
            return dataset.getYObserved(point);
        }        
                
        case ADScatterPlotsType._ptDomainPredictedDplot : {
        	/*
        	int n= dataset.getNpoints();
        	int n1 = point / n;
        	int n2 = point % n;
            return dataset.getYPredicted(n1)-dataset.getYPredicted(n2);
            */
        	if (dPlotIndex == null) initDPlotIndex();
            return Math.abs(dataset.getYPredicted(dPlotIndex[point][0])-
            	   dataset.getYPredicted(dPlotIndex[point][1]));        	
        }
        case ADScatterPlotsType._ptDomainObservedDplot : {
        	if (dPlotIndex == null) initDPlotIndex();
            return Math.abs(dataset.getYObserved(dPlotIndex[point][0])-
            	   dataset.getYObserved(dPlotIndex[point][1]));            	
        }
        case ADScatterPlotsType._ptDomain : {
            return 0;            
        }
        default: {
            return 0;
        }
        }
    }

    /* (non-Javadoc)
     * @see org.jfree.data.general.SeriesDataset#getSeriesCount()
     */
    public int getSeriesCount() {
        return 1;
    }

    /* (non-Javadoc)
     * @see org.jfree.data.general.SeriesDataset#getSeriesName(int)
     */
    public String getSeriesName(int series) {
        return dataset.getName();
    }

    /* (non-Javadoc)
     * @see org.jfree.data.general.Dataset#addChangeListener(org.jfree.data.general.DatasetChangeListener)
     */
    public void addChangeListener(DatasetChangeListener arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.jfree.data.general.Dataset#removeChangeListener(org.jfree.data.general.DatasetChangeListener)
     */
    public void removeChangeListener(DatasetChangeListener arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.jfree.data.general.Dataset#getGroup()
     */
    public DatasetGroup getGroup() {
        return group;
    }

    /* (non-Javadoc)
     * @see org.jfree.data.general.Dataset#setGroup(org.jfree.data.general.DatasetGroup)
     */
    public void setGroup(DatasetGroup arg0) {
        group = arg0;

    }
    public Comparable getSeriesKey(int arg0) {
    	return dataset.getName();
    	//return "TODO";
    }
    public int indexOf(Comparable arg0) {
    	// TODO Auto-generated method stub
    	return 0;
    }
}

