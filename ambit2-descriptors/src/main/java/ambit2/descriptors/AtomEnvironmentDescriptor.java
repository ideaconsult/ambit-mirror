/**
 * <b>Filename</b> AtomEnvironmentDescriptor.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-15
 * <b>Project</b> ambit
 */
package ambit2.descriptors;

import java.io.InputStream;
import java.util.TreeMap;

import org.openscience.cdk.atomtype.IAtomTypeMatcher;
import org.openscience.cdk.atomtype.SybylAtomTypeMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerArrayResultType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.data.Property;
import ambit2.core.config.AmbitCONSTANTS;

/**
 * implements atom environment fingerprint following Xing, L., Glen, R.C. Novel
 * Methods for the prediction of logger P, pKa and log D. J. Chem. Inf. Comput.
 * Sci. 2002, 42, 796-805
 * 
 * @author Nina Jeliazkova <br>
 *         <b>Modified</b> 2005-6-15
 */
public class AtomEnvironmentDescriptor implements IMolecularDescriptor {
	// parameters
	public enum _param {
		maxLevel {
			@Override
			public Class getType() {
				return Integer.class;
			}
		},
		atomTypeFactory {
			@Override
			public Class getType() {
				return AtomTypeFactory.class;
			}
		},
		atomTypeMatcher {
			@Override
			public Class getType() {
				return IAtomTypeMatcher.class;
			}
		};
		public abstract Class getType();
	}

	private int maxLevel = 3;

	private IAtomTypeMatcher atm = null;
	private AtomTypeFactory factory = null;
	// internal data
	protected boolean empty = true;
	protected IAtomType[] atomTypes = null;
	protected TreeMap<String, Integer> map = null;

	/**
     * 
     */
	public AtomEnvironmentDescriptor() {
	}

	@Override
	public void initialise(IChemObjectBuilder arg0) {

	}

	/**
	 * 
	 * @see org.openscience.cdk.qsar.IDescriptor#getSpecification()
	 */
	public DescriptorSpecification getSpecification() {
		// TODO Define specification !!!!
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,
						"AtomEnvironment"),
				this.getClass().getName(),
				"$Id: AtomEnvironmentDescriptor.java,v 1.3 2005/06/15 13:30:0 nina@acad.bg",
				"http://ambit.sourceforge.net");
	}

	public IDescriptorResult getDescriptorResultType() {
		return new IntegerArrayResultType(1);
	}

	/**
	 * Gets the parameterNames attribute of the AtomHybridizationDescriptor
	 * object
	 * 
	 * @return The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[_param.values().length];
		for (int i = 0; i < params.length; i++)
			params[i] = _param.values()[i].toString();
		return params;
	}

	/**
	 * 
	 * @see org.openscience.cdk.qsar.IDescriptor#getParameterType(java.lang.String)
	 */
	public Object getParameterType(String arg0) {
		Object[] paramTypes = new Object[3];
		paramTypes[0] = new Integer(1);
		// TODO
		paramTypes[1] = "AtomTypeFactory";
		paramTypes[2] = "AtomTypeMatcher";
		return paramTypes;
	}

	/**
	 * Sets the parameters attribute of the AtomEnvironmentDescriptor object
	 * 
	 * @param params
	 *            - parameters, as below params[0] MaxLevel, optional , default
	 *            3 params[1] perceiveCyclicAtoms, optional , default false
	 *            params[2] AtomTypeFactory factory, optional, default
	 *            AtomTypeFactory.getInstance(
	 *            "org/openscience/cdk/dict/data/sybyl-atom-types.owl")
	 *            params[3] AtomTypeMatcher atm, optional , default
	 *            HybridizationStateATMatcher
	 */
	public void setParameters(Object[] allParams) throws CDKException {

		AtomTypeFactory afactory = null;
		IAtomTypeMatcher amatcher = null;

		for (int i = 0; i < allParams.length; i++) {
			switch (i) {
			case 0: {
				if (!(allParams[i] instanceof Integer))
					throw new CDKException(String.format(
							"The %d parameter must be of type Integer", i + 1));
				maxLevel = ((Integer) allParams[i]).intValue();
				break;
			}
			case 1: {
				if (!(allParams[i] instanceof Boolean))
					throw new CDKException(String.format(
							"The %d parameter must be of type AtomTypeFactory",
							i + 1));
				afactory = (AtomTypeFactory) allParams[i];
				break;
			}
			case 2: {
				if (!(allParams[i] instanceof Boolean))
					throw new CDKException(
							String.format(
									"The %d parameter must be of type IAtomTypeMatcher",
									i + 1));
				amatcher = (IAtomTypeMatcher) allParams[i];
				break;
			}
			default: {
				throw new CDKException(String.format(
						"%s only expects %d parameters", getClass().getName(),
						allParams.length));
			}
			}
		}
		;

		try {
			initAtomTypes(afactory, amatcher);
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
		params[0] = new Integer(maxLevel);
		params[1] = factory; // TODO should it be factory.clone() ?
		params[2] = atm; // TODO should it be atm.clone() ?
		return params;
	}

	/**
	 * This method calculates the atom environment of an atom.
	 * 
	 * @param container
	 *            Parameter is the atom container.
	 * @return int[]
	 * @exception CDKException
	 *                Description of the Exception
	 */
	public DescriptorValue calculate(IAtomContainer container) {
		IntegerArrayResult r = null;
		try {
			int[][] result = doCalculation(container);
			r = new IntegerArrayResult();
			for (int[] a : result)
				for (int b : a)
					r.add(b);

			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), r, getDescriptorNames());
		} catch (Exception e) {
			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), r,
					new String[] { AmbitCONSTANTS.AtomEnvironment }, e);
		}
	}

	public String[] getDescriptorNames() {
		return new String[] { AmbitCONSTANTS.AtomEnvironment };
	}

	/**
	 * to hold counts of all atom types defined by factory for maxlevel and one
	 * additional entry for undefined atom types
	 * 
	 * @return
	 */
	public int getAtomFingerprintSize() {
		return (atomTypes.length + 1) * (maxLevel) + 2;

	}

	public String atomTypeToString(int index) {
		IAtomType aType = getAtomType(index);
		String atomS = null;
		if (aType == null)
			atomS = "Misc";
		else
			atomS = aType.getAtomTypeName();
		return atomS;
	}

	/**
	 * 
	 * @param arg0
	 * @param result
	 *            result[i] contains the level at which ith atom is from the
	 *            current atom (one set by setParameters)
	 * @throws CDKException
	 */
	public int[][] doCalculation(IAtomContainer atomContainer)
			throws CDKException {
		int[][] result = new int[atomContainer.getAtomCount()][getAtomFingerprintSize()];

		if ((atm == null) || (factory == null))
			initAtomTypes(null, null);
		// maxLevel is how many bonds from the atom we will count atom types
		int natoms = atomContainer.getAtomCount();
		// do atom type matching
		atomTypes = factory.getAllAtomTypes();
		int[] atomIndex = new int[natoms]; // array of atom type integers
		for (int i = 0; i < natoms; i++)
			try {
				IAtomType a = atm.findMatchingAtomType(atomContainer,
						atomContainer.getAtom(i));
				if (a != null) {
					Object mappedType = map.get(a.getAtomTypeName());
					if (mappedType != null)
						atomIndex[i] = ((Integer) mappedType).intValue();
					else {
						atomIndex[i] = -1;
					}
				} else
					// atom type not found
					atomIndex[i] = -1;
			} catch (Exception x) {
				throw new CDKException(x.getMessage()
						+ "\ninitConnectionMatrix");
			}
		// compute bond distances between all atoms
		int[][] aMatrix = PathTools.computeFloydAPSP(AdjacencyMatrix
				.getMatrix(atomContainer));
		// assign values to the results arrays for all atoms
		int L = (atomTypes.length + 1);

		for (int i = 0; i < natoms; i++) {
			// for every atom, iterate through its connections to all other
			// atoms
			for (int j = 0; j < natoms; j++) {
				if (aMatrix[i][j] == 0)
					result[i][1] = atomIndex[j]; // atom j is atom i
				else if (aMatrix[i][j] > 0 && aMatrix[i][j] <= maxLevel) {
					// j is not atom i and bonds less or equal to maxlevel
					if (atomIndex[j] >= 0) // atom type defined in factory
						result[i][L * (aMatrix[i][j] - 1) + atomIndex[j] + 2]++;
					else if (atomIndex[j] == -1) // -1, unknown type
						result[i][L * (aMatrix[i][j] - 1) + (L - 1) + 2]++;
				}
			}
			// checksum for easy comparison
			for (int j = 1; j < result[i].length; j++)
				result[i][0] += result[i][j];
		}
		return result;
	}

	protected InputStream getAtomTypeFactoryStream() {
		return this
				.getClass()
				.getClassLoader()
				.getResourceAsStream(
						"org/openscience/cdk/dict/data/sybyl-atom-types.owl");
	}

	private void initAtomTypes(AtomTypeFactory factory, IAtomTypeMatcher atm)
			throws CDKException {
		// the idea is not to create objects if they already exist...
		if ((atm == null) || (factory == null)) {
			if ((this.atm == null)
					|| (!(this.atm instanceof SybylAtomTypeMatcher))) {
				this.atm = SybylAtomTypeMatcher
						.getInstance(SilentChemObjectBuilder.getInstance());

				try {
					InputStream ins = getAtomTypeFactoryStream();
					this.factory = AtomTypeFactory.getInstance(ins, "owl",
							SilentChemObjectBuilder.getInstance());

				} catch (Exception x) {
					throw new CDKException(x.getMessage());
				}
				empty = true;
			} else
				empty = false;
		} else {
			this.atm = atm;
			this.factory = factory;
			empty = true;
		}
		if (empty) {
			atomTypes = this.factory.getAllAtomTypes();
			if (map == null)
				map = new TreeMap<String, Integer>();
			else
				map.clear();
			for (int i = 0; i < atomTypes.length; i++) {
				map.put(atomTypes[i].getAtomTypeName(), new Integer(i));
			}

		}

	}

	public IAtomType getAtomType(int index) {
		if (atomTypes == null)
			return null;
		else if (index == -1)
			return null;
		else
			return atomTypes[index];
	}
}
