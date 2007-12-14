/**
 * Created on 2005-1-20
 *
 */
package ambit.domain;

import java.io.DataOutputStream;

import ambit.data.AmbitObject;
import ambit.data.molecule.CompoundsList;


/**
 * Statistics for the applicability domain assessment
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DataCoverageStats extends AmbitObject implements IDataCoverageStats {
	protected int noIn, noOut, noNotAssessed;
	protected double rmseIn, rmseOut, rmseNotAssessed, rmseAll;
	protected boolean assessed;
	protected DataCoverage method;
	protected int[] inDomain;
	protected double[] coverage;
	/**
	 * Creates DataCoverageStats instance with null DataCoverage method
	 * Use setMethod(DataCoverage method) to assign a method
	 */
	public DataCoverageStats() {
		super();
		clear();
		method = null;
	}
	
	public void clear() {
		super.clear();
		assessed = false;
		noIn = 0;
		noOut= 0;
		noNotAssessed = 0;
		rmseIn = 0;
		rmseOut = 0;
		rmseNotAssessed = 0;
		inDomain = null;
		coverage = null;
		//if (method != null) method.clear();
	}
	/**
	 * @return Returns the noIn.
	 */
	public int getNoIn() {
		return noIn;
	}
	/**
	 * @return Returns the noOut.
	 */
	public int getNoOut() {
		return noOut;
	}
	/**
	 * @return Returns the rmseIn.
	 */
	public double getRmseIn() {
		return rmseIn;
	}
	/**
	 * @return Returns the rmseOut.
	 */
	public double getRmseOut() {
		return rmseOut;
	}
	
	
	/**
	 * @return Returns the assessed.
	 */
	public boolean isAssessed() {
		return assessed;
	}
	/**
	 * Estimates coverage of a data set, i.e. 
	 * finds ranges, distance threshold, density estimation   
	 *
	 */
	public boolean estimate(QSARDataset ds) {
		if (method == null) {
			//System.err.println("coverage: no method assigned");			
			return  false;
		}
		else {
			assessed = method.build(ds.data);
			return assess(ds);
		}
	}

	/**
	 * Assess coverage of a data set, i.e. checks which point is 
	 * in-range, in-threshold, in-dense areas
	 */
	public boolean assess(QSARDataset ds) {
		if (method == null) {
			System.err.println("coverage: no method assigned");
			return false;
		} else if ( method.isEmpty()) {
			System.err.println("coverage not estimated!");
			return false;		    
		} else {
			noIn = 0;
			noOut= 0;
			coverage = method.predict(ds.data);
			assessed = coverage != null;			
			if (assessed) {
				inDomain = method.getDomain(coverage);
				double residual = 0;
				
				for (int i = 0; i < inDomain.length; i++) {
					residual = ds.getResidual(i);
					residual = residual * residual;
					if (inDomain[i] == 0) { 
					    noIn++; rmseIn += residual; 
					} else if (inDomain[i] ==1) { 
					    noOut++; rmseOut += residual;
					}
					else { 
					    noNotAssessed++; 
					    rmseNotAssessed += residual;
					}
				    rmseAll += residual;
				}
				if (noIn > 0) 	rmseIn /= noIn;
				if (rmseIn > 0) rmseIn = Math.sqrt(rmseIn);
				if (noOut > 0)  rmseOut /= noOut;
				if (rmseOut > 0) rmseOut = Math.sqrt(rmseOut);
				if (rmseNotAssessed > 0) rmseNotAssessed = Math.sqrt(noNotAssessed);
				if (inDomain.length > 0)   rmseAll /= inDomain.length;

			}
			//System.err.println(ds.getCaption()+"\t" + method.getCaption()+"\tin\t"+noIn+"\tout\t"+noOut);
			assessed = coverage != null;
			return assessed;
		}
	}	
	/**
	 * @return Returns the method.
	 */
	public DataCoverage getMethod() {
		return method;
	}
	/**
	 * @param method The method to set.
	 */
	public void setMethod(DataCoverage method) {
		clear();
		this.method = method;
		//if (method != null)
		//System.err.println("set method\t"+method.getCaption());
	}
	
	public boolean isInDomain(int point) throws Exception {
		return inDomain[point] == 0;
	}
	public double getCoverage(int point) throws Exception {
	    if (inDomain[point] == 2) //not accessed
	        return Double.NaN;
		else return coverage[point];
	}

	public String getName() {
		if (assessed && (method != null)) return method.getName();
		else return "Coverage";
	}
	public void setName() {
		
	}
	public String toString() {
		StringBuffer b = new StringBuffer();
		if (method != null)	b.append(method.toString());
		else b.append("No method assigned");
		if (assessed) {
			b.append("No (in)=");
			b.append(noIn);		
			b.append("\tNo (out)=");
			b.append(noOut);
			b.append("RMSE (in)=");
			b.append(rmseIn);		
			b.append("\tRMSE (out)=");
			b.append(rmseOut);			
		} else b.append("\t not assessed");
		return b.toString();
	}
	public int getNoNotAssessed() {
		return noNotAssessed;
	}
	public void setNoNotAssessed(int noNotAssessed) {
		this.noNotAssessed = noNotAssessed;
	}
	public CompoundsList getCompoundsInDomain(QSARDataset ds) {
		return getCompounds(ds,0);
	}
	public CompoundsList getCompoundsOutOfDomain(QSARDataset ds) {
		return getCompounds(ds,1);
	}
	public CompoundsList getCompoundsNotAssessed(QSARDataset ds) {
		return getCompounds(ds,2);
	}
	
	protected CompoundsList getCompounds(QSARDataset ds, int domain) {
		if (ds == null) return null;
		AllData data = ds.getData();
		if (data == null) return null;
		
		CompoundsList list = new CompoundsList(); 
		for (int i = 0; i < inDomain.length; i++) 
			if (inDomain[i] == domain) {
				list.addItem(data.getCompound(i));
			}
		return list;
	}
	/**
	 * @see ambit.domain.IDataCoverageStats#writeData(int, DataOutputStream, char)
	 */
	public void writeData(int index, DataOutputStream out, char delimiter)
			throws Exception {
		out.writeBytes(Double.toString(getCoverage(index)));					
		out.write(delimiter);
		out.writeBytes(Boolean.toString(isInDomain(index)));
	}
	/* (non-Javadoc)
	 * @see ambit.data.domain.IDataCoverageStats#writeHeader(java.io.DataOutputStream, char, char)
	 */
	public void writeHeader(DataOutputStream out, char delimiter,
			char textDelimiter) throws Exception {
		out.write(textDelimiter);
		out.writeBytes(getName());
		out.write(textDelimiter);
		out.write(delimiter);
		out.write(textDelimiter);
		out.writeBytes("in domain");
		out.write(textDelimiter);
	}
    public double getRmseAll() {
        return rmseAll;
    }
}
