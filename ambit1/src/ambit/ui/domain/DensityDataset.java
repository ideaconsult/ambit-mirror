/*
 * Created on 13-Apr-2005
 *
 * @author Nina Jeliazkova
 */
package ambit.ui.domain;

import java.util.Vector;

import org.jfree.data.contour.DefaultContourDataset;

import Jama.Matrix;
import ambit.domain.DataCoverageDescriptors;
import ambit.domain.IDataCoverage;

/**
 * A descendant of {@link org.jfree.data.contour.DefaultContourDataset} <br>
 * Used to draw {@link ambit.domain.DataCoverageDescriptors} decsendants on a jfreechart
 * <br>
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 13-Apr-2005
 */
public class DensityDataset extends DefaultContourDataset {
	protected DataCoverageDescriptors dc = null;
	protected int gridResolution = 64;
	double [] step = {1,1};
	/**
	 * 
	 */
	public DensityDataset() {
		super();

	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public DensityDataset(String arg0, Object[] arg1, Object[] arg2,
			Object[] arg3) {
		super(arg0, arg1, arg2, arg3);
		
	}
	
	public DensityDataset(String name, IDataCoverage dc, 
						int[] index,
						double[][] range) {
		super();
		if (dc instanceof DataCoverageDescriptors)
			this.dc = (DataCoverageDescriptors) dc;
		else this.dc = null;
		initialize(this.dc,index,range);		
	}
	/**
	 * 
	 * @param dc {@link DataCoverageDescriptors}
	 * @param index Which descriptors to use
	 * @param range  range[0] provides min values for the selected descriptors, range[1] - max values
	 */
    public void initialize(DataCoverageDescriptors dc,
    			int[] index,
				double[][] range) {
    	if (dc.isEmpty()) return;
    	int npoints = gridResolution*gridResolution;
        this.xValues = new Double[npoints];
        this.yValues = new Double[npoints];
        this.zValues = new Double[npoints];

        for (int i = 0; i< range[0].length; i++) {
        	double d = (range[1][i] - range[0][i])/4;
        	range[0][i] -= d;
        	range[1][i] += d;
        }
        double[] minx = range[0];
        double[] maxx = range[1];
        int ndescriptors = minx.length;
        double[][] center = new double[1][ndescriptors];
        
        for (int i =0; i < ndescriptors; i++)
        	center[0][i] = (maxx[i]+minx[i])/2;
        Matrix centerPoints = new Matrix(center);
        
        for (int i =0; i < 2; i++) {
        	step[i] = (maxx[index[i]]-minx[index[i]]) / gridResolution;
        }
        int k = 0;
        final Vector tmpVector = new Vector(); //create a temporary vector
        for (double x=minx[index[0]]; x <maxx[index[0]]; x+= step[0] )  {
            tmpVector.add(new Integer(k));        	
            for (double y=minx[index[1]]; y <maxx[index[1]]; y+= step[1] )  {
            	center[0][index[0]] =x;
            	center[0][index[1]] =y;
            	double d;
            	/*
            	if (dc.isPca()) {
            		d = dc.assessTransformed(center[0],ndescriptors);

            		Matrix m = dc.inverseTransform(centerPoints);
                	this.xValues[k] = new Double(m.get(0,index[0]));
                	this.yValues[k] = new Double(m.get(0,index[1]));
                	m = null;
            		
            	} else {
            	*/
                	this.xValues[k] = new Double(x);
                	this.yValues[k] = new Double(y);  
                	
                	d = dc.assess(center)[0];
            	//}
            	if (dc.getDomain(d) !=0) {
            		this.zValues[k] = null; 
            		//if (dc.getAppDomainMethodType().isRange()) 
            		//	d = -1;
            		//else d = 0;
            	} else this.zValues[k] = new Double(d);            	
            	//System.out.println(this.zValues[k]);
            	k++;
            	if (k >= npoints) break;
            }
        	if (k >= npoints) break;            

        }
       // We organise the data with the following assumption:
       // 1) the data are sorted by x then y
       // 2) that the data will be represented by a rectangle formed by
       //    using x[i+1], x, y[j+1], and y.
       // 3) we march along the y-axis at the same value of x until a new value x is
       //    found at which point we will flag the index where x[i+1]<>x[i]



        final Object[] inttmp = tmpVector.toArray();
        this.xIndex = new int[inttmp.length];  // create array xIndex to hold new column indices

        for (int i = 0; i < inttmp.length; i++) {
            this.xIndex[i] = ((Integer) inttmp[i]).intValue();
        }
    }


}
