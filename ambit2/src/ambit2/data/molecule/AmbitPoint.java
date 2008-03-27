/**
 * Created on 2005-3-30
 *
 */
package ambit2.data.molecule;

import ambit2.data.AmbitObject;
import ambit2.data.descriptors.DescriptorsList;


/**
 * A QSAR point : {@link Compound} , xvalues, ypredicted, yobserved, coverage
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitPoint extends AmbitObject {
	public static int _ypIndex = 0; //predicted
	public static int _yoIndex = 1; // observed
	public static int _yrIndex = 2; //residual
	
	public static int _adCoverage = 0;
	public static int _adDomain = 1;
	public static int _adStructuralCoverage = 2;
	public static int _adStructuralDomain = 3;	
	
	protected Compound compound = null;
	protected double[] yvalues = null;
	
	protected double[] coverage = null;

	protected double[] xvalues = null;
	protected DescriptorsList descriptors = null;
	/**
	 * 
	 */
	public AmbitPoint() {
		super();
	}

	/**
	 * @param name
	 */
	public AmbitPoint(String name) {
		super(name);
	}
	
	/**
	 * @param name
	 * @param id
	 */
	public AmbitPoint(String name, int id) {
		super(name, id);
	}
	/**
	 * 
	 */
	public AmbitPoint(Compound compound) {
		super();
		this.compound = compound;
	}
		
	public void clear() {
		super.clear();
		compound = null;
		//yPredicted = null;
		//yObserved = null;
		coverage = null;
		xvalues = null;
		yvalues = null;
	}
	public boolean equals(Object object) {
		boolean m = super.equals(object);
		if (m) {
			AmbitPoint p = (AmbitPoint) object;

			if (compound == null) m = (p.compound == null);
			else m = compound.equals(p.compound);
			
			if (yvalues != null) {
				 double[] y = (double[]) yvalues;
				 double[] py = (double[]) p.yvalues;
				 m &= (y.length == py.length);
				 if (m) 
				 	for (int i=0; i < y.length; i++) 
				 		if (y[i] != py[i]) 
				 			return false;
			} else m &= (p.xvalues == null);	 		

			
			if (xvalues != null) {
				 double[] x = (double[]) xvalues;
				 double[] px = (double[]) p.xvalues;
				 m &= (x.length == px.length);
				 if (m) 
				 	for (int i=0; i < x.length; i++) 
				 		if (x[i] != px[i]) 
				 			return false;
			} else m &= (p.xvalues == null);	 		
		}
		return m;
	}
	
	public Object clone() throws CloneNotSupportedException {
		//this is a SHALLOW copy !!! 
		//TODO Deep copy
		AmbitPoint point = (AmbitPoint) super.clone();
		if (compound != null)
			point.compound = (Compound)compound.clone();

		if (coverage == null) point.setCoverage(null);
		else point.setCoverage( (double[]) coverage.clone());		
		point.coverage = coverage;
/*		
		if (yPredicted == null ) point.yPredicted = null;
		else point.yPredicted = new Double(yPredicted.doubleValue());
		if (yObserved == null) point.yObserved = null;
		else point.yObserved = new Double(yObserved.doubleValue());
		if (coverage == null) point.coverage = null; else		
		point.coverage = new Double(coverage.doubleValue());
		
		if (domain == null) point.domain = null; else 
		point.domain = new Double(domain.doubleValue());
		*/
		
		point.descriptors = descriptors;
		if (xvalues == null) point.setXvalues(null);
		else point.setXvalues( (double[]) xvalues.clone());
		if (yvalues == null) point.setYvalues(null);
		else point.setYvalues( (double[]) yvalues.clone());
		
		return point;
	}
	protected double[] initValues(int n) {
		double[] val = new double[n];
		for (int i = 0; i < n; i++) val[i] = Double.NaN;
		return val;
	}
	public String getDescriptorName(int i) {
		if (descriptors == null) return "";
		else return descriptors.getDescriptorName(i);
	}
	protected static String getValue(Double o) {
		if (o == null) return "";
		else return o.toString();
	}	
	public String getXValue(int i) {
		if (xvalues == null) return "";
		else return Double.toString(((double []) xvalues)[i]);
	}
	public void setXValue(int i, String value) {
		if (xvalues != null)
			try {
				((double []) xvalues)[i] = Double.parseDouble(value);
			} catch (Exception x) {
				//do nothing
			}
	}	
	public double[] getXvalues() {
		return (double []) xvalues;
	}
	public void setXvalues(double[] xvalues) {
		this.xvalues = xvalues;
	}
	public double[] getYvalues() {
		return (double []) xvalues;
	}
	public void setYvalues(double[] yvalues) {
		this.yvalues = yvalues;
	}
	
	public double getYObservedDouble() {
		
		if (yvalues == null) return Double.NaN;
		else return yvalues[_yoIndex];
	}
	public double getYPredictedDouble() {
		if (yvalues == null) return Double.NaN;
		else return yvalues[_ypIndex];
	}
	public String getYObservedString() {
		if (yvalues == null) return "";
		else return Double.toString(yvalues[_yoIndex]);
	}
	public String getYPredictedString() {
		if (yvalues == null) return "";
		else return Double.toString(yvalues[_ypIndex]);
	}		
	
	public String getCoverageString() {
		if (coverage == null) return "";
		else return Double.toString(coverage[_adCoverage]);
	}		
	public String getDomainString() {
		if (coverage == null) return "";
		else return Double.toString(coverage[_adDomain]);
	}		
	public String getStructuralCoverageString() {
		if (coverage == null) return "";
		else return Double.toString(coverage[_adStructuralCoverage]);
	}		
	public String getStructuralDomainString() {
		if (coverage == null) return "";
		else return Double.toString(coverage[_adStructuralDomain]);
	}			
	public void setStructuralCoverage(double value) {
		if (coverage == null) coverage = initValues(_adStructuralDomain+1);
		coverage[_adStructuralCoverage] = value;
	}	
	public void setStructuralDomain(double value) {
		if (coverage == null) coverage = initValues(_adStructuralDomain+1);
		coverage[_adStructuralDomain] = value;
	}
	public void setDomain(double value) {
		if (coverage == null) coverage = initValues(_adStructuralDomain+1);
		coverage[_adDomain] = value;
	}	
	public void setCoverage(double value) {
		if (coverage == null) coverage = initValues(_adStructuralDomain+1);
		coverage[_adCoverage] = value;
	}
	
	public void setYObserved(double observed) {
		if (yvalues == null) yvalues = initValues(_yrIndex+1);
		yvalues[_yoIndex] = observed;
		
	}
	public void setYPredicted(double predicted) {
		if (yvalues == null) yvalues = initValues(_yrIndex+1);
		yvalues[_ypIndex] = predicted;
	}
	public void setYObserved(String observed) {
		try {
			double d = Double.parseDouble(observed);
			setYObserved(d);
		} catch (Exception x) {
			setYObserved(Double.NaN);			
		}
	}
	public void setYPredicted(String predicted) {
		try {
			double d = Double.parseDouble(predicted);
			setYPredicted(d);
		} catch (Exception x) {
			setYPredicted(Double.NaN);			
		}
	}
	
	public Compound getCompound() {
		return compound;
	}
	public void setCompound(Compound compound) {
		this.compound = compound;
	}
	public DescriptorsList getDescriptors() {
		return descriptors;
	}
	public void setDescriptors(DescriptorsList descriptors) {
		this.descriptors = descriptors;
	}
	public double[] getCoverage() {
		return coverage;
	}
	public void setCoverage(double[] coverage) {
		this.coverage = coverage;
	}
	public String coverageStatus() {
		if (coverage == null) return "";
		StringBuffer b = new StringBuffer();
		String s = getCoverageString();
		if (!s.equals("")) {
			b.append("\t descriptor space: ");
			b.append(s);
		}
		s = getStructuralCoverageString();
		if (!s.equals("")) {
			b.append("\t structure space: ");
			b.append(s);
		}
		return "Coverage: " + b.toString();
	}
	public String toString() {
		String s = "";
		if (compound != null ) s = compound.toString();		
		if (descriptors != null ) s += descriptors.toString();
		
		return super.toString() + coverageStatus() + s; 
	}
}
