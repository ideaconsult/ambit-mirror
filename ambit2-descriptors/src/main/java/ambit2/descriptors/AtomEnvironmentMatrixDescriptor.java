/**
 * <b>Filename</b> AtomEnvironmentDescriptor.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-15
 * <b>Project</b> ambit
 */
package ambit2.descriptors;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

import org.openscience.cdk.atomtype.IAtomTypeMatcher;
import org.openscience.cdk.atomtype.SybylAtomTypeMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerArrayResultType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.data.Property;

/**
 * @author Nina Jeliazkova <br>
 *         <b>Modified</b> 2013-4-22
 */
public class AtomEnvironmentMatrixDescriptor implements IMolecularDescriptor {
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

	private int maxLevel = 7;

	private IAtomTypeMatcher atm = null;
	private AtomTypeFactory factory = null;
	// internal data
	protected boolean empty = true;
	protected IAtomType[] atomTypes = null;
	protected TreeMap<String, Integer> map = null;
	protected String[] descriptorNames;

	/**
     * 
     */
	public AtomEnvironmentMatrixDescriptor() {
	}

	/**
	 * 
	 * @see org.openscience.cdk.qsar.IDescriptor#getSpecification()
	 */
	public DescriptorSpecification getSpecification() {
		// TODO Define specification !!!!
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,
						"AtomEnvironmentMatrix"),
				this.getClass().getName(),
				"$Id: AtomTypeMatrixDescriptor.java,v 1.3 2013/04/16 11:41:0 jeliazkova.nina@gmail.com",
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
			descriptorNames = initAtomTypes(afactory, amatcher);
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
			Hashtable<String, Integer> sparseMatrix = doCalculation(container);
			r = new IntegerArrayResult();
			for (int i = 0; i < descriptorNames.length; i++) {
				Integer c = sparseMatrix.get(descriptorNames[i]);
				r.add(c == null ? 0 : c.intValue());
			}
			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), r, getDescriptorNames());
		} catch (Exception e) {
			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), r, new String[] { getClass().getName() },
					e);
		}
	}

	public String[] getDescriptorNames() {
		return descriptorNames;
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

	private String getKeyLevel0(IAtomType atomType) {
		return "L0_" + (atomType == null ? "X" : atomType.getAtomTypeName());
	}

	private String getKeyLevel(IAtomType atomType1, IAtomType atomType2,
			int level) {
		String key1 = atomType1 == null ? "X" : atomType1.getAtomTypeName();
		String key2 = atomType2 == null ? "X" : atomType2.getAtomTypeName();
		return getKeyLevel(key1, key2, level);
	}

	private String getKeyLevel(String key1, String key2, int level) {
		if (key1.compareTo(key2) > 0)
			return String.format("L%d_%s_%s", level, key2, key1);
		else
			return String.format("L%d_%s_%s", level, key1, key2);
	}

	private boolean isHet(IAtomType atomType) {
		String symbol = atomType == null ? null : atomType.getSymbol();
		if (symbol == null)
			return false;
		return !"C".equals(symbol) && !"H".equals(symbol);
	}

	private boolean isHev(IAtomType atomType) {
		String symbol = atomType == null ? null : atomType.getSymbol();
		if (symbol == null)
			return false;
		return !"H".equals(symbol);
	}

	private boolean isHal(IAtomType atomType) {
		String symbol = atomType == null ? null : atomType.getSymbol();
		if (symbol == null)
			return false;
		return "Cl".equals(symbol) || "Br".equals(symbol) || "I".equals(symbol)
				|| "F".equals(symbol);
	}

	private void add(Hashtable<String, Integer> sparseMatrix, String key) {
		Integer value = sparseMatrix.get(key);
		if (value == null)
			sparseMatrix.put(key, 1);
		else
			sparseMatrix.put(key, value + 1);
	}

	/**
	 * 
	 * @param arg0
	 * @param result
	 *            result[i] contains the level at which ith atom is from the
	 *            current atom (one set by setParameters)
	 * @throws CDKException
	 */
	public Hashtable<String, Integer> doCalculation(IAtomContainer atomContainer)
			throws CDKException {
		Hashtable<String, Integer> sparseMatrix = new Hashtable<String, Integer>();
		if ((atm == null) || (factory == null))
			descriptorNames = initAtomTypes(null, null);

		// maxLevel is how many bonds from the atom we will count atom types
		int natoms = atomContainer.getAtomCount();
		// do atom type matching
		atomTypes = factory.getAllAtomTypes();
		/*
		 * System.out.println("AtomTypes"); for (IAtomType at :atomTypes) {
		 * System.out.print(at.getAtomTypeName());System.out.print("\t");}
		 * System.out.println("End AtomTypes");
		 */
		int[] atomIndex = new int[natoms]; // array of atom type integers
		for (int i = 0; i < natoms; i++)
			try {
				IAtomType a = atm.findMatchingAtomType(atomContainer,
						atomContainer.getAtom(i));
				if (a != null) {
					Object mappedType = map.get(a.getAtomTypeName());
					if (mappedType != null) {
						atomIndex[i] = ((Integer) mappedType).intValue();
					} else {
						atomIndex[i] = -1;
					}
				} else { // not found
					atomIndex[i] = -1;
				}
				String key = getKeyLevel0(atomIndex[i] >= 0 ? atomTypes[atomIndex[i]]
						: null);
				add(sparseMatrix, key);
				if ((atomIndex[i] > 0) && atomTypes[atomIndex[i]] != null) {
					if (isHal(atomTypes[atomIndex[i]]))
						add(sparseMatrix, "L0_Hal");
					if (isHet(atomTypes[atomIndex[i]]))
						add(sparseMatrix, "L0_Het");
					if (isHev(atomTypes[atomIndex[i]]))
						add(sparseMatrix, "L0_Hev");
				}
				add(sparseMatrix, "L0_Any");

			} catch (Exception x) {
				// x.printStackTrace();
				throw new CDKException(x.getMessage()
						+ "\ninitConnectionMatrix");
			}
		// compute bond distances between all atoms
		int[][] aMatrix = PathTools.computeFloydAPSP(AdjacencyMatrix
				.getMatrix(atomContainer));
		// assign values to the results arrays for all atoms

		for (int i = 0; i < natoms; i++) {
			IAtomType key1 = atomIndex[i] >= 0 ? atomTypes[atomIndex[i]] : null;
			String[] generic1 = new String[] {
					key1 == null ? "X" : key1.getAtomTypeName(),
					"Any",
					atomIndex[i] >= 0 ? isHal(atomTypes[atomIndex[i]]) ? "Hal"
							: null : null,
					atomIndex[i] >= 0 ? isHet(atomTypes[atomIndex[i]]) ? "Het"
							: null : null,
					atomIndex[i] >= 0 ? isHev(atomTypes[atomIndex[i]]) ? "Hev"
							: null : null };

			for (int j = i; j < natoms; j++) {
				IAtomType key2 = atomIndex[j] >= 0 ? atomTypes[atomIndex[j]]
						: null;
				String[] generic2 = new String[] {
						key2 == null ? "X" : key2.getAtomTypeName(),
						"Any",
						atomIndex[i] >= 0 ? isHal(atomTypes[atomIndex[i]]) ? "Hal"
								: null
								: null,
						atomIndex[i] >= 0 ? isHet(atomTypes[atomIndex[i]]) ? "Het"
								: null
								: null,
						atomIndex[i] >= 0 ? isHev(atomTypes[atomIndex[i]]) ? "Hev"
								: null
								: null };

				if (aMatrix[i][j] > 0) { // j is not atom i and bonds less or
											// equal to maxlevel
					for (int level = 1; level <= (maxLevel + 1); level++) {

						for (String g1 : generic1)
							if (g1 != null) {
								for (String g2 : generic2)
									if (g2 != null) {
										String key = getKeyLevel(g1, g2, level);
										if (level == (maxLevel + 1)) {
											if (aMatrix[i][j] > maxLevel) {
												add(sparseMatrix, key);
											} else {
											}
										} else if (aMatrix[i][j] == level) {
											add(sparseMatrix, key);
										}
									}
							}
					}
				}
			}
		}

		return sparseMatrix;
	}

	protected InputStream getAtomTypeFactoryStream() {
		return this
				.getClass()
				.getClassLoader()
				.getResourceAsStream(
						"org/openscience/cdk/dict/data/sybyl-atom-types.owl");
	}

	private String[] initAtomTypes(AtomTypeFactory factory, IAtomTypeMatcher atm)
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
		// descriptorNames
		List<String> dNames = new ArrayList<String>();
		String key = getKeyLevel0(null);
		if (dNames.indexOf(key) < 0)
			dNames.add(key);
		for (int x1 = 0; x1 < atomTypes.length; x1++) {
			IAtomType key1 = atomTypes[x1];
			key = getKeyLevel0(key1);
			if (dNames.indexOf(key) < 0)
				dNames.add(key);
			for (int x2 = x1; x2 < atomTypes.length; x2++) {
				IAtomType key2 = atomTypes[x2];
				for (int l = 1; l <= 8; l++) {
					key = getKeyLevel(key1, key2, l);
					if (dNames.indexOf(key) < 0)
						dNames.add(key);
				}
			}
			for (int l = 1; l <= 8; l++) {
				key = getKeyLevel(key1, null, l);
				if (dNames.indexOf(key) < 0)
					dNames.add(key);
			}
		}
		// dNames.add(getClass().getName());
		Collections.sort(dNames);
		return dNames.toArray(new String[dNames.size()]);
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
