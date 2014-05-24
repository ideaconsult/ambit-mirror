/**
 * Created on 2005-2-4
 *
 */
package ambit2.model.numeric;

import java.util.Arrays;

import Jama.Matrix;
import ambit2.domain.stats.Tools;
import ambit2.domain.stats.transforms.densityestimation.KDensity1D;
import ambit2.domain.stats.transforms.densityestimation.KNormal;
import ambit2.domain.stats.transforms.densityestimation.Kernel;



/**
 * A class to estimate applicability domain by nonparametric probability density estimation  
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DataCoverageDensity extends DataCoverageDescriptors {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4530499526801051110L;
	private Kernel theKernel = new KNormal();
    private KDensity1D[] kdeList = null;
    private int nGrid = 1024;
	/**
	 * 
	 */
	public DataCoverageDensity() {
		super( ADomainMethodType._modeDENSITY);
		kdeList = null;

	}


	public boolean isEmpty() {
		return super.isEmpty() || (kdeList == null);
	}
	
	public void clear() {
		super.clear();
		clearKDEList();
	}
	
	protected void clearKDEList() {
		if (kdeList != null) for (int i = 0; i < kdeList.length; i++) kdeList[i] = null;
		kdeList = null;
	}
	protected void initKDE(int ndescriptors) {
		clearKDEList();
		kdeList = new KDensity1D[ndescriptors];
		for (int i = 0; i < kdeList.length; i++) kdeList[i] = null;		
	}
	protected void doEstimation(Matrix matrix, int np, int nd) {
		double[][] points = matrix.getArray();
		//super.doEstimation(points,np,nd);
		mindata = new double[nd];
		maxdata = new double[nd];
		
		if (kdeList == null) initKDE(nd);
	    double A[] = new double[np];
        
	    KDensity1D akde;
	    for (int d = 0; d< nd; d++) {
	        for (int p = 0; p < np; p++) A[p] = points[p][d];
	        akde = null;
	        double datarange[] = new double[2];
	        if (Tools.ExtendedInterval(A,np, datarange))  {
	        	mindata[d] = datarange[0];
	        	maxdata[d] = datarange[1];	        	
	            akde = new KDensity1D();
	            if (akde.estimateDensity(A, np, theKernel, nGrid, 0,
	                     datarange[0], datarange[1], null)) {
	            } else akde = null;	
	        } else {
	        	mindata[d] = Tools.min(A,np);
	        	maxdata[d] = Tools.max(A,np);	        	
	        }
	        kdeList[d] = akde;
	    }
	 	//TODO threshold
	    //threshold
	    A = doAssessment(matrix,np,nd);
	    threshold = estimateThreshold(pThreshold,A) ;
	    A=null;
	}
	
	protected double doAssessment(double[] points, int nd) {
        double result = 1;    	

        	for (int d = 0; d < nd; d++) {
        		//if outside range then set density to zero; 
        		//TODO use extended range
                if ((points[d] < mindata[d]) || 
                	(points[d] > maxdata[d])) { result = 0; break; }
                else 
        		if (kdeList[d] != null) 
        			result = result *	kdeList[d].getGridPoint(points[d]);
        		else { 
        			result = 1; //if density not assessed reduces to ranges ...
        		}
        	}
        return result;
	}
	
	public int getDomain(double coverage) {
		int domain;
		if (coverage >= threshold) domain = 0;
		else domain =1;
		return domain;
	}	
	public int[] getDomain(double[] coverage) {
		if (coverage == null) return null;
		int np = coverage.length;
		int[] domain = new int[np];
		for (int i = 0; i < np; i++)
			//note the inequality , here it is >= in contrast to DataCoverageDistance			
			if (new Double(coverage[i]).isNaN()) domain[i] = 2;
			else if (coverage[i] >= threshold) domain[i] = 0;
			else domain[i] =1;
		return domain;
	}
	
	protected double estimateThreshold(double percent, double[] values) {
		double t;
		int tIndex = 0;
		if (percent == 1) {
			t = Tools.min(values,values.length);
		}
		else {
		    Arrays.sort(values);
		    tIndex = (int) Math.round(values.length * (1-percent));
		    if (tIndex < 0) tIndex = 0;
			t = values[tIndex];
		}
		return t;
		
	}
	
}
