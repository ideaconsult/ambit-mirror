/**
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-7-26
 * <b>Project</b> ambit
 */
package ambit2.domain;


import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.smiles.SmilesParserWrapper;
import ambit2.data.descriptors.AtomEnvironment;
import ambit2.data.descriptors.AtomEnvironmentDescriptor;
import ambit2.data.molecule.Compound;
import ambit2.stats.Tools;
import ambit2.stats.datastructures.Histogram;
import ambit2.stats.datastructures.Sort;

/**
 * Abstract class for similarity analysis by {@link ambit2.data.descriptors.AtomEnvironmentDescriptor}.
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-7-26
 */
public abstract class DataCoverageAtomEnvironmentCore extends DataCoverage {
	protected int fingerprintsCalculated = 0;
    
    protected HydrogenAdder hAdder = null;
    protected AtomEnvironmentDescriptor atomEnvironment = null;
	protected int[] fpae = null;
	protected SmilesParserWrapper sp = null;
	protected boolean withHydrogens = true;

    /**
     * @param mode
     */
    public DataCoverageAtomEnvironmentCore(int mode) {
        super(mode);
    }
    
	public void clear() {
		super.clear();
		fingerprintsCalculated = 0;
		fpae = null;
	}
	protected IMolecule getMolecule(Compound cmp) throws Exception {
	    String smiles;
        IMolecule mol = cmp.getMolecule();
		if (mol == null) {
		    smiles = cmp.getSMILES();
		    if (!smiles.equals("")) {
		        if (sp ==null) sp = SmilesParserWrapper.getInstance();
		        try {
				mol = sp.parseSmiles(smiles);
		        } catch (InvalidSmilesException x) {
		            throw new Exception("Cannot parse SMILES\t"+smiles,x);
		        }
		    } else {
				mol = null;
		    }
		} else mol = (IMolecule) mol.clone();
		return mol;
	}	
	protected void calculateAtomEnvironment(IMolecule mol, 
	        Histogram fingerprints, Object[] params ,
	        Histogram summary) throws Exception {
	    if (mol == null) throw new Exception("Molecule not assigned");
	    
	    IAtomContainer newMol = mol;
	    if (withHydrogens) {
			if (hAdder == null) hAdder = new HydrogenAdder();
			IMolecule m = (IMolecule)((IMolecule)mol).clone();
		    hAdder.addExplicitHydrogensToSatisfyValency(m);
		    newMol = m;
	    } else {
	    	MFAnalyser mfa = new MFAnalyser(mol);
	    	newMol = mfa.removeHydrogensPreserveMultiplyBonded();
	    }
	    String key;
	     for (int a =0; a < newMol.getAtomCount(); a++) {
	            params[0] = new Integer(a);
	            atomEnvironment.setParameters(params);
	            if (fpae == null)
	                fpae = new int[atomEnvironment.getAtomFingerprintSize()]; 
	            atomEnvironment.doCalculation(newMol,fpae);
	            key = AtomEnvironment.atomFingerprintToString(fpae,',');
	            fingerprints.addObject(key);
	            if (summary != null) summary.addObject(key);
	    } //atoms
	}
	/**
	 * 
	 * @param data
	 * @param summary
	 * @return Histogram[] = new Histogram[data.size()]
	 */
	protected Histogram[] dataAtomEnvironments(AllData data, Histogram summary) {
		if (atomEnvironment == null) 
		    atomEnvironment = new AtomEnvironmentDescriptor();	    
		Histogram[] result = null; 
		result = new Histogram[data.size()];

		
		fingerprintsCalculated = 0;
		if (atomEnvironment == null) 
		    atomEnvironment = new AtomEnvironmentDescriptor();
		if (hAdder == null) hAdder = new HydrogenAdder();
		fpae = null;
		
		Object[] params = new Object[1];
		IMolecule mol = null;
		Compound cmp  = null;
		
		fingerprintsCalculated = 0;
		Histogram fp = null;
		for (int i = 0; i < data.size(); i++ ) {
		    cmp = data.getCompound(i);
			try {
			    mol = getMolecule(cmp);
			} catch (Exception x) {
				mol = null;
				System.err.println(x.getMessage());
			}		        
	        if (mol == null) {
	            result[i] = null;
	            continue;
	        }
			
	        try {
	            if (fp == null) fp = new Histogram();
	            calculateAtomEnvironment(mol,fp,params,summary);
	            result[i] = fp;
	            fp = null;
	            mol = null;
	            fingerprintsCalculated++;
		    } catch (Exception x) {
		        fp.clear();
		        result[i] = null;
		        System.err.println(x.getMessage());
		    }	    
		}
		fpae = null;
		return result;
	    
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
		System.err.println("Percent\t"+pThreshold*100+"\tThreshold\t"+t+"\tIndex\t"+tIndex + "\n");

		return t;
		
	}

	public boolean isWithHydrogens() {
		return withHydrogens;
	}

	public void setWithHydrogens(boolean withHydrogens) {
		this.withHydrogens = withHydrogens;
	}
}

