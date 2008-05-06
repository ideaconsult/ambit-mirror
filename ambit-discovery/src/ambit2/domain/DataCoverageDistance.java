/**
 * Created on 2005-1-28
 *
 */
package ambit2.domain;

import java.util.Vector;

import ambit2.stats.Tools;

/**
 * A parent class for all classes that estimate applicability domain by distance in the descriptor space 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DataCoverageDistance extends DataCoverageDescriptors {
	protected double[] meandata ; 
	/**
	 * 
	 */
	public DataCoverageDistance() {
		super();
		appDomainMethodType.setId(ADomainMethodType._modeEUCLIDEAN);
	}

	/**
	 * @param mode
	 */
	public DataCoverageDistance(int mode) {
		super(mode);
		if ((appDomainMethodType.getId() == ADomainMethodType._modeEUCLIDEAN) 
			|| (appDomainMethodType.getId() == ADomainMethodType._modeCITYBLOCK)) {
			appDomainMethodType.setId(mode);
		} else appDomainMethodType.setId(ADomainMethodType._modeEUCLIDEAN);
	}
	public void clear() {
		super.clear();
		meandata = null;
	}
	
	public double calcDistance(double[] center,
				double[] points,  int nd) {
		switch (appDomainMethodType.getId()) {
		case (ADomainMethodType._modeEUCLIDEAN): 
			return euclideanDistance(center,points,nd);
		case (ADomainMethodType._modeCITYBLOCK): 
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
	protected void doEstimation(double[][] points, int np, int nd) {
		mindata = Tools.min(points);
		maxdata = Tools.max(points);
		switch (datasetCenterPoint.getId()) {
		case DatasetCenterPoint._cmodeMean: { meandata = Tools.mean(points); break; }
		case DatasetCenterPoint._cmodeCenter: { 
			meandata = new double[nd];
			for (int i = 0; i < nd; i++) 
			meandata[i] = (maxdata[i] - mindata[i])/2; 
		}
		case DatasetCenterPoint._cmodeOrigin: { 
			meandata = new double[nd];
			for (int i = 0; i < nd; i++) meandata[i] = 0; 
			break; 
		}
		default: { meandata = Tools.mean(points); break; }
		}
		double[] d = doAssessment(points,np,nd);
		threshold = estimateThreshold(pThreshold,d);
		d = null;
	}
	protected void doEstimation(Vector points, int np, int nd) {
		mindata = Tools.min(points);
		maxdata = Tools.max(points);
		switch (datasetCenterPoint.getId()) {
		case DatasetCenterPoint._cmodeMean: { meandata = Tools.mean(points); break; }
		case DatasetCenterPoint._cmodeCenter: { 
			meandata = new double[nd];
			for (int i = 0; i < nd; i++) 
			meandata[i] = (maxdata[i] - mindata[i])/2; 
		}
		case DatasetCenterPoint._cmodeOrigin: { 
			meandata = new double[nd];
			for (int i = 0; i < nd; i++) meandata[i] = 0; 
			break; 
		}
		default: { meandata = Tools.mean(points); break; }
		}
		double[] d = doAssessment(points,np,nd);
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
