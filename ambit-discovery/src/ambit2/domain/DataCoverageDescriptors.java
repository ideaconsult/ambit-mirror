/**
 * Created on 2005-1-28
 *
 */
package ambit2.domain;

import java.util.Vector;

import Jama.Matrix;
import ambit2.stats.Tools;
import ambit2.stats.datastructures.Sort;
import ambit2.stats.transforms.transformfilters.OrthogonalTransform;


/**
 * A parent class for all classes that estimate applicability domain in descriptor space 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DataCoverageDescriptors extends DataCoverage {
	DatasetCenterPoint datasetCenterPoint = null;		
	
	protected boolean scale = false;
	protected boolean pca = true;
	protected boolean boxcox = false;
	 
	
	protected double[] mindata = null;
	protected double[] maxdata = null;
	OrthogonalTransform pcaTransform = null;
	/**
	 * DataCoverage constructor
	 * Default mode modeRANGE 
	 */
	public DataCoverageDescriptors() {
		super();

		datasetCenterPoint = new DatasetCenterPoint();		
	}
	/**
	 * DataCoverage constructor 
	 * @param mode - ADomainMethodType
	 * _modeRANGE = 0, _modeLEVERAGE = 1; modeEUCLIDEAN = 2, 
	 * _modeCITYBLOCK = 3, _modeMAHALANOBIS = 4,  _modeDENSITY = 5;
	 */
	public DataCoverageDescriptors(int mode) {
		super(mode);
		datasetCenterPoint = new DatasetCenterPoint(); 		
	}

	public void clear() {
		super.clear();
		mindata = null;
		maxdata = null;
		pcaTransform = null;
	}
	public boolean isEmpty() {
		return (mindata == null) || (maxdata == null) ||
			   (pca && (pcaTransform == null));
	}
	/**
	 * @return Returns the boxcox.
	 */
	public boolean isBoxcox() {
		return boxcox;
	}
	/**
	 * @param boxcox The boxcox to set.
	 */
	public void setBoxcox(boolean boxcox) {
		this.boxcox = boxcox;
	}

	/**
	 * @return Returns the pca.
	 */
	public boolean isPca() {
		return pca;
	}
	/**
	 * @param pca The pca to set.
	 */
	public void setPca(boolean pca) {
		if (pca != this.pca) {
			this.pca = pca;
			clear();
		}	
	}
	/**
	 * @return Returns the scale.
	 */
	public boolean isScale() {
		return scale;
	}
	/**
	 * @param scale The scale to set.
	 */
	public void setScale(boolean scale) {
		this.scale = scale;
	}
	protected Matrix toMatrix(Vector points) {
		int np = points.size();
		int nd = ((double[]) points.get(0)).length;
		Matrix m = new Matrix(np,nd);
		double[] d;
		for (int i =0; i < np; i++) {
			d = ((double[]) points.get(i));
			for (int j =0; j < nd; j++) 
				m.set(i,j,d[j]);
		}
		return m;
	}
	public boolean build(AllData data) {
		return estimate(data.getData());
	}
	
	/**
	 * does the actual estimation, to be overrided by inherited class
	 * @param points
	 * @param np
	 * @param nd
	 */
	protected void doEstimation(double[][] points, int np, int nd) {
		mindata = Tools.min(points);
		maxdata = Tools.max(points);
		if (pThreshold == 1) threshold = 0;
		else {
			double[] values  = doAssessment(points,np,nd);
			threshold = estimateThreshold(pThreshold,values);
			values = null;
		}
	}
	protected void doEstimation(Vector points, int np, int nd) {
		mindata = Tools.min(points);
		maxdata = Tools.max(points);
		if (pThreshold == 1) threshold = 0;
		else {
			double[] values  = doAssessment(points,np,nd);
			threshold = estimateThreshold(pThreshold,values);
			values = null;
		}
	}	
	public boolean estimate(double[][] points) {
		//TODO to throw exception
		if (!pca) scale = false;
		if ((points == null) || (points.length < 1)) return false;
		int d = points[0].length;
		
		if (pca) {
			try {
			
			Matrix m = new Matrix(points);
			pcaTransform = new OrthogonalTransform();
			//System.err.println("PCA running ...");
			pcaTransform.InitializeFilter(m,d,d);
			//System.err.println("PCA trasnform ...");			
			Matrix pcapoints = pcaTransform.TransformPoints(m);
			m = null;
			doEstimation(pcapoints.getArray(),points.length,d);
			pcapoints = null;
			m = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			doEstimation(points,points.length,d);			
		}
		return !isEmpty();
	}
	public boolean estimate(Vector points) {
		//TODO to throw exception
		if (!pca) scale = false;
		if ((points == null) || (points.size() < 1)) return false;
		int d = ((double[]) points.get(0)).length;
		if (d == 0) return false;
		if (pca) {
			try {
			
			Matrix m = toMatrix(points);
			pcaTransform = new OrthogonalTransform();
			//System.err.println("PCA running ...");
			pcaTransform.InitializeFilter(m,d,d);
			//System.err.println("PCA trasnform ...");			
			Matrix pcapoints = pcaTransform.TransformPoints(m);
			m = null;
			doEstimation(pcapoints.getArray(),points.size(),d);
			pcapoints = null;
			m = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			doEstimation(points,points.size(),d);			
		}
		return !isEmpty();
	}
	/**
	 * perform an inverse transformation of points <br>
	 * the result is stored in the second argument
	 * @param points  The points to be transformed by the inverse transform
	 * @return  the transformed points 
	 */
	public Matrix inverseTransform(Matrix points ) {
		if (pca)
			return pcaTransform.InverseTransformPoints(points);
		else return points;
	}
	/**
	 * does not perform transformation (e.g. PCA) <br>
	 * Assumes that point is already in a transformed space <br>
	 * @param points
	 * @param nd
	 * @return true if successful
	 */
	public double assessTransformed(double[] points,int nd) {
		return doAssessment(points,nd);
	}
	/**
	 * 
	 * @param points
	 * @param nd
	 * @return double[]
	 */
	protected double doAssessment(double[] points, int nd) {
		double c = 0;
		for (int j = 0; j < nd; j++) {
			if ((points[j]< mindata[j]) || 
				(points[j]> maxdata[j])) {
				c++;
			}
		}
		return c;
	}	
	/**
	 *  does the actual assessment, to be overrided by inherited class
	 * @param points
	 * @param np
	 * @param nd
	 * @return double[]
	 */
	protected double[] doAssessment(double[][] points, int np, int nd) {
		double[] c = new double[np];
		for (int i = 0; i < np; i++) 
			c[i] = doAssessment(points[i],nd);
		return c;
	}
	protected double[] doAssessment(Vector points, int np, int nd) {
		double[] c = new double[np];
		for (int i = 0; i < np; i++) {
			double[] d = (double[]) points.get(i);
			c[i] = doAssessment(d,nd);
		}
		return c;
	}	
	public double[] predict(AllData data) {
		return assess(data.getData());
	}
	/**
	 * 
	 * @param points
	 * @return double[] vector containing estimated coverage
	 */
	public double[] assess(double[][] points) {
		//TODO throw exception
		if (isEmpty()) return null;
		if ((points == null) || (points.length < 1)) return null;
		int np = points.length;
		int nd = mindata.length;
		//incompatible data
		if (nd != points[0].length) {
			//System.err.println("Incompatibe training and test data sets");
			return null;
		}
	
		if (pca) {
			Matrix m = new Matrix(points);
			//System.err.println("PCA transform ...");			
			Matrix pcapoints = pcaTransform.TransformPoints(m);
			//System.err.println("PCA transform done");			
			m = null;
			double [] c = doAssessment(pcapoints.getArray(),np,nd);
			pcapoints = null;
			return c;
		} else //no pca
			return doAssessment(points,np,nd);
	}
	public double[] assess(Vector points) {
		//TODO throw exception
		if (isEmpty()) return null;
		if ((points == null) || (points.size() < 1)) return null;
		int np = points.size();
		int nd = mindata.length;
		//incompatible data
		if (nd != ((double[])points.get(0)).length) {
			//System.err.println("Incompatibe training and test data sets");
			return null;
		}
	
		if (pca) {
			Matrix m = toMatrix(points);
			
			//System.err.println("PCA transform ...");			
			Matrix pcapoints = pcaTransform.TransformPoints(m);
			//System.err.println("PCA transform done");			
			m = null;
			double [] c = doAssessment(pcapoints.getArray(),np,nd);
			pcapoints = null;
			return c;
		} else //no pca
			return doAssessment(points,np,nd);
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(getName());
		if (isPca()) b.append(" PCA,");
		b.append(" Threshold= ");
		b.append(pThreshold*100);
		b.append("%"); 
		return b.toString();
	}
	public String getName() {
		if (appDomainMethodType.isDistance()) {
			return super.getName() + 
			" to " + datasetCenterPoint.toString() ;
		} else return super.getName();
	}
	

	/**
	 * 
	 * @param cmode - as in DatasetCenterPoint
	 * _cmodeCenter = 1;_cmodeMean = 0;_cmodeOrigin = 2;	
	 */
	public void setCmode(int cmode) {
		if (!appDomainMethodType.isDistance()) return;
		else if (datasetCenterPoint.getId() != cmode) clear();
		datasetCenterPoint.setId(cmode);

	}
	protected double estimateThreshold(double percent, double[] values) {
		double t;
		int tIndex = values.length;
		if (percent == 1) {
			t = Tools.max(values,values.length);
		}
		else {
		    Sort sort = new Sort();
		    sort.QuickSortArray(values, values.length);
		    sort = null;
		    tIndex = (int) Math.round(values.length * percent);
		    if (tIndex > values.length) tIndex = values.length;
			t = values[tIndex-1];
		}
		System.err.println("Percent\t"+pThreshold*100+"\tThreshold\t"+t+"\tIndex\t"+tIndex);
		return t;
		
	}
	public DatasetCenterPoint getDatasetCenterPoint() {
		return datasetCenterPoint;
	}
	public void setDatasetCenterPoint(DatasetCenterPoint datasetCenterPoint) {
		this.datasetCenterPoint = datasetCenterPoint;
	}
	
	/**
	 * @return Returns the maxdata.
	 */
	public double[] getMaxdata() {
		return maxdata;
	}
	/**
	 * @return Returns the mindata.
	 */
	public double[] getMindata() {
		return mindata;
	}
}
