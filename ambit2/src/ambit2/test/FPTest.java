/*
 * Created on 16-Apr-2005
 *
 * @author Nina Jeliazkova
 */
package ambit2.test;

import java.util.BitSet;

import junit.framework.TestCase;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;

import ambit2.smiles.SmilesParserWrapper;
import ambit2.data.molecule.MoleculeTools;

/**
 * TODO add description for FPTest
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 16-Apr-2005
 */
public class FPTest extends TestCase {
	public static final String[] SmilesToTest = {
		"C1=CC=CC=C1",
		"NC1=CC2=C(C=C1)C3=C(C2)C=C(Br)C=C3",
		"COC1=CC=C(C)C=C1N",
		"NC1=CC=CC2=C1C=CC=N2",
		"NC1=CC=CC2=C1N=CC=C2",
		"NC1=CC2=NC3=CC=CC(N)=C3N=C2C=C1",
		"CC1=CC(C)=C(N)C=C1C",
		"CC1=CC(C)=C(N)C=C1",
		"NC1=CC=CC=C1C2=CC=C(C=C2)[N+]([O-])=O",
		"NC1=C(O)C=C(C=C1)[N+]([O-])=O",
		"CC1=CC(N)=C(O)C=C1",
		"NC1=CC2=NC3=CC=CC=C3N=C2C=C1",
		"NC1=C(C=C(C=C1)[N+]([O-])=O)[N+]([O-])=O",
		"CC(=O)NC1=CC=C2C(CC3=CC(N)=CC=C23)=C1",
		"NC1=CC=C2N=C3C=CC(N)=CC3=NC2=C1",
		"NC1=CC2=C(C=C1)N=CC=C2",
		"NC1=CC=CC2=NC3=C(N)C=CC=C3N=C12",
		"NC1=C(C=C(C=C1Br)[N+]([O-])=O)[N+]([O-])=O",
		"NC1=CC=CC2=NC3=CC=CC(N)=C3N=C12",
		"NC1=CC(=CC=C1)C2=CC=C(C=C2)[N+]([O-])=O",
		"NC1=CC(Cl)=CC=C1O",
		"NC1=CC=C(C=C1)C2=CC=C(C=C2)[N+]([O-])=O",
		"NC1=CC=CC2=NC3=CC=CC=C3N=C12",
		"NC1=C(C=C(Cl)C=C1)[N+]([O-])=O",
		"NC1=CC2=C(C=CC=C2)N=C1",
		"NC1=C(N)C=C(Cl)C=C1"
	};
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(FPTest.class);
	}

	/**
	 * A weird bug perhaps. 
	 * If a molecule is obtained by parsing a smiles, then the fingerprint generated
	 * from this molecule sometimes differ from the fingerprint , generated from the
	 * canonical smiles, representing the same molecule.
	 * This doesn not manifest itself always (yes, benzene works fine)
	 * and moreover the difference is very tiny (a few bits) and depends   
	 * on the fingerprint length.
	 */
	public void testGetFingerPrint() {
	    boolean print = false;
	    Fingerprinter fingerprinter;
	    SmilesParserWrapper sp = SmilesParserWrapper.getInstance();
		SmilesGenerator gen = new SmilesGenerator();
		fingerprinter = new Fingerprinter(1024);
		int c = 0;
		for (int i = 0; i < SmilesToTest.length; i++) {
			try {
				//parse the smiles to get the Molecule
			    
				IMolecule mol1 = sp.parseSmiles(SmilesToTest[i]);
				//generate fingerprint
				BitSet bs1 = fingerprinter.getFingerprint((AtomContainer) mol1);
				
				//create canonical smiles from the molecule 
				String smilesNew = gen.createSMILES(mol1);
				
				//now parse back the canonical smiles
				//what is expected is to obtain the same molecule we started with
				IMolecule mol2 = sp.parseSmiles(smilesNew);
				//now its fingerprint
				BitSet bs2 = fingerprinter.getFingerprint((AtomContainer) mol2);
				
				//hopefully, both fingerprints should be the same
				//assertEquals(bs1,bs2);
				
				//printing just out of curiousity
				 if (!bs1.equals(bs2)) {
				    if (print) {
						System.err.println("Compound No.\t" + (i+1) + "\nOriginal SMILES\t" + SmilesToTest[i]);
						System.err.println("Fingerprint\t" + bs1.toString());
						System.err.println("Canonical SMILES\t" + smilesNew);
						System.err.println("Fingerprint\t" +bs2.toString());
						bs1.xor(bs2);
						System.err.println("Number of bits different\t" + bs1.cardinality() + "\n");
				    }
				 } else c++;

			} catch (NoSuchAtomException x) {
				x.printStackTrace();
			} catch (InvalidSmilesException x) {
				System.err.println("Invalid SMILES \t" + SmilesToTest[i]);
			} catch (Exception x) {
			    x.printStackTrace();
			}
		}
		if (print) {
			System.err.println("SMILES processed\t" + SmilesToTest.length);
			System.err.println("Same fingerprints\t" + c);
		}
		
		assertEquals(c,SmilesToTest.length);
		
		sp = null;
		gen = null;
		
	}
	
	public void testBitset2long() {
		long[] h16 = new long[16];
		BitSet bs = new BitSet();
		for (int i=0; i < 1024;i++)
			bs.set(i,true);
		MoleculeTools.bitset2Long16(bs,64,h16);
		
		for (int i=0; i < h16.length; i++) {
			System.out.println(Long.toHexString(h16[i]));
		}
		
		
	}
}
