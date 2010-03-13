/**
 * Created on 2005-1-28
 *
 */
package ambit2.model.numeric;

import Jama.Matrix;
import ambit2.domain.stats.Tools;




/**
 * A parent class for all classes that estimate applicability domain by distance in the descriptor space 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DataCoverageDistance extends DataCoverageDescriptors {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4288804703751436528L;
	protected double[] meandata ; 
	/**
	 * 
	 */
	public DataCoverageDistance() {
		super();
		appDomainMethodType = ADomainMethodType._modeEUCLIDEAN;
	}

	/**
	 * @param mode
	 */
	public DataCoverageDistance(ADomainMethodType mode) {
		super(mode);
		if (!mode.isDistance())
			appDomainMethodType = ADomainMethodType._modeEUCLIDEAN;
	}
	public void clear() {
		super.clear();
		meandata = null;
	}
	
	public double calcDistance(double[] center,
				double[] points,  int nd) {
		switch (appDomainMethodType) {
		case _modeEUCLIDEAN: 
			return euclideanDistance(center,points,nd);
		case _modeCITYBLOCK: 
			return cityBlockDistance(center,points,nd);
		default:
			return euclideanDistance(center,points,nd);
		}
	}
		
	/**
	 * 
	 * @param center
	 * @param point
	 * @param nd
	 * @return euclidean distance
	 */
	public double euclideanDistance(double[] center,
			double[] point, int nd) {
		double r=0;
		for (int j = 0; j < nd; j++) 
			r += (point[j] - center[j])*(point[j] - center[j]);
		return Math.sqrt(r);
	}

	/**
	 * 
	 * @param center
	 * @param point
	 * @param nd
	 * @return cityblock distance
	 */
	private double cityBlockDistance(double[] center,
			double[] point,  int nd) {
	double r = 0;
		for (int j = 0; j < nd; j++) 
			r += Math.abs(point[j] - center[j]);
	return r;
	}
		
	/**
	 * 
	 */
	protected void doEstimation(Matrix matrix, int np, int nd) {
		double[][] points = matrix.getArray();
		mindata = Tools.min(points);
		maxdata = Tools.max(points);
		switch (datasetCenterPoint) {
		case _cmodeMean: { meandata = Tools.mean(points); break; }
		case _cmodeCenter: { 
			meandata = new double[nd];
			for (int i = 0; i < nd; i++) 
			meandata[i] = (maxdata[i] - mindata[i])/2; 
		}
		case _cmodeOrigin: { 
			meandata = new double[nd];
			for (int i = 0; i < nd; i++) meandata[i] = 0; 
			break; 
		}
		default: { meandata = Tools.mean(points); break; }
		}
		double[] d = doAssessment(matrix,np,nd);
		threshold = estimateThreshold(pThreshold,d);
		d = null;
	}
	
	/**
	 * 
	 */
	protected double doAssessment(double[] points, int nd) {
		return calcDistance(meandata,points,nd);
	}
	public boolean isEmpty() {
		return super.isEmpty() || (meandata == null);
			   
	}
	
}
