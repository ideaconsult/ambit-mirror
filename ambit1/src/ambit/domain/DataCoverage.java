/**
 * Created on 2005-4-1
 *
 */
package ambit.domain;

import org.openscience.cdk.qsar.model.IModel;
import org.openscience.cdk.qsar.model.QSARModelException;

import ambit.data.AmbitObject;


/**
 * A parent class to all classess that estimate and assess applicability domain 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public abstract class DataCoverage extends AmbitObject implements  IDataCoverage {
	protected  Object object = null;
	
	ADomainMethodType appDomainMethodType  = null;
	
	protected double threshold = 0;
	//percent - 1 means 100% , all points in
	protected double pThreshold = 1.0;

	/**
	 * 
	 */
	public DataCoverage() {
		super();
		appDomainMethodType = new ADomainMethodType();		
	}
	public DataCoverage(int mode) {
		super();
		appDomainMethodType = new ADomainMethodType(mode);
	}
	/* (non-Javadoc)
	 * @see ambit.domain.IDataCoverage#isEmpty()
	 */
	public boolean isEmpty() {
		return true;
	}
	/* (non-Javadoc)
	 * @see ambit.domain.IDataCoverage#setMode(int)
	 */
	public void setMode(int mode) {
		appDomainMethodType.setId(mode);
		if (appDomainMethodType.isRange()) 
			pThreshold = 1;
	}
	
	public void clear() {
		super.clear();
	}
	
	/* (non-Javadoc)
	 * @see ambit.domain.IDataCoverage#getPThreshold()
	 */
	public double getPThreshold() {
		return pThreshold;
	}
	/* (non-Javadoc)
	 * @see ambit.domain.IDataCoverage#setPThreshold(double)
	 */
	public void setPThreshold(double athreshold) {
		if (appDomainMethodType.isRange()) athreshold = 1;
		if (pThreshold != athreshold) clear();
		pThreshold = athreshold;
	}
	/* (non-Javadoc)
	 * @see ambit.domain.IDataCoverage#getDomain(double)
	 */
	public int getDomain(double coverage) {
		int domain;
		if (coverage <= threshold) domain = 0;
		else domain =1;
		return domain;
	}	
	/* (non-Javadoc)
	 * @see ambit.domain.IDataCoverage#getDomain(double[])
	 */
	public int[] getDomain(double[] coverage) {
		if (coverage == null) return null;
		int np = coverage.length;
		int[] domain = new int[np];
		for (int i = 0; i < np; i++) 
			if (new Double(coverage[i]).isNaN()) domain[i] = 2;
			else if (coverage[i] <= threshold) domain[i] = 0;
			else domain[i] =1;
		return domain;
	}
	public String getName() {
		return appDomainMethodType.getName();
	}

	/* (non-Javadoc)
	 * @see ambit.domain.IDataCoverage#getAppDomainMethodType()
	 */
	public ADomainMethodType getAppDomainMethodType() {
		return appDomainMethodType;
	}
	/* (non-Javadoc)
	 * @see ambit.domain.IDataCoverage#setAppDomainMethodType(ambit.domain.ADomainMethodType)
	 */
	public void setAppDomainMethodType(ADomainMethodType appDomainMethodType) {
		this.appDomainMethodType = appDomainMethodType;
	}	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	public String toString() {
		return getName();
	}
	/*
	public abstract boolean build(Object data);
	public abstract double[] predict(Object data);
	*/
	/*
	public void build() throws QSARModelException {
		build(getPoints());
	}
	public void predict() throws QSARModelException {
		predict(getPoints());
		
	}
	*/
	/* (non-Javadoc)
	 * @see ambit.domain.IDataCoverage#getPoints()
	 */
	public Object getPoints() {
		return object;
	}

    /* (non-Javadoc)
	 * @see ambit.domain.IDataCoverage#setParameters(ambit.domain.AllData)
	 */
    public void setParameters(Object points) throws QSARModelException {
    	this.object = points;
    }	
}
