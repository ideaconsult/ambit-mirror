/**
 * Created on 2005-1-28
 *
 */
package ambit2.model.numeric;

import java.util.Arrays;

import Jama.Matrix;
import ambit2.domain.stats.Tools;
import ambit2.domain.stats.transforms.transformfilters.OrthogonalTransform;


/**
 * A parent class for all classes that estimate applicability domain in descriptor space 
 * This clas estimates descriptor ranges in PCA space
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DataCoverageDescriptors extends DataCoverage<Matrix> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 730617163278629469L;

	protected DatasetCenterPoint datasetCenterPoint = null;		
	
	protected boolean scale = false;
	protected boolean pca = true;
	protected boolean boxcox = false;
	 
	
	protected double[] mindata = null;
	protected double[] maxdata = null;
	protected OrthogonalTransform pcaTransform = null;

	/**
	 * DataCoverage constructor
	 * Default mode modeRANGE 
	 */
	public DataCoverageDescriptors() {
		super();

		datasetCenterPoint = DatasetCenterPoint._cmodeCenter;	
	}
	/**
	 * DataCoverage constructor 
	 * @param mode - ADomainMethodType
	 * _modeRANGE = 0, _modeLEVERAGE = 1; modeEUCLIDEAN = 2, 
	 * _modeCITYBLOCK = 3, _modeMAHALANOBIS = 4,  _modeDENSITY = 5;
	 */
	public DataCoverageDescriptors(ADomainMethodType mode) {
		super(mode);
		datasetCenterPoint = DatasetCenterPoint._cmodeCenter;		
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

	/**
	 * perform an inverse transformation of points <br>
	 * the result is stored in the second argument
	 * @param points  The points to be transformed by the inverse transform
	 * @return  the transformed points 
	 */
	public Matrix inverseTransform(Matrix points ) throws Exception {
		if (pca)
			return pcaTransform.InverseTransformPoints(points);
		else return points;
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
		if (getAppDomainMethodType().isDistance()) {
			return super.getName() + 
			" to " + datasetCenterPoint.toString() ;
		} else return super.getName();
	}
	

	/**
	 * 
	 * @param cmode - as in DatasetCenterPoint
	 * _cmodeCenter = 1;_cmodeMean = 0;_cmodeOrigin = 2;	
	 */
	public void setCmode(DatasetCenterPoint cmode) {
		if (!getAppDomainMethodType().isDistance()) return;
		datasetCenterPoint = cmode;

	}
	protected double estimateThreshold(double percent, double[] values) {
		double t;
		int tIndex = values.length;
		if (percent == 1) {
			t = Tools.max(values,values.length);
		}
		else {
		    Arrays.sort(values);
		    tIndex = (int) Math.round(values.length * (percent));
		    if (tIndex > values.length) tIndex = values.length;
			t = values[tIndex-1];
		}
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
	
	
	public double[] predict(Matrix data) {
		return assess(data);
	}
	public boolean build(Matrix data) {
		return estimate(data);
	}
	
	/**
	 *  does the actual assessment, to be overrided by inherited class
	 * @param points
	 * @param np
	 * @param nd
	 * @return double[]
	 */
	protected double[] doAssessment(Matrix points, int np, int nd) {
		double[] c = new double[np];
		for (int i = 0; i < np; i++) 
			c[i] = doAssessment(points.getArray()[i],nd);
		return c;
	}
	
	/**
	 * 
	 * @param points
	 * @return double[] vector containing estimated coverage
	 */
	public double[] assess(Matrix matrix) {
		//TODO throw exception
		
		if (isEmpty()) return null;
		if ((matrix == null) || (matrix.getRowDimension() < 1)) return null;
		int np = matrix.getRowDimension();
		int nd = mindata.length;
		//incompatible data
		if (nd !=matrix.getColumnDimension()) {
			//System.err.println("Incompatibe training and test data sets");
			return null;
		}
	
		if (pca) {
			//System.err.println("PCA transform ...");			
			Matrix pcapoints = pcaTransform.TransformPoints(matrix);
			//System.err.println("PCA transform done");			
			
			double [] c = doAssessment(pcapoints,np,nd);
			pcapoints = null;
			return c;
		} else //no pca
			return doAssessment(matrix,np,nd);
	}

	/**
	 * does the actual estimation, to be overrided by inherited class
	 * @param points
	 * @param np
	 * @param nd
	 */
	protected void doEstimation(Matrix points, int np, int nd) {
		mindata = Tools.min(points.getArray());
		maxdata = Tools.max(points.getArray());
		if (pThreshold == 1) threshold = 0;
		else {
			double[] values  = doAssessment(points,np,nd);
			threshold = estimateThreshold(pThreshold,values);
			values = null;
		}
	}
	
	public boolean estimate(Matrix m) {
		//TODO to throw exception
		pca = pca && (m.getRowDimension()>=m.getColumnDimension());

		if (!pca) scale = false;
		if ((m == null) || (m.getRowDimension() < 1)) return false;
		int d = m.getColumnDimension();
		
		if (pca) {
			try {
			
			pcaTransform = new OrthogonalTransform();
			//System.err.println("PCA running ...");
			pcaTransform.InitializeFilter(m,d,d);
			//System.err.println("PCA transform ...");			
			Matrix pcapoints = pcaTransform.TransformPoints(m);
			doEstimation(pcapoints,m.getRowDimension(),d);
			pcapoints = null;
			m = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			doEstimation(m,m.getRowDimension(),d);			
		}
		return !isEmpty();
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
}
