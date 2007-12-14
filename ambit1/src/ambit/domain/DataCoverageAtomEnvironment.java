/**
 * <b>Filename</b> DataCoverageAtomEnvironment.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-17
 * <b>Project</b> ambit
 */
package ambit.domain;

import java.util.Arrays;

import org.openscience.cdk.interfaces.IMolecule;

import ambit.data.molecule.Compound;
import ambit.stats.datastructures.Histogram;

/**
 * Similarity analysis by {@link ambit.data.descriptors.AtomEnvironmentDescriptor}.
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-17
 */
public class DataCoverageAtomEnvironment extends DataCoverageAtomEnvironmentCore {
	protected AtomEnvironmentDistanceType aeComparison = null;
	
	protected Histogram consensusFingerprint = null;
	
	protected Histogram[] histogramsFor1NN = null;
	protected double [] minDistanceFor1NN = null;
	
    /**
     * 
     */
    public DataCoverageAtomEnvironment() {
        super(ADomainMethodType._modeATOMENVIRONMENT);
		aeComparison = new AtomEnvironmentDistanceType(0);        
    }

    /**
     * @param mode
     */
    public DataCoverageAtomEnvironment(int mode) {
        super(ADomainMethodType._modeATOMENVIRONMENT);
		aeComparison = new AtomEnvironmentDistanceType(mode);        
    }
	public void clear() {
		super.clear();
		if (consensusFingerprint != null) consensusFingerprint.clear();
	}
	
	
	protected boolean estimateByConsensus(AllData data) {
	    
	    Histogram result[] = dataAtomEnvironments(data,consensusFingerprint);
	    double A[] = new double[result.length];
	    for (int i = 0; i < result.length; i++) 
	        if (result[i] != null) {
		        try {
		            A[i] = result[i].hellinger(consensusFingerprint);
		        } catch (Exception x) {
		            x.printStackTrace();
		            A[i] = -1;
		        }
	        } else  A[i] = -1;
	    
		threshold = estimateThreshold(pThreshold,A) ;
		result = null;
		A = null;
		return (fingerprintsCalculated > 0);
	}

	protected double[] assessByConsensus(AllData data) {
    
		double[] values = new double[data.size()];
		IMolecule mol;
		Compound cmp;
		Histogram fingerprints = new Histogram();
		Object[] params = new Object[1];
		//System.err.println("Consensus fingerprints\n"+consensusFingerprint.toString());		
		for (int i = 0; i < data.size(); i++ ) {
		    cmp = data.getCompound(i);
		    try {
		        mol = getMolecule(cmp);
			} catch (Exception x) {
				mol = null;
				System.err.println(x.getMessage());
			}	
	        if (mol == null) {
			    values[i] = Double.NaN;
			    continue;
	        }	        
	        fingerprints.clear();
	        try {
	            calculateAtomEnvironment(mol,fingerprints,params,null);
	            values[i]  = fingerprints.hellinger(consensusFingerprint);
            
		    } catch (Exception x) {
		        //TODO exception
		        x.printStackTrace();
		        values[i] = Double.NaN;
		    }	    
		}
		fingerprints = null;
		System.err.println("Number of patterns in consensus fingerprint\t"+consensusFingerprint.size());		
		return values;
	}	
	/**
	 * the similarity to the nearest molecule. 
	 * "Nearness" is the number of common fragments / number of fragments
	 * An atom environment is considered a fragment
	 * All molecules in the training set have nearness = 1; 
	 * @param data
	 */
	protected boolean estimateBy1NN(AllData data) {
			    
		histogramsFor1NN = dataAtomEnvironments(data,consensusFingerprint);
		minDistanceFor1NN = new double[histogramsFor1NN.length];
		for (int i = 0; i < minDistanceFor1NN.length; i++) 
		    minDistanceFor1NN[i] = -1;
		
		/*
		threshold =0;
	    double d;
	    for (int i = 0; i < histogramsFor1NN.length; i++) 
	        for (int j = i+1; j < histogramsFor1NN.length; j++) {
		        if (histogramsFor1NN[i] != null) {
			        try {
			            d = 1- histogramsFor1NN[i].tanimoto(histogramsFor1NN[j]);
			        } catch (Exception x) {
			            x.printStackTrace();
			            d = -1;
			        }
			        
		        } else  d = -1;
		        if ((minDistanceFor1NN[i] == -1) || (minDistanceFor1NN[i] > d))
		        	minDistanceFor1NN[i] = d;
		        if ((minDistanceFor1NN[j] == -1) || (minDistanceFor1NN[j] > d))
		        	minDistanceFor1NN[j] = d;
	        		                          
	        }
	    /*
	    for (int i = 0; i < histogramsFor1NN.length; i++) {
	        if (histogramsFor1NN[i] == null) minDistanceFor1NN[i] = Double.NaN;
	        else minDistanceFor1NN[i] = minDistanceFor1NN[i] / histogramsFor1NN[i].size(); 
	    }
	    */
		double A[] = assessBy1NN(data);
		threshold = estimateThreshold(pThreshold,A) ;

		return (fingerprintsCalculated > 0);
	}
	protected double[] assessBy1NN(AllData data) {
	    
	    	double d=-1;
	    	int di = -1;
	    	int c =0;
			double[] values = new double[data.size()];
			IMolecule mol;
			Compound cmp;
			Histogram fingerprints = new Histogram();
			Object[] params = new Object[1];
			//System.err.println("Consensus fingerprints\n"+consensusFingerprint.toString());
	        double buf[] = new double[histogramsFor1NN.length];
			
			for (int i = 0; i < data.size(); i++ ) {
			    cmp = data.getCompound(i);
			    try {
			        mol = getMolecule(cmp);
				} catch (Exception x) {
					mol = null;
					System.err.println(x.getMessage());
				}	
		        if (mol == null) {
				    values[i] = Double.NaN;
				    continue;
		        } else values[i] = 0;	        
		        
		        fingerprints.clear();

		        try {
		            calculateAtomEnvironment(mol,fingerprints,params,null);
		            c = fingerprints.size();
		            if (c > 0) {
		                
			            for (int j=0; j < histogramsFor1NN.length; j++) {
		                    buf[j] = -1;
			                if (histogramsFor1NN[j] == null) continue;
			                d = 1- histogramsFor1NN[j].tanimoto(fingerprints);
			                buf[j] = d;
		                    //System.out.println("Compound\t"+ (i+1) + "Compound\t"+ (j+1) + "\tcommon fragments\t"+d + "\tMy size\t" + c);
			                /*
			                if ((j==0) || (values[i] > d)) {
			                    values[i] = d;
			                    di = j;
			                } 
			                */
			                //values[i] += d;
			            }
	                    //System.out.println("Compound\t"+ (di+1) + "\tcommon fragments\t"+d + "\tMy size\t" + c);
			            int L = 0;
			            long tL = Math.round(buf.length / 10.0);
			            if (tL < 3) tL=3;
			            Arrays.sort(buf);
			            for (int j=0; j < buf.length; j++)
			                if ((buf[j] > -1) && (L < tL)) { 
			                    values[i] += buf[j];
			                    L++;
			                }    
			            values[i] = values[i] / L;    
		            	//values[i] = values[i] / histogramsFor1NN.length;
		        	} else {
		                values[i] = 0;
		            }
		            
		            
			    } catch (Exception x) {
			        //TODO exception
			        x.printStackTrace();
			        values[i] = Double.NaN;
			    }	    
			}
			fingerprints = null;
			return values;
		}	
	public boolean build(AllData data) {
	    boolean result = false;
		switch (aeComparison.getId()) {
		case 1: {
			if (consensusFingerprint != null)  consensusFingerprint.clear();		    
			result = estimateBy1NN(data);
			break;
		}
		default: {
		    histogramsFor1NN = null;
		    
			if (consensusFingerprint == null) 
			    consensusFingerprint = new Histogram();
			else consensusFingerprint.clear();
			
			result = estimateByConsensus(data);
		    System.out.println("Atom environments calculated for "+fingerprintsCalculated+" molecules.");
		}
		}	    
		setNotModified();
		return result;
		
	}
	/*
	public int[] getDomain(double[] coverage) {
		switch (aeComparison.getId()) {
		case 1: {
			if (coverage == null) return null;
			int np = coverage.length;
			int[] domain = new int[np];
			for (int i = 0; i < np; i++) 
				if (new Double(coverage[i]).isNaN()) domain[i] = 2;
				else if (coverage[i] > threshold) domain[i] = 0;
				else domain[i] =1;
			return domain;
		}
		default: {
		    return super.getDomain(coverage);
		}
		}	    
	    
		    

	}	
	*/
	public boolean isEmpty() {
		switch (aeComparison.getId()) {
		case 1: {
		    return (histogramsFor1NN == null) || (fingerprintsCalculated == 0);
		}
		default: {
		    return (consensusFingerprint == null) || (fingerprintsCalculated == 0);
		}
		}	    
	    
		
	}
	
	public double[] predict(AllData data) {
		switch (aeComparison.getId()) {
		case 1: {
			return assessBy1NN(data);
		}
		default: {
			return assessByConsensus(data);
		}
		}	    
	    
	}
	public String getName() {
		return "AtomEnvironments (" + aeComparison.getName() + ")";
	}	
	public AtomEnvironmentDistanceType getAEComparison() {
		return aeComparison;
	}
	public void setAEComparison(AtomEnvironmentDistanceType aeComparison) {
		this.aeComparison.setId(aeComparison.getId());
	}
	public void setAEComparison(String aeType) {
		this.aeComparison.setName(aeType);
	}



}

