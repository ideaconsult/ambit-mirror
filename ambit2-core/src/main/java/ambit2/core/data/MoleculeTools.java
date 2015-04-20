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
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Point2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.io.IteratingChemObjectReaderWrapper;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.core.smiles.SmilesParserWrapper;

/**
 * Various structure processing. TODO refactor.
 * 
 * @author Nina Jeliazkova <br>
 *         <b>Modified</b> 2005-4-7
 */
public class MoleculeTools {
    protected static Logger logger = Logger.getLogger(MoleculeTools.class.getName());
    public final static int _FPLength = 1024;
    protected static CDKHydrogenAdder adder = null;
    protected static Fingerprinter fingerprinter = null;
    public static final String[] substanceType = { "organic", "inorganic", "mixture/unknown", "organometallic" };
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

    public static IMolecule getMolecule(String smiles) throws InvalidSmilesException {

	SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
	return parser.parseSmiles(smiles);
    }

    public static BitSet getFingerPrint(String smiles, int fpLength) throws Exception {
	SmilesParserWrapper sp = SmilesParserWrapper.getInstance();
	IAtomContainer mol = sp.parseSmiles(smiles);
	if (fingerprinter == null)
	    fingerprinter = new Fingerprinter(fpLength);
	return fingerprinter.getFingerprint(mol);

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

    public static boolean analyzeSubstance(IAtomContainer molecule) throws IOException {
	if ((molecule == null) || (molecule.getAtomCount() == 0))
	    return false;
	int noH = 0;
	// MFAnalyser mfa = new MFAnalyser(molecule);
	IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(molecule);

	IElement h = IsotopeFactory.getInstance(molecule.getBuilder()).getElement("H");
	if (MolecularFormulaManipulator.getElementCount(formula, h) == 0) {

	    noH = 1;
	    // TODO to insert H if necessary
	    // TODO this uses new SaturationChecker() which relies on
	    // cdk/data/config
	    if (adder == null)
		adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
	    try {
		adder.addImplicitHydrogens(molecule);
		int atomCount = molecule.getAtomCount();
		formula = MolecularFormulaManipulator.getMolecularFormula(molecule);
	    } catch (CDKException x) {
		logger.log(Level.SEVERE, x.getMessage(), x);
		formula = null;
	    }

	}
	if (formula != null) {
	    molecule.setProperty(AmbitCONSTANTS.FORMULA, MolecularFormulaManipulator.getString(formula));

	    double mass = MolecularFormulaManipulator.getTotalMassNumber(formula);
	    molecule.setProperty(AmbitCONSTANTS.MOLWEIGHT, new Double(mass));
	    try {
		molecule.setProperty(AmbitCONSTANTS.STRUCTURETYPE, sp.process(molecule));
	    } catch (Exception x) {
	    }
	    ;
	    molecule.setProperty(AmbitCONSTANTS.SUBSTANCETYPE,
		    getSubstanceType(MolecularFormulaManipulator.getString(formula)));
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

    public static IMolecule readMolfile(Reader molfile) throws Exception {
	IIteratingChemObjectReader mReader = new MyIteratingMDLReader(molfile, SilentChemObjectBuilder.getInstance());
	IMolecule molecule = null;
	while (mReader.hasNext()) {
	    Object mol = mReader.next();
	    if (mol instanceof IMolecule) {
		molecule = (IMolecule) mol;
		break;
	    }
	}
	mReader.close();
	return molecule;

    }

    public static IMolecule readMolfile(String molfile) throws Exception {
	StringReader reader = new StringReader(molfile);
	IMolecule mol = readMolfile(reader);
	reader.close();
	return mol;
    }

    public static IMolecule readPDBfile(String molfile) throws Exception {
	PDBReader pdbreader = new PDBReader(new StringReader(molfile));
	IteratingChemObjectReaderWrapper reader = new IteratingChemObjectReaderWrapper(pdbreader);
	try {
	    while (reader.hasNext()) {
		Object o = reader.next();
		if (o instanceof IMolecule)
		    return ((IMolecule) o);
	    }
	} catch (Exception x) {
	    throw x;
	} finally {
	    try {reader.close();} catch (Exception x) {}
	}
	return null;
    }

    public static IMolecule readCMLMolecule(String cml) throws Exception {
	IMolecule mol = null;
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

    public static IMolecule readCMLMolecule(InputStream in) throws Exception {
	IMolecule mol = null;

	CMLReader reader = new CMLReader(in);
	IChemFile obj = null;

	obj = (IChemFile) reader.read(newChemFile(SilentChemObjectBuilder.getInstance()));
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
		IMoleculeSet som = mod.getMoleculeSet();
		if (som.getMoleculeCount() > 1)
		    logger.finest("> 1 molecule in a record");
		for (int l = 0; l < som.getMoleculeCount(); l++) {
		    mol = som.getMolecule(l);
		    return mol;
		}

	    }
	}

	reader = null;
	return mol;
    }

    /**
     * CDK 1.3.5 code
     * */
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

    public static IPseudoAtom newPseudoAtom(IChemObjectBuilder builder, String element) {
	return builder.newInstance(IPseudoAtom.class, element);
    }

    public static ILonePair newLonePair(IChemObjectBuilder builder, IAtom atom) {
	return builder.newInstance(ILonePair.class, atom);
    }

    public static IBond newBond(IChemObjectBuilder builder, IAtom a1, IAtom a2, Order order, IBond.Stereo stereo) {
	return builder.newInstance(IBond.class, a1, a2, order, stereo);
    }

    public static IBond newBond(IChemObjectBuilder builder, IAtom a1, IAtom a2, Order order) {
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

    public static IMolecule newMolecule(IChemObjectBuilder builder) {
	return builder.newInstance(IMolecule.class);
    }

    public static IAtomContainer newAtomContainer(IChemObjectBuilder builder, IMolecule molecule) {
	return builder.newInstance(IAtomContainer.class, molecule);
    }

    public static ISingleElectron newSingleElectron(IChemObjectBuilder builder, IAtom atom) {
	return builder.newInstance(ISingleElectron.class, atom);
    }

    public static IMoleculeSet newMoleculeSet(IChemObjectBuilder builder) {
	return builder.newInstance(IMoleculeSet.class);
    }

    public static IAtomContainerSet newAtomContainerSet(IChemObjectBuilder builder) {
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

    public static IAtomParity newAtomParity(IChemObjectBuilder builder, IAtom a, IAtom a1, IAtom a2, IAtom a3,
	    IAtom a4, int parity) {
	return builder.newInstance(IAtomParity.class, a, a1, a2, a3, a4, parity);
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
     * builder.newAtomContainer(); } public static IMolecule
     * newMolecule(IChemObjectBuilder builder) { return builder.newMolecule(); }
     * public static IAtomContainer newAtomContainer(IChemObjectBuilder
     * builder,IMolecule molecule) { return builder.newAtomContainer(molecule);
     * } public static ISingleElectron newSingleElectron(IChemObjectBuilder
     * builder,IAtom atom) { return builder.newSingleElectron(atom); } public
     * static IMoleculeSet newMoleculeSet(IChemObjectBuilder builder) { return
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
    public static IAtomContainer copyChangeBuilders(IAtomContainer molecule, IChemObjectBuilder newBuilder) {
	final String no = "_NO";
	// atoms
	IMolecule newMol = newBuilder.newInstance(IMolecule.class);
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
	    IAtom newAtom = newBuilder.newInstance(IAtom.class, molecule.getAtom(i).getSymbol());
	    newAtom.setCharge(atom.getCharge());
	    newAtom.setFormalCharge(atom.getFormalCharge());
	    newAtom.setStereoParity(atom.getStereoParity());
	    if (atom.getPoint2d() != null) {
		newAtom.setPoint2d(new Point2d(atom.getPoint2d().x, atom.getPoint2d().y));
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
		Integer index = (Integer) molecule.getBond(i).getAtom(j).getProperty(no);
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
	    ISingleElectron singleElectron = newBuilder.newInstance(ISingleElectron.class);
	    singleElectron.setElectronCount(molecule.getSingleElectron(i).getElectronCount());
	    Integer index = (Integer) molecule.getSingleElectron(i).getAtom().getProperty(no);
	    singleElectron.setAtom(newMol.getAtom(index));
	    newMol.addSingleElectron(singleElectron);
	}
	// Lone pairs
	for (int i = 0; i < molecule.getLonePairCount(); i++) {
	    ILonePair lonePair = newBuilder.newInstance(ILonePair.class);
	    lonePair.setElectronCount(molecule.getLonePair(i).getElectronCount());
	    Integer index = (Integer) molecule.getLonePair(i).getAtom().getProperty(no);
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
	return AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(newMol);
	// return newMol;
    }
}
