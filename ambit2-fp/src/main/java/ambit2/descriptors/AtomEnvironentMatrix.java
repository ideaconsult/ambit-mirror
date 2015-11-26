package ambit2.descriptors;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.openscience.cdk.atomtype.IAtomTypeMatcher;
import org.openscience.cdk.atomtype.SybylAtomTypeMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.fingerprint.ICountFingerprint;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.core.data.HashIntDescriptorResult;

public class AtomEnvironentMatrix implements IFingerprinter {
	protected String factoryResource = "org/openscience/cdk/dict/data/sybyl-atom-types.owl";

	// protected String factoryResource =
	// "org/openscience/cdk/dict/data/cdk-atom-types.owl";

	// parameters
	protected boolean addGenericTypes = false;

	public boolean isAddGenericTypes() {
		return addGenericTypes;
	}

	public void setAddGenericTypes(boolean addGenericTypes) {
		this.addGenericTypes = addGenericTypes;
	}
	
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

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public AtomEnvironentMatrix() throws Exception {
		this(null, null,7);
	}

	public AtomEnvironentMatrix(IAtomTypeMatcher matcher, String factoryResource,int maxLevels)
			throws Exception {
		setMaxLevel(maxLevels);
		if (factoryResource != null) {
			if (matcher == null)
				throw new Exception(
						"AtomTypeFactory specified, but not AtomTypeMatcher");
			this.factoryResource = factoryResource;
			try {
				InputStream ins = getAtomTypeFactoryStream();
				AtomTypeFactory afactory = AtomTypeFactory.getInstance(ins,
						"owl", SilentChemObjectBuilder.getInstance());
				descriptorNames = initAtomTypes(afactory, matcher);
				empty = false;
			} catch (Exception x) {
				throw x;
			}
		}
		
	}

	// internal data
	protected boolean empty = true;

	protected TreeMap<String, Integer> map = null;
	protected String[] descriptorNames;

	public String[] getDescriptorNames() {
		return descriptorNames;
	}

	public void setDescriptorNames(String[] descriptorNames) {
		this.descriptorNames = descriptorNames;
	}

	private IAtomTypeMatcher atm = null;

	public IAtomTypeMatcher getAtm() {
		return atm;
	}

	public void setAtm(IAtomTypeMatcher atm) {
		this.atm = atm;
	}

	private AtomTypeFactory factory = null;

	public AtomTypeFactory getFactory() {
		return factory;
	}

	public void setFactory(AtomTypeFactory factory) {
		this.factory = factory;
	}

	protected IAtomType[] atomTypes = null;

	/**
	 * 
	 * @param arg0
	 * @param result
	 *            result[i] contains the level at which ith atom is from the
	 *            current atom (one set by setParameters)
	 * @throws CDKException
	 */
	public HashIntDescriptorResult doCalculation(IAtomContainer atomContainer)
			throws CDKException {

		HashIntDescriptorResult sparseMatrix = null;
		try {
			sparseMatrix = new HashIntDescriptorResult(
					new TreeMap<String, Integer>());
			sparseMatrix.setMaxLevels(getMaxLevel());
			
			sparseMatrix.setFactory(factoryResource.substring(factoryResource.lastIndexOf("/")+1));
		} catch (Exception x) {
			throw new CDKException(x.getMessage());
		}
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
				add(sparseMatrix.getResults(), key);
				if (addGenericTypes) {
					if ((atomIndex[i] > 0) && atomTypes[atomIndex[i]] != null) {
						if (isHal(atomTypes[atomIndex[i]]))
							add(sparseMatrix.getResults(), "L0_Hal");
						if (isHet(atomTypes[atomIndex[i]]))
							add(sparseMatrix.getResults(), "L0_Het");
						if (isHev(atomTypes[atomIndex[i]]))
							add(sparseMatrix.getResults(), "L0_Hev");
					}
					add(sparseMatrix.getResults(), "L0_Any");
				}

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

			for (int j = i; j < natoms; j++) {
				IAtomType key2 = atomIndex[j] >= 0 ? atomTypes[atomIndex[j]]
						: null;
				if (aMatrix[i][j] > 0 && (aMatrix[i][j]!=999999999) && (aMatrix[i][j]<=maxLevel)) { // j is not atom i and bonds less or
											// equal to maxlevel
					String key = getKeyLevel(key1, key2, aMatrix[i][j] );
					add(sparseMatrix.getResults(), key);
					
				}
			}
		}

		return sparseMatrix;
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

	private String getKeyLevel0(IAtomType atomType) {
		return  (atomType == null ? "X" : atomType.getAtomTypeName()) + "_A";
	}

	private String getKeyLevel(IAtomType atomType1, IAtomType atomType2,
			int level) {
		String key1 = atomType1 == null ? "X" : atomType1.getAtomTypeName();
		String key2 = atomType2 == null ? "X" : atomType2.getAtomTypeName();
		return getKeyLevel(key1, key2, level);
	}

	private String getKeyLevel(String key1, String key2, int level) {
		String l = Character.toString((char)(level+65));
		if (key1.compareTo(key2) > 0)
			return String.format("%s_%s_%s", key2, key1,l);
		else
			return String.format("%s_%s_%s", key1, key2,l);
	}

	private void add(Map<String, Integer> sparseMatrix, String key) {
		Integer value = sparseMatrix.get(key);
		if (value == null)
			sparseMatrix.put(key, 1);
		else
			sparseMatrix.put(key, value + 1);
	}

	protected String[] initAtomTypes(AtomTypeFactory factory,
			IAtomTypeMatcher atm) throws CDKException {
		// the idea is not to create objects if they already exist...
		if ((atm == null) || (factory == null)) {
			if (this.atm == null) {
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
		Set<String> dNames = new TreeSet<String>();
		String key = getKeyLevel0(null);
		if (!dNames.contains(key))
			dNames.add(key);
		for (int x1 = 0; x1 < atomTypes.length; x1++) {
			IAtomType key1 = atomTypes[x1];
			key = getKeyLevel0(key1);
			if (!dNames.contains(key))
				dNames.add(key);
			for (int x2 = x1; x2 < atomTypes.length; x2++) {
				IAtomType key2 = atomTypes[x2];
				for (int l = 1; l <= 8; l++) {
					key = getKeyLevel(key1, key2, l);
					if (!dNames.contains(key))
						dNames.add(key);
				}
			}
			for (int l = 1; l <= 8; l++) {
				key = getKeyLevel(key1, null, l);
				if (!dNames.contains(key))
					dNames.add(key);
			}
		}
		// dNames.add(getClass().getName());
		// Collections.sort(dNames);
		return dNames.toArray(new String[dNames.size()]);
	}

	protected InputStream getAtomTypeFactoryStream() {
		return this.getClass().getClassLoader()
				.getResourceAsStream(factoryResource);
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

	public IAtomType getAtomType(int index) {
		if (atomTypes == null)
			return null;
		else if (index == -1)
			return null;
		else
			return atomTypes[index];
	}

	@Override
	public IBitFingerprint getBitFingerprint(IAtomContainer container)
			throws CDKException {
		return doCalculation(container).getBitFingerprint(container);
	}

	@Override
	public ICountFingerprint getCountFingerprint(IAtomContainer container)
			throws CDKException {
		return doCalculation(container).getCountFingerprint(container);
	}

	@Override
	public Map<String, Integer> getRawFingerprint(IAtomContainer container)
			throws CDKException {
		return doCalculation(container).getRawFingerprint(container);
	}

	@Override
	public int getSize() {
		return descriptorNames == null ? 0 : descriptorNames.length;
	}
}
