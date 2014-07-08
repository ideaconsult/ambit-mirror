package ambit2.descriptors.processors;

import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.Property;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.processors.structure.AbstractPropertyGenerator;
import ambit2.descriptors.AtomEnvironment;
import ambit2.descriptors.AtomEnvironmentDescriptor;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;

/**
 * A processor to generate atom environments. Uses {@link ambit2.data.descriptors.AtomEnvironmentDescriptor} for actual generation.
 * The result is contained in {@link ambit2.data.descriptors.AtomEnvironmentList} and is assigned as a molecule property 
 * with a name {@link AmbitCONSTANTS#AtomEnvironment}. <br>
 * Used by {@link ambit2.database.writers.AtomEnvironmentWriter}
 * @author Nina Jeliazkova nina@acad.bg
 * @author Patrik Rydberg
 * <b>Modified</b> Jan 3, 2012
 */
public class AtomEnvironmentGenerator extends AbstractPropertyGenerator<AtomEnvironmentList>   {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7279981279903286909L;
	protected CDKHydrogenAdder hAdder = null;
	protected AtomEnvironmentDescriptor aeDescriptor = null;
	protected Object[] aeParams = null;
    protected boolean useHydrogens = false;
    protected Integer maxLevels = new Integer(3);
    protected boolean createSubLevels = false;
    protected boolean noDuplicates = true;
    protected FPTable fpmode = FPTable.atomenvironments;
    
	public AtomEnvironmentGenerator() {
		super();
		aeDescriptor = createAtomEnvironmentDescriptor();
		aeParams = new Object[1];
	}
	protected AtomEnvironmentDescriptor createAtomEnvironmentDescriptor() {
		return new AtomEnvironmentDescriptor();
	}
	@Override
	public AtomEnvironmentList generateProperty(IAtomContainer atomContainer)
			throws AmbitException {

			try {
				
				IAtomContainer mol = (IAtomContainer) ((IAtomContainer) atomContainer).clone();
                
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);				
                //if (useHydrogens) { //always, otherwise atom types are not recognised correctly
                	//for some reason H atoms are added as bond references, but not in atom list - bug?
				try {
	    			if (hAdder == null) hAdder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
	    		    hAdder.addImplicitHydrogens(mol);
				} catch (Exception x) {
					
				}
    			CDKHueckelAromaticityDetector.detectAromaticity(mol);                    
                //}    
                
                AtomEnvironmentList aeList = new AtomEnvironmentList();
                aeList.setNoDuplicates(noDuplicates);
                
                aeParams[AtomEnvironmentDescriptor._param.maxLevel.ordinal()] = maxLevels;
                aeDescriptor.setParameters(aeParams);
                int[][] aeResult = null;
                try {
                	aeResult = aeDescriptor.doCalculation(mol);
			    } catch (CDKException x) {
			    	throw new AmbitException(x);
			    }		
				for (int a = 0; a < aeResult.length; a++) {
						AtomEnvironment atomenv = new AtomEnvironment(maxLevels); 
		
					    atomenv.setTime_elapsed(System.currentTimeMillis());
						aeParams[0] = maxLevels;
							
						atomenv.setTime_elapsed(System.currentTimeMillis() - atomenv.getTime_elapsed());
						atomenv.setStatus(1);
						atomenv.setSublevel(maxLevels);
						//result formatting
						atomenv.setAtom_environment(aeResult[a]);
						//atomenv.setAtom_environment(aeDescriptor.atomFingerprintToString(aeResult,':'));
						atomenv.setCentral_atom(aeDescriptor.atomTypeToString(aeResult[a][1]));
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
					}	
	
				return aeList;
			} catch (Exception x) {
				logger.log(Level.WARNING,x.getMessage(),x);
			}
		return null;
	}

    /* (non-Javadoc)
     * @see ambit2.processors.DefaultAmbitProcessor#toString()
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

    @Override
    public Property getProperty() {
    	return Property.getInstance(fpmode.getProperty(),fpmode.getProperty());
    }
    @Override
    protected Property getTimeProperty() {
    	return null;
    }	
}
