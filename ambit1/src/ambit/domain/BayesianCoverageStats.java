/*
 * Created on 2005-6-30
 *
 */
package ambit.domain;

import java.io.DataOutputStream;

import ambit.data.AmbitObject;
import ambit.data.molecule.CompoundsList;

/**
 * This class consists of an array of {@link ambit.domain.DataCoverageStats}
 * The primary purpose is to keep the results obtained by various applicability domain assessment methods 
 * and to combine in Bayesian way.
 * Other combinations of the results are also planned
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-7-12
 */
public class BayesianCoverageStats extends AmbitObject implements
		IDataCoverageStats {
	protected DataCoverageStats coverageStats[] = null;
	protected int current = -1;
	/**
	 * 
	 */
	public BayesianCoverageStats() {
		super();
		init();
	}

	/**
	 * @param name
	 */
	public BayesianCoverageStats(String name) {
		super(name);
		init();
	}

	/**
	 * @param name
	 * @param id
	 */
	public BayesianCoverageStats(String name, int id) {
		super(name, id);
		init();
	}
	public void clear() {
		if ((current >=0) && (coverageStats[current] != null))
			coverageStats[current].clear();
	}
	protected void init() {
		int n = ADomainMethodType.methodName.length;
		coverageStats = new DataCoverageStats[n];
		for (int i = 0; i < n; i++) coverageStats[i] = null;
		current = -1;
	}
	/**
	 * @see ambit.domain.IDataCoverageStats#getMethod()
	 */
	public DataCoverage getMethod() {
		if (current >= 0) return coverageStats[current].getMethod();
		else return null;
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#setMethod(ambit.domain.DataCoverage)
	 */
	public void setMethod(DataCoverage method) {
		if (method == null) current = -1;
		else {
			int p = method.appDomainMethodType.getId();
			if (coverageStats[p] == null) coverageStats[p] = new DataCoverageStats();
			coverageStats[p].setMethod(method);
			current = p;
		}
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#getNoIn()
	 */
	public int getNoIn() {
		if (current >= 0) return coverageStats[current].getNoIn();
		else return 0;
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#getNoOut()
	 */
	public int getNoOut() {
		if (current >= 0) return coverageStats[current].getNoOut();
		else return 0;
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#getRmseIn()
	 */
	public double getRmseIn() {
		if (current >= 0) return coverageStats[current].getRmseIn();
		else return Double.NaN;
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#getRmseOut()
	 */
	public double getRmseOut() {
		if (current >= 0) return coverageStats[current].getRmseOut();
		else return Double.NaN;
	}
	/* (non-Javadoc)
     * @see ambit.data.domain.IDataCoverageStats#getRmseAll()
     */
    public double getRmseAll() {
		if (current >= 0) return coverageStats[current].getRmseAll();
		else return Double.NaN;
    }
	/**
	 * @see ambit.domain.IDataCoverageStats#isAssessed()
	 */
	public boolean isAssessed() {
		if (current >= 0)
			return coverageStats[current].isAssessed();
		else return false;
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#estimate(ambit.domain.QSARDataset)
	 */
	public boolean estimate(QSARDataset ds) {
		return coverageStats[current].estimate(ds);
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#assess(ambit.domain.QSARDataset)
	 */
	public boolean assess(QSARDataset ds) {
		return coverageStats[current].assess(ds);
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#isInDomain(int)
	 */
	public boolean isInDomain(int point) throws Exception {
		return coverageStats[current].isInDomain(point);
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#getCoverage(int)
	 */
	public double getCoverage(int point) throws Exception {
		return coverageStats[current].getCoverage(point);
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#getNoNotAssessed()
	 */
	public int getNoNotAssessed() {
		return coverageStats[current].getNoNotAssessed();
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#setNoNotAssessed(int)
	 */
	public void setNoNotAssessed(int noNotAssessed) {
		coverageStats[current].setNoNotAssessed(noNotAssessed);
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#getCompoundsInDomain(ambit.domain.QSARDataset)
	 */
	public CompoundsList getCompoundsInDomain(QSARDataset ds) {
		return coverageStats[current].getCompoundsInDomain(ds);
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#getCompoundsOutOfDomain(ambit.domain.QSARDataset)
	 */
	public CompoundsList getCompoundsOutOfDomain(QSARDataset ds) {
		return coverageStats[current].getCompoundsOutOfDomain(ds);
	}

	/**
	 * @see ambit.domain.IDataCoverageStats#getCompoundsNotAssessed(ambit.domain.QSARDataset)
	 */
	public CompoundsList getCompoundsNotAssessed(QSARDataset ds) {
		return coverageStats[current].getCompoundsNotAssessed(ds);
	}
	/**
	 * returns the name of the current method
	 */
	public String getName() {
		return coverageStats[current].getName();
	}
	public void setName() {
		
	}
	public String toString() {
		if ((current < 0) || (coverageStats[current] == null)) 
				return getClass().getName() +  "\tMethod not assigned";
		else return coverageStats[current].toString();
	}
	public boolean isModified() {
		DataCoverageStats ds = coverageStats[current];
		if (ds == null) return false; else return ds.isModified();
	}
	/* (non-Javadoc)
	 * @see ambit.data.domain.IDataCoverageStats#save(int, java.io.DataOutputStream, char)
	 */

	public void writeData(int index, DataOutputStream out, char delimiter)
	throws Exception {
		int c = 0;
		int cIn = 0;
		for (int i = 0; i < coverageStats.length; i++) 
			if ((coverageStats[i] != null) && (coverageStats[i].isAssessed())) {
				if (c>0) out.write(delimiter);
				coverageStats[i].writeData(index,out,delimiter);
				c++;
				if (coverageStats[i].isInDomain(index)) cIn++;
			}
		 			
		if (c>0) {
			out.write(delimiter);
			double d = (double) cIn/ (double) c;
			out.writeBytes(Double.toString(d));
			out.write(delimiter);
			out.writeBytes(Boolean.toString(d >= 0.5));
		}
		/*	
		out.writeBytes(Double.toString(getCoverage(index)));					
		out.write(delimiter);
		out.writeBytes(Boolean.toString(isInDomain(index)));
		*/
	}
	/* (non-Javadoc)
	 * @see ambit.data.domain.IDataCoverageStats#writeHeader(java.io.DataOutputStream, char, char)
	 */
	public void writeHeader(DataOutputStream out, char delimiter,
			char textDelimiter) throws Exception {
		boolean c = false;
		for (int i = 0; i < coverageStats.length; i++) 
			if ((coverageStats[i] != null) && (coverageStats[i].isAssessed())) {
				if (c) out.write(delimiter);
				coverageStats[i].writeHeader(out,delimiter,textDelimiter);
				c = true;
			}	
		if (c) {
			out.write(delimiter);
			out.write(textDelimiter);
			out.writeBytes("Consensus domain");
			out.write(textDelimiter);
			out.write(delimiter);
			out.write(textDelimiter);
			out.writeBytes("in domain");
			out.write(textDelimiter);			
		}
			/*
		out.write(textDelimiter);
		out.writeBytes(getName());
		out.write(textDelimiter);
		out.write(delimiter);
		out.write(textDelimiter);
		out.writeBytes("in domain");
		out.write(textDelimiter);
		*/
	}
	
}
