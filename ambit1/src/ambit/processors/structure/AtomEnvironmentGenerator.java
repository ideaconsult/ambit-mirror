package ambit.processors.structure;

import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.HydrogenAdder;

import ambit.data.IAmbitEditor;
import ambit.data.descriptors.AtomEnvironment;
import ambit.data.descriptors.AtomEnvironmentDescriptor;
import ambit.data.descriptors.AtomEnvironmentList;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.DefaultAmbitProcessor;
import ambit.processors.DefaultProcessorEditor;
import ambit.processors.IAmbitResult;

/**
 * A processor to generate atom environments. Uses {@link ambit.data.descriptors.AtomEnvironmentDescriptor} for actual generation.
 * The result is contained in {@link ambit.data.descriptors.AtomEnvironmentList} and is assigned as a molecule property 
 * with a name {@link AmbitCONSTANTS#AtomEnvironment}. <br>
 * Used by {@link ambit.database.writers.AtomEnvironmentWriter}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class AtomEnvironmentGenerator extends DefaultAmbitProcessor  {
    protected HydrogenAdder hAdder = null;
	protected AtomEnvironmentDescriptor aeDescriptor = null;
	protected Object[] aeParams = null;
    protected boolean useHydrogens = false;
    protected Integer maxLevels = new Integer(3);
    protected boolean createSubLevels = false;
    protected boolean noDuplicates = true;
	public AtomEnvironmentGenerator() {
		super();
		aeDescriptor = createAtomEnvironmentDescriptor();
		aeParams = new Object[2];
	}
	protected AtomEnvironmentDescriptor createAtomEnvironmentDescriptor() {
		return new AtomEnvironmentDescriptor();
	}
	public Object process(Object object) throws AmbitException {
		if (object instanceof IMolecule) {
			try {
				
				int[] aeResult = null;
				IMolecule mol = (IMolecule) ((IMolecule) object).clone();
                //IAtomContainer mol;

                
				
                //if (useHydrogens) { //always, otherwise atom types are not recognised correctly
                	//for some reason H atoms are added as bond references, but not in atom list - bug?
    			    if (hAdder == null) hAdder = new org.openscience.cdk.tools.HydrogenAdder();
    		        hAdder.addExplicitHydrogensToSatisfyValency(mol);
                    
                HueckelAromaticityDetector.detectAromaticity(mol);                    
                //}    
    		        /*
                } else {
                    MFAnalyser mfa =  new MFAnalyser(((IMolecule) object));
                    mol = mfa.removeHydrogensPreserveMultiplyBonded();
                }
                */
                
                AtomEnvironmentList aeList = new AtomEnvironmentList();
                aeList.setNoDuplicates(noDuplicates);
				for (int a = 0; a < mol.getAtomCount(); a++) {
						if ("H".equals(mol.getAtomAt(a).getSymbol())) continue;
						AtomEnvironment atomenv = new AtomEnvironment(maxLevels); 
					    //calculation
					    try {
					    	
					    	atomenv.setTime_elapsed(System.currentTimeMillis());
							aeParams[0] = new Integer(a);
							aeParams[1] = maxLevels;
							aeDescriptor.setParameters(aeParams);
							
						    aeResult = new int[aeDescriptor.getAtomFingerprintSize()];
							
							aeDescriptor.doCalculation(mol,aeResult);
							atomenv.setTime_elapsed(System.currentTimeMillis() - atomenv.getTime_elapsed());
							atomenv.setStatus(1);
							atomenv.setSublevel(maxLevels);
							//result formatting
							atomenv.setAtom_environment(aeResult);
							//atomenv.setAtom_environment(aeDescriptor.atomFingerprintToString(aeResult,':'));
							atomenv.setCentral_atom(aeDescriptor.atomTypeToString(aeResult[1]));
							atomenv.setAtomno(a);
						
							
							aeList.add(atomenv);
							
							
							
							//lower level AE
							if (isCreateSubLevels())
					    		for (int i=1; i < atomenv.getLevels(); i++) {
					    			AtomEnvironment subae = atomenv.getSubAtomEnvironment(i);
					    			subae.setSublevel(i);
					    			if (!atomenv.equals(subae))
					    				aeList.add(subae);
					    		}
				    		
	
							
					    } catch (CDKException x) {
					    	atomenv.setTime_elapsed(0);
					    	atomenv.setStatus(3);
					    	atomenv.setAtom_environment(null);
							atomenv.setCentral_atom("Error");
							aeList.add(atomenv);
					    }
					    
					}	
	
				//System.out.println(aeList);
				((IAtomContainer) object).setProperty(AmbitCONSTANTS.AtomEnvironment,aeList);
			} catch (Exception x) {
				((IAtomContainer) object).getProperties().remove(AmbitCONSTANTS.AtomEnvironment);
				throw new AmbitException("Error generating atom environment\t",x);
			}
		}
		return object;
	}

	public IAmbitResult createResult() {
		return null;
	}

	public IAmbitResult getResult() {
		return null;
	}

	public void setResult(IAmbitResult result) {

	}
	/* (non-Javadoc)
     * @see ambit.processors.IAmbitProcessor#close()
     */
    public void close() {

    }
    public IAmbitEditor getEditor() {

    	return new DefaultProcessorEditor(this);
    }
    
    /* (non-Javadoc)
     * @see ambit.processors.DefaultAmbitProcessor#toString()
     */
    public String toString() {
        return "Generates atom environments";
    }

    public synchronized boolean isUseHydrogens() {
        return useHydrogens;
    }

    public synchronized void setUseHydrogens(boolean useHydrogens) {
        this.useHydrogens = useHydrogens;
    }

	public Integer getMaxLevels() {
		return maxLevels;
	}

	public void setMaxLevels(Integer maxLevels) {
		this.maxLevels = maxLevels;
	}
	public boolean isNoDuplicates() {
		return noDuplicates;
	}
	public void setNoDuplicates(boolean noDuplicates) {
		this.noDuplicates = noDuplicates;
	}
	public boolean isCreateSubLevels() {
		return createSubLevels;
	}
	public void setCreateSubLevels(boolean createSubLevels) {
		this.createSubLevels = createSubLevels;
	}
}
