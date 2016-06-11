/**
 * Created on 2005-2-24
 *
 */
package ambit2.core.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Point2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Kekulization;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import com.google.common.collect.Maps;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.io.IteratingChemObjectReaderWrapper;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.core.smiles.SmilesParserWrapper;

/**
 * Various structure processing. TODO refactor.
 * 
 * @author Nina Jeliazkova <br>
 *         <b>Modified</b> 2005-4-7
 */
public class MoleculeTools {
	protected static Logger logger = Logger.getLogger(MoleculeTools.class
			.getName());
	public final static int _FPLength = 1024;
	protected static CDKHydrogenAdder adder = null;
	protected static Fingerprinter fingerprinter = null;
	public static final String[] substanceType = { "organic", "inorganic",
			"mixture/unknown", "organometallic" };
	public static final int substTypeOrganic = 1;
	public static final int substTypeInorganic = 2;
	public static final int substTypeMixture = 3;
	public static final int substTypeMetallic = 4;
	protected static StructureTypeProcessor sp = new StructureTypeProcessor();
	protected static String delim;
	protected static String bracketLeft = "[";
	protected static String bracketRight = "]";

	/**
	 * 
	 */
	public MoleculeTools() {
		super();
	}

	public static IAtomContainer getMolecule(String smiles)
			throws InvalidSmilesException {

		SmilesParser parser = new SmilesParser(
				SilentChemObjectBuilder.getInstance());
		return parser.parseSmiles(smiles);
	}

	public static BitSet getFingerPrint(String smiles, int fpLength)
			throws Exception {
		SmilesParserWrapper sp = SmilesParserWrapper.getInstance();
		IAtomContainer mol = sp.parseSmiles(smiles);
		if (fingerprinter == null)
			fingerprinter = new Fingerprinter(fpLength);
		return fingerprinter.getBitFingerprint(mol).asBitSet();

	}

	public static BitSet getFingerPrint(String smiles) throws Exception {
		return getFingerPrint(smiles, _FPLength);
	}

	public static long bitset2Long(BitSet bs) {
		long h = 0;
		for (int i = 0; i < 64; i++) {
			h = h << 1;
			if (bs.get(i)) {
				h |= 1;
			}
		}
		return h;
	}

	public static void bitset2bigint16(BitSet bs, int size, BigInteger[] h16) {
		if (bs == null)
			for (int i = 0; i < h16.length; i++)
				h16[i] = BigInteger.ZERO;
		else {
			int L = h16.length;
			for (int j = 0; j < L; j++) {
				StringBuilder b = new StringBuilder();
				for (int i = (size - 1); i >= 0; i--)
					b.append(bs.get(j * size + i) ? "1" : "0");

				h16[j] = new BigInteger(b.toString(), 2);
			}
		}
	}

	/*
	 * protected static byte[] toByteArray(BitSet bits) { byte[] bytes = new
	 * byte[bits.length()/8+1]; for (int i=0; i<bits.length(); i++) { if
	 * (bits.get(i)) { bytes[bytes.length-i/8-1] |= 1<<(i%8); } } return bytes;
	 * }
	 */

	public static boolean analyzeSubstance(IAtomContainer molecule)
			throws IOException {
		if ((molecule == null) || (molecule.getAtomCount() == 0))
			return false;
		int noH = 0;
		// MFAnalyser mfa = new MFAnalyser(molecule);
		IMolecularFormula formula = MolecularFormulaManipulator
				.getMolecularFormula(molecule);

		IElement h = Isotopes.getInstance().getElement("H");
		if (MolecularFormulaManipulator.getElementCount(formula, h) == 0) {

			noH = 1;
			// TODO to insert H if necessary
			// TODO this uses new SaturationChecker() which relies on
			// cdk/data/config
			if (adder == null)
				adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder
						.getInstance());
			try {
				adder.addImplicitHydrogens(molecule);
				int atomCount = molecule.getAtomCount();
				formula = MolecularFormulaManipulator
						.getMolecularFormula(molecule);
			} catch (CDKException x) {
				logger.log(Level.SEVERE, x.getMessage(), x);
				formula = null;
			}

		}
		if (formula != null) {
			molecule.setProperty(AmbitCONSTANTS.FORMULA,
					MolecularFormulaManipulator.getString(formula));

			double mass = MolecularFormulaManipulator
					.getTotalMassNumber(formula);
			molecule.setProperty(AmbitCONSTANTS.MOLWEIGHT, new Double(mass));
			try {
				molecule.setProperty(AmbitCONSTANTS.STRUCTURETYPE,
						sp.process(molecule));
			} catch (Exception x) {
			}
			;
			molecule.setProperty(AmbitCONSTANTS.SUBSTANCETYPE,
					getSubstanceType(MolecularFormulaManipulator
							.getString(formula)));
		}
		return true;
	}

	protected static int getSubstanceType(String formula) {
		// TODO add mixture/organometallic recognition
		if (formula.equals(""))
			return substTypeOrganic;
		else if (formula.startsWith("C"))
			return substTypeOrganic;
		else
			return substTypeInorganic;
	}

	protected static boolean hasBondOrderUnsetAromatic(IAtomContainer mol) {
		boolean result = false;
		for (IBond bond : mol.bonds())
			if (bond.getOrder() == IBond.Order.UNSET) {
				for (IAtom atom : bond.atoms())
					if (atom.getFlag(CDKConstants.ISAROMATIC)) {
						bond.setFlag(CDKConstants.ISAROMATIC, true);
						bond.setOrder(IBond.Order.SINGLE);
						result = true;
						break;
					}
			}
		return result;
	}

	public static boolean repairBondOrder4(IAtomContainer mol) throws Exception {
		if (hasBondOrderUnsetAromatic(mol)) {
			// need hydrogen counts, can add via other method
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHydrogenAdder.getInstance(mol.getBuilder())
					.addImplicitHydrogens(mol);

			// percieveAtomTypesAndConfigureAtoms wipes arom flags on atoms
			// (bug)
			for (IBond bond : mol.bonds()) {
				if (bond.getFlag(CDKConstants.ISAROMATIC)) {
					for (IAtom atom : bond.atoms())
						atom.setFlag(CDKConstants.ISAROMATIC, true);
				}
			}
			Kekulization.kekulize(mol);
			return true;
		} else
			return false;
	}

	public static IAtomContainer readMolfile(Reader reader) throws Exception {
		MDLV2000Reader r = null;
		try {
			r = new MDLV2000Reader(reader);
			IAtomContainer mol = r.read(new AtomContainer());
			return mol;
		} finally {
			try {
				r.close();
			} catch (Exception x) {
			}
		}

	}

	public static IAtomContainer readMolfile(String molfile) throws Exception {
		MDLV2000Reader r = null;
		try {
			StringReader reader = new StringReader(molfile);
			r = new MDLV2000Reader(reader);

			r.addSetting(new BooleanIOSetting(
					"AddStereoElements",
					IOSetting.Importance.HIGH,
					"Assign stereo configurations to stereocenters utilising 2D/3D coordinates.",
					"false"));
			/*
			 * Properties customSettings = new Properties();
			 * customSettings.setProperty("AddStereoElements", "false");
			 * PropertiesListener listener = new
			 * PropertiesListener(customSettings);
			 * r.addChemObjectIOListener(listener);
			 */
			IAtomContainer mol = r.read(new AtomContainer());
			reader.close();
			return mol;
		} finally {
			try {
				r.close();
			} catch (Exception x) {
			}
		}
	}

	public static IAtomContainer readPDBfile(String molfile) throws Exception {
		PDBReader pdbreader = new PDBReader(new StringReader(molfile));
		IteratingChemObjectReaderWrapper reader = new IteratingChemObjectReaderWrapper(
				pdbreader);
		try {
			while (reader.hasNext()) {
				Object o = reader.next();
				if (o instanceof IAtomContainer) {
					return ((IAtomContainer) o);
				}
			}
		} catch (Exception x) {
			throw x;
		} finally {
			try {
				reader.close();
			} catch (Exception x) {
			}
		}
		return null;
	}

	public static IAtomContainer readCMLMolecule(String cml) throws Exception {
		IAtomContainer mol = null;
		// StringReader strReader = null;
		try {
			// strReader= new StringReader(cml);
			ByteArrayInputStream in = new ByteArrayInputStream(cml.getBytes());
			mol = readCMLMolecule(in);
			in.close();
			return mol;
		} catch (Exception e) {
			return mol;
		}

	}

	public static IAtomContainer readCMLMolecule(InputStream in)
			throws Exception {
		IAtomContainer mol = null;

		CMLReader reader = new CMLReader(in);
		IChemFile obj = null;

		obj = (IChemFile) reader.read(newChemFile(SilentChemObjectBuilder
				.getInstance()));
		int n = obj.getChemSequenceCount();
		if (n > 1)
			logger.finest("> 1 sequence in a record");
		for (int j = 0; j < n; j++) {
			IChemSequence seq = obj.getChemSequence(j);
			int m = seq.getChemModelCount();
			if (m > 1)
				logger.finest("> 1 model in a record");
			for (int k = 0; k < m; k++) {
				IChemModel mod = seq.getChemModel(k);
				IAtomContainerSet som = mod.getMoleculeSet();
				if (som.getAtomContainerCount() > 1)
					logger.finest("> 1 molecule in a record");
				for (int l = 0; l < som.getAtomContainerCount(); l++) {
					mol = som.getAtomContainer(l);
					return mol;
				}

			}
		}

		reader = null;
		return mol;
	}

	public static IAtom newAtom(IChemObjectBuilder builder, IElement element) {
		return builder.newInstance(IAtom.class, element);
	}

	public static IAtom newAtom(IChemObjectBuilder builder, IAtom atom) {
		return builder.newInstance(IAtom.class, atom);
	}

	public static IAtom newAtom(IChemObjectBuilder builder, String atom) {
		return builder.newInstance(IAtom.class, atom);
	}

	public static IAtom newAtom(IChemObjectBuilder builder) {
		return builder.newInstance(IAtom.class);
	}

	public static IPseudoAtom newPseudoAtom(IChemObjectBuilder builder,
			String element) {
		return builder.newInstance(IPseudoAtom.class, element);
	}

	public static ILonePair newLonePair(IChemObjectBuilder builder, IAtom atom) {
		return builder.newInstance(ILonePair.class, atom);
	}

	public static IBond newBond(IChemObjectBuilder builder, IAtom a1, IAtom a2,
			Order order, IBond.Stereo stereo) {
		return builder.newInstance(IBond.class, a1, a2, order, stereo);
	}

	public static IBond newBond(IChemObjectBuilder builder, IAtom a1, IAtom a2,
			Order order) {
		return builder.newInstance(IBond.class, a1, a2, order);
	}

	public static IBond newBond(IChemObjectBuilder builder, IAtom a1, IAtom a2) {
		return builder.newInstance(IBond.class, a1, a2);
	}

	public static IBond newBond(IChemObjectBuilder builder) {
		return builder.newInstance(IBond.class);
	}

	public static IAtomContainer newAtomContainer(IChemObjectBuilder builder) {
		return builder.newInstance(IAtomContainer.class);
	}

	public static IAtomContainer newMolecule(IChemObjectBuilder builder) {
		return builder.newInstance(IAtomContainer.class);
	}

	public static IAtomContainer newAtomContainer(IChemObjectBuilder builder,
			IAtomContainer molecule) {
		return builder.newInstance(IAtomContainer.class, molecule);
	}

	public static ISingleElectron newSingleElectron(IChemObjectBuilder builder,
			IAtom atom) {
		return builder.newInstance(ISingleElectron.class, atom);
	}

	public static IAtomContainerSet newMoleculeSet(IChemObjectBuilder builder) {
		return builder.newInstance(IAtomContainerSet.class);
	}

	public static IAtomContainerSet newAtomContainerSet(
			IChemObjectBuilder builder) {
		return builder.newInstance(IAtomContainerSet.class);
	}

	public static IChemModel newChemModel(IChemObjectBuilder builder) {
		return builder.newInstance(IChemModel.class);
	}

	public static IChemSequence newChemSequence(IChemObjectBuilder builder) {
		return builder.newInstance(IChemSequence.class);
	}

	public static IChemFile newChemFile(IChemObjectBuilder builder) {
		return builder.newInstance(IChemFile.class);
	}

	public static IRingSet newRingSet(IChemObjectBuilder builder) {
		return builder.newInstance(IRingSet.class);
	}

	public static IRing newRing(IChemObjectBuilder builder, int i) {
		return builder.newInstance(IRing.class, i);
	}

	public static IElement newElement(IChemObjectBuilder builder, String element) {
		return builder.newInstance(IElement.class, element);
	}

	/**
	 * pre- cdk 1.3.5 code
	 * 
	 * public static IAtom newAtom(IChemObjectBuilder builder,IElement element)
	 * { return builder.newAtom(element); } public static IAtom
	 * newAtom(IChemObjectBuilder builder,IAtom atom) { return
	 * builder.newAtom(atom); } public static IAtom newAtom(IChemObjectBuilder
	 * builder,String atom) { return builder.newAtom(atom); } public static
	 * IAtom newAtom(IChemObjectBuilder builder) { return builder.newAtom(); }
	 * public static IPseudoAtom newPseudoAtom(IChemObjectBuilder builder,String
	 * element) { return builder.newPseudoAtom(element); } public static
	 * ILonePair newLonePair(IChemObjectBuilder builder,IAtom atom) {
	 * 
	 * return builder.newLonePair(atom); } public static IBond
	 * newBond(IChemObjectBuilder builder,IAtom a1, IAtom a2, Order order,
	 * IBond.Stereo stereo) { return builder.newBond(a1, a2, order, stereo); }
	 * public static IBond newBond(IChemObjectBuilder builder,IAtom a1, IAtom
	 * a2, Order order) { return builder.newBond(a1, a2, order); } public static
	 * IBond newBond(IChemObjectBuilder builder,IAtom a1, IAtom a2) { return
	 * builder.newBond(a1, a2); } public static IBond newBond(IChemObjectBuilder
	 * builder) { return builder.newBond(); } public static IAtomContainer
	 * newAtomContainer(IChemObjectBuilder builder) { return
	 * builder.newAtomContainer(); } public static IAtomContainer
	 * newMolecule(IChemObjectBuilder builder) { return builder.newMolecule(); }
	 * public static IAtomContainer newAtomContainer(IChemObjectBuilder
	 * builder,IAtomContainer molecule) { return
	 * builder.newAtomContainer(molecule); } public static ISingleElectron
	 * newSingleElectron(IChemObjectBuilder builder,IAtom atom) { return
	 * builder.newSingleElectron(atom); } public static IAtomContainerSet
	 * newMoleculeSet(IChemObjectBuilder builder) { return
	 * builder.newMoleculeSet(); } public static IAtomContainerSet
	 * newAtomContainerSet(IChemObjectBuilder builder) { return
	 * builder.newAtomContainerSet(); }
	 * 
	 * public static IChemModel newChemModel(IChemObjectBuilder builder) {
	 * return builder.newChemModel(); } public static IChemSequence
	 * newChemSequence(IChemObjectBuilder builder) { return
	 * builder.newChemSequence(); } public static IChemFile
	 * newChemFile(IChemObjectBuilder builder) { return builder.newChemFile(); }
	 * public static IRingSet newRingSet(IChemObjectBuilder builder) { return
	 * builder.newRingSet(); } public static IRing newRing(IChemObjectBuilder
	 * builder,int i) { return builder.newRing(i); } public static IAtomParity
	 * newAtomParity(IChemObjectBuilder builder,IAtom a,IAtom a1, IAtom a2,
	 * IAtom a3, IAtom a4, int parity) { return
	 * builder.newAtomParity(a,a1,a2,a3,a4,parity); } public static IElement
	 * newElement(IChemObjectBuilder builder,String element) { return
	 * builder.newElement(element); }
	 */

	/**
	 * Just copy atoms and bonds, discard all the flags, they will be
	 * recalculated later
	 */
	public static IAtomContainer copyChangeBuilders(IAtomContainer molecule,
			IChemObjectBuilder newBuilder) {
		final String no = "_NO";
		// atoms
		IAtomContainer newMol = newBuilder.newInstance(IAtomContainer.class);
		boolean aromatic = false;
		for (IAtom atom : molecule.atoms())
			if (atom.getFlag(CDKConstants.ISAROMATIC)) {
				aromatic = true;
				break;
			}

		aromatic = false;
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			IAtom atom = molecule.getAtom(i);
			molecule.getAtom(i).setProperty(no, i);
			IAtom newAtom = newBuilder.newInstance(IAtom.class, molecule
					.getAtom(i).getSymbol());
			newAtom.setCharge(atom.getCharge());
			newAtom.setFormalCharge(atom.getFormalCharge());
			newAtom.setStereoParity(atom.getStereoParity());
			if (atom.getPoint2d() != null) {
				newAtom.setPoint2d(new Point2d(atom.getPoint2d().x, atom
						.getPoint2d().y));
			}
			for (int k = 0; k < atom.getFlags().length; k++)
				newAtom.setFlag(k, atom.getFlag(k));
			// aromati/c |= atom.getFlag(CDKConstants.ISAROMATIC);
			// /newAtom.setFlag(CDKConstants.ISAROMATIC,atom.getFlag(CDKConstants.ISAROMATIC));
			newAtom.setAtomTypeName(atom.getAtomTypeName());
			newMol.addAtom(newAtom);
		}

		// bonds
		for (int i = 0; i < molecule.getBondCount(); i++) {
			IAtom[] newAtoms = new IAtom[molecule.getBond(i).getAtomCount()];
			for (int j = 0; j < molecule.getBond(i).getAtomCount(); j++) {
				Integer index = (Integer) molecule.getBond(i).getAtom(j)
						.getProperty(no);
				newAtoms[j] = newMol.getAtom(index);
			}
			IBond newBond = newBuilder.newInstance(IBond.class);
			newBond.setAtoms(newAtoms);
			newBond.setOrder(molecule.getBond(i).getOrder());
			for (int k = 0; k < molecule.getBond(i).getFlags().length; k++)
				newBond.setFlag(k, molecule.getBond(i).getFlag(k));
			newMol.addBond(newBond);
		}

		// single electrons
		for (int i = 0; i < molecule.getSingleElectronCount(); i++) {
			ISingleElectron singleElectron = newBuilder
					.newInstance(ISingleElectron.class);
			singleElectron.setElectronCount(molecule.getSingleElectron(i)
					.getElectronCount());
			Integer index = (Integer) molecule.getSingleElectron(i).getAtom()
					.getProperty(no);
			singleElectron.setAtom(newMol.getAtom(index));
			newMol.addSingleElectron(singleElectron);
		}
		// Lone pairs
		for (int i = 0; i < molecule.getLonePairCount(); i++) {
			ILonePair lonePair = newBuilder.newInstance(ILonePair.class);
			lonePair.setElectronCount(molecule.getLonePair(i)
					.getElectronCount());
			Integer index = (Integer) molecule.getLonePair(i).getAtom()
					.getProperty(no);
			lonePair.setAtom(newMol.getAtom(index));
			newMol.addElectronContainer(lonePair);
		}
		/*
		 * try {
		 * AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(newMol);
		 * CDKHueckelAromaticityDetector.detectAromaticity(newMol); } catch
		 * (Exception x) {}
		 * 
		 * 
		 * for (IAtom atom: newMol.atoms()) atom.setValency(null); if (aromatic)
		 * try { DeduceBondSystemTool dbt = new DeduceBondSystemTool(); newMol =
		 * dbt.fixAromaticBondOrders(newMol); } catch (Exception x)
		 * {x.printStackTrace();}
		 */
		return AtomContainerManipulator
				.removeHydrogensPreserveMultiplyBonded(newMol);
		// return newMol;
	}

	public static void clearProperties(IChemObject chemObject) {
		chemObject.setProperties(null);
		/*
		 * Iterator props = chemObject.getProperties().keySet().iterator();
		 * while (props.hasNext()) { chemObject.removeProperty(props.next()); }
		 */
	}

	/**
	 * Adds explicit hydrogens (without coordinates) to the IAtomContainer,
	 * equaling the number of set implicit hydrogens. Tetrahedrals with an
	 * implicit H now are set correctly.
	 *
	 * @param atomContainer
	 *            the atom container to consider
	 * @cdk.keyword hydrogens, adding
	 */
	public static void convertImplicitToExplicitHydrogens(
			IAtomContainer atomContainer) {
		List<IAtom> hydrogens = new ArrayList<IAtom>();
		List<IBond> newBonds = new ArrayList<IBond>();

		// store a single explicit hydrogen of each original neighbor
		Map<IAtom, IAtom> hNeighbor = Maps
				.newHashMapWithExpectedSize(atomContainer.getAtomCount());

		for (IAtom atom : atomContainer.atoms()) {
			if (!atom.getSymbol().equals("H")) {
				Integer hCount = atom.getImplicitHydrogenCount();
				if (hCount != null) {
					for (int i = 0; i < hCount; i++) {

						IAtom hydrogen = atom.getBuilder().newInstance(
								IAtom.class, "H");
						hydrogen.setAtomTypeName("H");
						hydrogen.setImplicitHydrogenCount(0);
						hydrogens.add(hydrogen);
						newBonds.add(atom.getBuilder().newInstance(IBond.class,
								atom, hydrogen, Order.SINGLE));

						if (hNeighbor.get(atom) == null)
							hNeighbor.put(atom, hydrogen);

					}
					atom.setImplicitHydrogenCount(0);
				}
			}
		}
		for (IAtom atom : hydrogens)
			atomContainer.addAtom(atom);
		for (IBond bond : newBonds)
			atomContainer.addBond(bond);

		// update tetrahedral elements with an implicit part
		List<IStereoElement> newStereo = new ArrayList<IStereoElement>();
		for (IStereoElement se : atomContainer.stereoElements()) {
			if (se instanceof ITetrahedralChirality) {
				ITetrahedralChirality tc = (ITetrahedralChirality) se;

				IAtom focus = tc.getChiralAtom();
				IAtom[] neighbors = tc.getLigands();
				IAtom hydrogen = hNeighbor.get(focus);

				// in sulfoxide - the implicit part of the tetrahedral centre
				// is a lone pair
				if (hydrogen == null) {
					newStereo.add(se);
					continue;
				}

				boolean FlagH = false;
				for (int i = 0; i < tc.getLigands().length; i++) {
					if (neighbors[i] == focus) {
						neighbors[i] = hydrogen;
						FlagH = true;
						break;
					}
				}

				if (FlagH) {
					TetrahedralChirality tc1 = new TetrahedralChirality(
							tc.getChiralAtom(), neighbors, tc.getStereo());
					newStereo.add(tc1);
				} else
					newStereo.add(se);
			} else
				newStereo.add(se); // non tetrahedral

			// TODO handle Extended Tetrahedral Chirality
		}

		if (!newStereo.isEmpty())
			atomContainer.setStereoElements(newStereo);
	}

	public static void convertExplicitHAtomsToImplicit(IAtomContainer mol)
			throws Exception {
		List<IBond> removeBonds = new ArrayList<IBond>();

		for (IBond bond : mol.bonds()) {
			if (bond.getAtom(0).getSymbol().equals("H")) {
				if (bond.getAtom(1).getSymbol().equals("H"))
					removeBonds.add(bond);
				else {
					add1ImplicitHAtom(bond.getAtom(1));
					removeBonds.add(bond);
				}
			} else {
				if (bond.getAtom(1).getSymbol().equals("H")) {
					add1ImplicitHAtom(bond.getAtom(0));
					removeBonds.add(bond);
				}
			}

			// System.out.println(bond.getAtom(0).getSymbol() + " ~ " +
			// bond.getAtom(1).getSymbol() + "  " + implicitHAtomsVector(mol));
		}

		// update tetrahedral elements with an explicit H ligand
		List<IStereoElement> newStereo = new ArrayList<IStereoElement>();
		for (IStereoElement se : mol.stereoElements()) {
			if (se instanceof ITetrahedralChirality) {
				ITetrahedralChirality tc = (ITetrahedralChirality) se;

				IAtom focus = tc.getChiralAtom();
				IAtom[] neighbors = tc.getLigands();

				boolean FlagH = false;
				for (int i = 0; i < tc.getLigands().length; i++) {

					if (neighbors[i].getSymbol().equals("H")) {
						neighbors[i] = focus;
						FlagH = true;
						break;
					}
				}

				if (FlagH) {
					TetrahedralChirality tc1 = new TetrahedralChirality(
							tc.getChiralAtom(), neighbors, tc.getStereo());
					newStereo.add(tc1);
				} else
					newStereo.add(se);
			} else
				newStereo.add(se); // non tetrahedral

			// TODO handle Extended Tetrahedral Chirality
		}

		if (!newStereo.isEmpty())
			mol.setStereoElements(newStereo);

		List<IAtom> removeAtoms = new ArrayList<IAtom>();

		for (IAtom atom : mol.atoms()) {
			if (atom.getSymbol().equals("H"))
				removeAtoms.add(atom);
		}

		for (IBond bond : removeBonds)
			mol.removeBond(bond);

		for (IAtom atom : removeAtoms)
			mol.removeAtom(atom);
	}

	public static void add1ImplicitHAtom(IAtom atom) {
		if (atom.getImplicitHydrogenCount() == CDKConstants.UNSET)
			atom.setImplicitHydrogenCount(new Integer(1));
		else
			atom.setImplicitHydrogenCount(atom.getImplicitHydrogenCount() + 1);
	}
}
