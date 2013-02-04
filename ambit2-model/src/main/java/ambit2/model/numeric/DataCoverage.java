/**
 * Created on 2005-4-1
 *
 */
package ambit2.model.numeric;

import java.util.logging.Logger;

import ambit2.base.exceptions.QSARModelException;


/**
 * A parent class to all classess that estimate and assess applicability domain 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public abstract class DataCoverage<DATA>  implements  IDataCoverage<DATA> /*, IMolecularDescriptor  TODO */{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5355137220284907331L;
	protected static Logger logger = Logger.getLogger(DataCoverage.class.getName());
	protected transient Object object = null;
	protected ADomainMethodType appDomainMethodType  = null;
	
	protected double threshold = 0;
	//percent - 1 means 100% , all points in
	protected double pThreshold = 1.0;

	/**
	 * 
	 */
	public DataCoverage() {
		super();
		setMode(ADomainMethodType._modeRANGE);
	}
	public DataCoverage(ADomainMethodType mode) {
		super();
		setMode(mode);
	}

	public String getDomainName() {
		return String.format("AppDomain_%s", appDomainMethodType.getName());
	}
	public void setDomainName(String domainName) {
		
	}
	public String getMetricName() {
		return String.format("%s", appDomainMethodType.getName());
	}
	public void setMetricName(String metricName) {
		
	}
	public String getId() {
		return  appDomainMethodType.getId();
	}
	
	/* (non-Javadoc)
	 * @see ambit2.domain.IDataCoverage#isEmpty()
	 */
	public boolean isEmpty() {
		return true;
	}
	/* (non-Javadoc)
	 * @see ambit2.domain.IDataCoverage#setMode(int)
	 */
	public void setMode(ADomainMethodType mode) {
		appDomainMethodType = mode;
		if (appDomainMethodType.isRange()) 
			pThreshold = 1;
	}
	
	public void clear() {
		
	}
	
	/* (non-Javadoc)
	 * @see ambit2.domain.IDataCoverage#getPThreshold()
	 */
	public double getPThreshold() {
		return pThreshold;
	}
	/* (non-Javadoc)
	 * @see ambit2.domain.IDataCoverage#setPThreshold(double)
	 */
	public void setPThreshold(double athreshold) {
		if (appDomainMethodType.isRange()) athreshold = 1;
		if (pThreshold != athreshold) clear();
		pThreshold = athreshold;
	}
	/* (non-Javadoc)
	 * @see ambit2.domain.IDataCoverage#getDomain(double)
	 */
	public int getDomain(double coverage) {
		int domain;
		if (coverage <= threshold) domain = 0;
		else domain =1;
		return domain;
	}	
	/* (non-Javadoc)
	 * @see ambit2.domain.IDataCoverage#getDomain(double[])
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
	 * @see ambit2.domain.IDataCoverage#getAppDomainMethodType()
	 */
	public ADomainMethodType getAppDomainMethodType() {
		return appDomainMethodType;
	}
	/* (non-Javadoc)
	 * @see ambit2.domain.IDataCoverage#setAppDomainMethodType(ambit2.domain.ADomainMethodType)
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

	
	/* (non-Javadoc)
	 * @see ambit2.domain.IDataCoverage#getPoints()
	 */
	public Object getPoints() {
		return object;
	}

    /* (non-Javadoc)
	 * @see ambit2.domain.IDataCoverage#setParameters(ambit2.domain.AllData)
	 */
    public void setParameters(Object points) throws QSARModelException {
    	this.object = points;
    }	
  
}
