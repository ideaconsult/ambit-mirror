/**
 * Created on 2004-11-4
 *
 */
package ambit2.test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.atomtype.HybridizationStateATMatcher;
import org.openscience.cdk.atomtype.IAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;


/**
 * JUnit test of CML functionality 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class cmlTest extends TestCase {

public void xtestCML0() {
	
	StringWriter output = new StringWriter();
	boolean makeFragment = true;

	Molecule mol = MoleculeFactory.makeAlkane(8);
	mol.setProperty(CDKConstants.AUTONOMNAME,"Alkane");
            
	assertEquals("Alkane",(String)mol.getProperty(CDKConstants.AUTONOMNAME));
	CMLWriter cmlwriter = null;
	try {
		cmlwriter = new CMLWriter(output);
	} catch (Exception e) {
		fail();
	}
	
	try {
		if (cmlwriter != null) {
			cmlwriter.write(mol);
			cmlwriter.close();
		}
	} catch (Exception e) {
		fail();
	}
	String cmlcode = output.toString();
	String cml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	"\n<molecule id=\"m1\">" +
	"\n<atomArray>" +
	"\n<atom id=\"a1\" elementType=\"C\"/>" +
	"\n<atom id=\"a2\" elementType=\"C\"/>" +
	"\n<atom id=\"a3\" elementType=\"C\"/>" +
	"\n<atom id=\"a4\" elementType=\"C\"/>" +
	"\n<atom id=\"a5\" elementType=\"C\"/>" +
	"\n<atom id=\"a6\" elementType=\"C\"/>" +
	"\n<atom id=\"a7\" elementType=\"C\"/>" +
	"\n<atom id=\"a8\" elementType=\"C\"/>" +
	"\n</atomArray>" +
	"\n<bondArray>" +
	"\n<bond id=\"b1\" atomRefs2=\"a2 a1\" order=\"S\"/>" +
	"\n<bond id=\"b2\" atomRefs2=\"a3 a2\" order=\"S\"/>" +
	"\n<bond id=\"b3\" atomRefs2=\"a4 a3\" order=\"S\"/>" +
	"\n<bond id=\"b4\" atomRefs2=\"a5 a4\" order=\"S\"/>" +
	"\n<bond id=\"b5\" atomRefs2=\"a6 a5\" order=\"S\"/>" +
	"\n<bond id=\"b6\" atomRefs2=\"a7 a6\" order=\"S\"/>" +
	"\n<bond id=\"b7\" atomRefs2=\"a8 a7\" order=\"S\"/>" +
	"\n</bondArray>" +
	"\n<scalar title=\"AutonomName\">Alkane</scalar>" +
	"\n</molecule>\n";
	System.out.println(cmlcode);
	assertEquals(cml,cmlcode);	
	CMLReader cmlreader = new CMLReader(cmlcode);
	Molecule mol1 = null;
	/*
	try {
		mol1 = (Molecule) cmlreader.read(new Molecule());
	
	} catch (Exception e) {
		e.printStackTrace();
		
		fail();
	}
	//mol = getMolecule((ChemFile)cmlr.read(new ChemFile()));	
	
	assertEquals(mol,mol1);
	*/
	
	
}
protected IMolecule getMolecule() {
	  Molecule mol = new Molecule();
	  Atom a1 = new Atom("C");
	  mol.addAtom(a1);
	  Atom a2 = new Atom("C");
	  mol.addAtom(a2);
	  Atom a3 = new Atom("C");
	  mol.addAtom(a3);
	  Atom a4 = new Atom("C");
	  mol.addAtom(a4);
	  Atom a5 = new Atom("C");
	  mol.addAtom(a5);
	  Atom a6 = new Atom("C");
	  mol.addAtom(a6);
	  IBond b1 = new Bond(a1, a2, Order.DOUBLE);
	  mol.addBond(b1);
	  IBond b2 = new Bond(a2, a3, Order.SINGLE);
	  mol.addBond(b2);
	  IBond b3 = new Bond(a3, a4, Order.DOUBLE);
	  mol.addBond(b3);
	  IBond b4 = new Bond(a4, a5, Order.SINGLE);
	  mol.addBond(b4);
	  IBond b5 = new Bond(a5, a6, Order.DOUBLE);
	  mol.addBond(b5);
	  IBond b6 = new Bond(a6, a1, Order.SINGLE);
	  mol.addBond(b6);
	return mol;
}

	protected String getCML(IMolecule mol) throws Exception {
			StringWriter w = new StringWriter();
			new CMLWriter(w).write(mol);
			
			return w.toString();
	}
	public void testRoundtrip() {
		roundtrip(false);
	}
	public void testRoundtripAromaticity() {
		roundtrip(true);
	}	
	public void roundtrip(boolean checkAromaticity) {

		IMolecule m = getMolecule();
		HydrogenAdder h = new HydrogenAdder();
		
		try {
			h.addExplicitHydrogensToSatisfyValency(m);
			if (checkAromaticity) {
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);	
				CDKHueckelAromaticityDetector.detectAromaticity(m);
			}			
			String cml = getCML(m);
			System.out.println(cml);
			/*
			 * Not sure why CMLReader(Reader) is deprecated , with StringBufferInputStream now deprecated, 
			 * how do we write the same code without deprecated methods?
			 */
			CMLReader reader = new CMLReader(new StringReader(cml));
			IChemFile chemFile = DefaultChemObjectBuilder.getInstance().newChemFile();
			reader.read(chemFile);
			/*
			 * Could we have List<IMolecule> as return type from ChemFileManipulator.getAllAtomContainers()  (and similar functions)?
			 * Then type safety will be better supported and there will be no need for a cast each (IMolecule) list.get(i) 
			 */
			List<IAtomContainer> molecules = ChemFileManipulator.getAllAtomContainers(chemFile);
			assertEquals(1,molecules.size());
			
			assertTrue(UniversalIsomorphismTester.isIsomorph(m,molecules.get(0)));
			
			Fingerprinter fp = new Fingerprinter();
			assertEquals(fp.getFingerprint(m),fp.getFingerprint(molecules.get(0)));
			
			/*
			AtomEnvironmentGenerator g = new AtomEnvironmentGenerator();
			g.process(m);
			g.process(molecules[0]);
			
			AtomEnvironmentList ae = (AtomEnvironmentList) m.getProperty(AmbitCONSTANTS.AtomEnvironment);
			AtomEnvironmentList aenew = (AtomEnvironmentList) (molecules[0]).getProperty(AmbitCONSTANTS.AtomEnvironment);
			assertEquals(ae,aenew);
			*/
			
			IAtomTypeMatcher atm = new HybridizationStateATMatcher();
			List<IAtomType> at = getAtomTypes(atm, m);
			List<IAtomType> atnew = getAtomTypes(atm, (IMolecule) molecules.get(0));
			
			printAtomTypes(at);
			System.out.println("new");
			printAtomTypes(atnew);
			
			
		} catch (Exception x) {
			fail();
		}		
	}
	protected void printAtomTypes( List<IAtomType> list) {
		Iterator<IAtomType> iterator = list.iterator();
		while (iterator.hasNext())  {
			IAtomType t = iterator.next();
			System.out.print(t.getAtomTypeName());
			System.out.print('\t');
			System.out.print(t.getHybridization());
			System.out.print('\t');
			System.out.print(t.getBondOrderSum());
			System.out.print('\t');
			System.out.println(t.getMaxBondOrder());
		}	
	}
	protected List<IAtomType> getAtomTypes(IAtomTypeMatcher atm, IMolecule m) throws CDKException {
		
		List<IAtomType> list = new ArrayList<IAtomType>();
		// would be nice to have Enumeration<IAtom>
		Iterator<IAtom> e = m.atoms();
		while (e.hasNext()) {
			IAtomType atomType = atm.findMatchingAtomType(m,e.next());
			if (atomType != null) list.add(atomType);
		}
		Collections.sort(list,new Comparator<IAtomType>() {
			public int compare(IAtomType o1, IAtomType o2) {
				
				return o1.getAtomTypeName().compareTo(o2.getAtomTypeName());
			}
		});
		return list;
		
	}


}
