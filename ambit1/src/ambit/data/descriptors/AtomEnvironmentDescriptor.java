/**
 * <b>Filename</b> AtomEnvironmentDescriptor.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-15
 * <b>Project</b> ambit
 */
package ambit.data.descriptors;

import java.io.InputStream;
import java.util.TreeMap;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.atomtype.HybridizationStateATMatcher;
import org.openscience.cdk.atomtype.IAtomTypeMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.tools.LoggingTool;

import ambit.data.molecule.IntArrayResult;

/**
 * implements atom environment fingerprint following
 * Xing, L., Glen, R.C. Novel Methods for the prediction of logger P, pKa and logger D. J. Chem. Inf. Comput. Sci. 2002, 42, 796-805
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-15
 */
public class AtomEnvironmentDescriptor implements IMolecularDescriptor {
    //parameters
	private int targetPosition = 0;
	private int maxLevel = 3;
	private boolean perceiveCyclicAtoms = false;
	private boolean useExistingConnectionMatrix = true;
	
	private IAtomTypeMatcher atm = null;
	private AtomTypeFactory factory = null;

	//logger
	private LoggingTool logger;
	//internal data
	protected boolean empty = true;
	protected IAtomType[] atomTypes = null;
	protected TreeMap<String,Integer> map = null;
	private IAtomContainer atomContainer = null;
    private int[][] aMatrix = null;
    private int[] atomLevels = null;
    private int[] atomIndex = null;
    /**
     * 
     */
    public AtomEnvironmentDescriptor() {
        super();
    }

    /**
     * 
     * @see org.openscience.cdk.qsar.IDescriptor#getSpecification()
     */
    public DescriptorSpecification getSpecification() {
        // TODO Define specification !!!!
		return new DescriptorSpecification(
				"http://qsar.sourceforge.net/dicts/qsar-descriptors:atomEnvironments",
				this.getClass().getName(),
				"$Id: AtomEnvironmentDescriptor.java,v 1.3 2005/06/15 13:30:0 nina@acad.bg",
				"The Chemistry Development Kit");        
    }

	/**
	 *  Gets the parameterNames attribute of the AtomHybridizationDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[6];
		params[0] = "targetPosition";
		params[1] = "maxLevel";
		params[2] = "perceiveCyclicAtoms";
		params[3] = "useExistingConnectionMatrix";
		params[4] = "AtomTypeFactory";
		params[5] = "AtomTypeMatcher";
		return params;
	}

	/**
	 * 
	 * @see org.openscience.cdk.qsar.IDescriptor#getParameterType(java.lang.String)
	 */
    public Object getParameterType(String arg0) {
        Object[] paramTypes = new Object[4];
        paramTypes[0] = new Integer(1);
        paramTypes[1] = new Integer(2);
        paramTypes[2] = new Boolean(false);
        paramTypes[3] = new Boolean(false);
        //TODO
        paramTypes[4] = "AtomTypeFactory";
        paramTypes[5] = "AtomTypeMatcher";
		return paramTypes;
    }

	/**
	 *  Sets the parameters attribute of the AtomEnvironmentDescriptor object
	 *@param  params - parameters, as below
	 *params[0]  The parameter is the atom position, mandatory
	 *params[1]  MaxLevel, optional , default 3
	 *params[2]  perceiveCyclicAtoms, optional , default false
	 *params[3]  useExistingConnectionMatrix, optional , default false
	 *params[4]  AtomTypeFactory factory, optional, default AtomTypeFactory.getInstance("org/openscience/cdk/config/data/hybridization_atomtypes.xml")
	 *params[5]  AtomTypeMatcher atm, optional , default HybridizationStateATMatcher
	 */
    public void setParameters(Object[] params) throws CDKException {
        Object[] allParams = new Object[6];
        
        AtomTypeFactory afactory = null;
        IAtomTypeMatcher amatcher = null;
        
        for (int i = 0; i < 5; i++)  
        	if (params.length > i) { 
        		allParams[i] = params[i]; 
        		if (allParams[i] == null) 
        		    if (i==0)
        		        throw new CDKException("The first parameter is mandatory!");
        		    else continue;    
	        	if (i < 2) { 
	        		if (!(allParams[i] instanceof Integer)) 
	    			throw new CDKException("The " + (i+1) + " parameter must be of type Integer");
	    		} else        	
	        	if ( i < 4) { 
	        		if (!(allParams[i] instanceof Boolean)) 
	        		throw new CDKException("The " + (i+1) + " parameter must be of type Boolean");
	    		} else
		        	if ( i == 4)  {
		        		if (!(allParams[i] instanceof Boolean))
		        		throw new CDKException("The " + (i+1) + " parameter must be of type Boolean");
		    		} else	  
			        	if ( i == 5) {
			        		if (!(allParams[i] instanceof Boolean)) 
			        		throw new CDKException("The " + (i+1) + " parameter must be of type Boolean");
			    		}		    			
        	} else allParams[i] = null;

        if (params.length > 6)
        	throw new CDKException("AtomEnvironmentDescriptor only expects 5 parameters");
        
        targetPosition = ((Integer) allParams[0]).intValue();
        
		if (allParams[1] != null)
	        maxLevel = ((Integer) allParams[1]).intValue();

		if (allParams[2] != null)
			perceiveCyclicAtoms = ((Boolean) allParams[2]).booleanValue();

		if (allParams[3] != null)
			useExistingConnectionMatrix = ((Boolean) allParams[3]).booleanValue();
		
		if (allParams[4] != null) 
		    afactory = (AtomTypeFactory)allParams[4];

		if (allParams[5] != null) 
		    amatcher = (IAtomTypeMatcher) allParams[5];
				
		
				
		try {
		    
		    initAtomTypes(afactory,amatcher);
		} catch (Exception x) {
		    throw new CDKException(x.getMessage());
		}
    }

    /**
     * 
     * @see org.openscience.cdk.qsar.IDescriptor#getParameters()
     */
    public Object[] getParameters() {
		Object[] params = new Object[6];
		params[0] = new Integer(targetPosition);
		params[1] = new Integer(maxLevel);
		params[2] = new Boolean(perceiveCyclicAtoms);
		params[3] = new Boolean(useExistingConnectionMatrix);		
		params[4] = factory; //TODO should it be  factory.clone() ?
		params[5] = atm; //TODO should it be  atm.clone() ?
		return params;
    }

	/**
	 *  This method calculates the atom environment of an atom.
	 *
	 *@param  container         Parameter is the atom container.
	 *@return                   int[] 
	 *@exception  CDKException  Description of the Exception
	 */
    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
    	int[] result = new int[getAtomFingerprintSize()];
    	doCalculation(container,result);
        return new DescriptorValue(
                getSpecification(), 
                getParameterNames(), 
                getParameters(), 
                new IntArrayResult(result));
    }

     /**
      * to hold counts of all atom types defined by factory for maxlevel
      * and one additional entry for undefined atom types 
      */
    public int getAtomFingerprintSize() {
        return (atomTypes.length +1)*(maxLevel)+2;
    }
    public String atomTypeToString(int index) {
        IAtomType aType = getAtomType(index);
		String atomS;
		if (aType == null) 
		    atomS = "Misc";
		else atomS = aType.getAtomTypeName();
		return atomS;
    }

    /**
     * 
     * @param arg0
     * @param result   result[i] contains the level at which ith atom is from the current atom (one set by setParameters)
     * @throws CDKException
     */
    public void doCalculation(IAtomContainer arg0, int[] result) throws CDKException {
        if ((atm == null) || (factory == null)) initAtomTypes(null,null);
        initConnectionMatrix(arg0);
        for (int i=0; i < atomLevels.length; i++) atomLevels[i] = -1;
        getAtomEnvironment(atomLevels,targetPosition,0);

        int L = (atomTypes.length +1) ;
        
        for (int j = 0; j < result.length; j++) result[j] = 0;
        //TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
        for (int j=0; j< atomLevels.length; j++)
        	if (atomLevels[j] == 0)
        		result[1] = atomIndex[j];
        	else if (atomLevels[j]>0)
        		if (atomIndex[j] >= 0) //atomtype defined in factory
	                result[L*(atomLevels[j]-1)+atomIndex[j]+2]++;
        		else if (atomIndex[j] == -1) //-1, unknown  type
        			result[L*(atomLevels[j]-1)+(L-1)+2]++;
        		//skip if == -2
	    //checksum for easy comparison        
	    for (int j = 1; j < result.length; j++) result[0] += result[j];
    }
    
    protected void getAtomEnvironment(int[] atomLevels, int index, int level) {
        atomLevels[index] = level;        
        for (int i = 0; i < aMatrix.length; i++) 
            if ((atomLevels[i] == -1) && (level < maxLevel)) {
                if (aMatrix[index][i] == 1) 
                	getAtomEnvironment(atomLevels,i,level+1);
            }    
        
    }    
	public static int[][] getMatrix(IAtomContainer container) {
		IElectronContainer electronContainer = null;
		int indexAtom1;
		int indexAtom2;
		
		int heavyatoms = 0;
		for (int a=0; a < container.getAtomCount();a++) 
			if (! ("H".equals(container.getAtomAt(a).getSymbol()))) 
				heavyatoms++;
		
		int[][] conMat = new int[container.getAtomCount()][container.getAtomCount()];
		for (int f = 0; f < container.getElectronContainerCount(); f++){
            electronContainer = container.getElectronContainerAt(f);
			if (electronContainer instanceof IBond)
			{
				IBond bond = (IBond) electronContainer;
				IAtom atom1 = bond.getAtomAt(0);
				IAtom atom2 = bond.getAtomAt(1);

				if ("H".equals(atom1.getSymbol()) || "H".equals(atom2.getSymbol())) 
					continue;
				
				indexAtom1 = container.getAtomNumber(atom1);
				if (indexAtom1 < 0) {
					System.out.println(atom1.getSymbol() + " not found");
					continue;
				}
				
				indexAtom2 = container.getAtomNumber(atom2);
				if (indexAtom1 < 0) {
					System.out.println(atom1.getSymbol() + " not found");
					continue;
				}
				
				conMat[indexAtom1][indexAtom2] = 1;
				conMat[indexAtom2][indexAtom1] = 1;
			}
		}
		return conMat;
	}
    private void initConnectionMatrix(IAtomContainer atomContainer) throws CDKException {
        if (    (!useExistingConnectionMatrix) || 
        		(aMatrix == null) || (this.atomContainer == null) || 
                (this.atomContainer != atomContainer)) {
            this.atomContainer = atomContainer;
            //aMatrix = AdjacencyMatrix.getMatrix(this.atomContainer);
            aMatrix = getMatrix(this.atomContainer);
            int natoms = aMatrix.length;
            atomLevels = new int[natoms];
            atomIndex = new int[natoms];
            for (int i = 0; i < natoms; i++) {
                try {
                    IAtomType a = atm.findMatchingAtomType(atomContainer,atomContainer.getAtomAt(i));
                    if ( a != null) {
                    	if (a.getAtomTypeName().startsWith("H")) atomIndex[i] = -2;
                    	else {
	                        Object mappedType = map.get(a.getAtomTypeName());
	                        if (mappedType != null)	
	                            atomIndex[i] = ((Integer) mappedType).intValue();
	                        else {
	                            //System.out.println(a.getAtomTypeName() + " not found in " + map);
	                            atomIndex[i] = -1;
	                        }    
                    	}
                    } else //atomtype not found 
                    	atomIndex[i] = -1;
                    
                } catch (Exception x) {
                    x.printStackTrace();
                    throw new CDKException(x.getMessage() + "\ninitConnectionMatrix");
                }                
            }
            
        }
        //else leave the same matrix
    }
    protected InputStream getAtomTypeFactoryStream() {
    	//return this.getClass().getClassLoader().getResourceAsStream("ambit/data/descriptors/test_atomtypes.xml");
    	return this.getClass().getClassLoader().getResourceAsStream("ambit/data/descriptors/hybridization_atomtypes.xml");
    }
    private void initAtomTypes(AtomTypeFactory factory,IAtomTypeMatcher atm) throws CDKException {
        //the idea is not to create objects if they already exist...
        if ((atm == null) || (factory == null)) {
            if ( (this.atm == null) || (!(this.atm instanceof HybridizationStateATMatcher))) {
                this.atm = new HybridizationStateATMatcher();
                try {
                    //InputStream ins = this.getClass().getClassLoader().getResourceAsStream("ambit/data/descriptors/hybridization_atomtypes.xml");
                	InputStream ins = getAtomTypeFactoryStream();
                    this.factory = 
                        AtomTypeFactory.getInstance(ins,"xml",DefaultChemObjectBuilder.getInstance());
                } catch (Exception x) {
                    throw new CDKException(x.getMessage());
                }
                empty = true;
            } else empty = false;
        } else {
            this.atm = atm;
            this.factory = factory;
            empty = true;
        }
        if (empty ) {
	        atomTypes = this.factory.getAllAtomTypes();
	        if (map ==null) map = new TreeMap<String,Integer>();
	        else map.clear();
	        for (int i = 0; i < atomTypes.length; i++) { 
	            map.put(atomTypes[i].getAtomTypeName(),new Integer(i));
	        }	
	        //System.out.println(map);
        }
        
    }
    public IAtomType getAtomType(int index) {
        if (atomTypes == null) return null;
        else
	        if (index == -1) return null;
	        else return atomTypes[index];
    }
}

