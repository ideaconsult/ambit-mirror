/*
 * Created on 2005-4-9

 * @author Nina Jeliazkova nina@acad.bg
 *
 * Project : ambit
 * Package : ambit.test
 * Filename: SMILESTest.java
 */
package ambit.test;

import java.io.StringReader;
import java.util.BitSet;

import junit.framework.TestCase;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.MFAnalyser;

import ambit.data.molecule.SmilesParserWithTimeout;
import ambit.io.FilteredMDLReader;

/**
 * TODO Add SMILESTest description
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-9
 */
public class SMILESTest extends TestCase {
	protected void setUp() throws Exception {
			super.setUp();
			LoggingTool.configureLog4j();
		}	
//c1cc(ccc1C(c2ccc(cc2)Cl)=C(Cl)Cl)Cl
    //Clc1ccc(cc1)C(=C(Cl)Cl)c2ccc(Cl)cc2 
	public void testCanonicalSmiles() {
	    //String[] smiles = {"NCCO","OCCN"};
	    String[] smiles = {"c1cc(ccc1C(c2ccc(cc2)Cl)=C(Cl)Cl)Cl",
	            "Clc1ccc(cc1)C(=C(Cl)Cl)c2ccc(Cl)cc2",
	            "C(=C(Cl)Cl)(C1C=CC(=CC=1)Cl)C2=CC=C(C=C2)Cl" 
	            };
	    String[] newSmiles = {"","",""};
	    Molecule m = getMolecule();
		SmilesParser parser = new SmilesParser();
		try {
		    for (int i=0; i < smiles.length;i++) {
				Molecule mol = parser.parseSmiles(smiles[i]);
				SmilesGenerator gen1 = new SmilesGenerator(mol.getBuilder());
				newSmiles[i] = gen1.createSMILES(mol);
				if (i>0) assertEquals(newSmiles[i],newSmiles[i-1]);
				System.out.println(newSmiles[i]);
		    }
			
						
		} catch (InvalidSmilesException x) {
			fail();
		}
	}
	public void testSmilesParser() {

	    String[] smiles = {
	            "ClC1=CC(Cl)=C(OC2=CC=C(C=C2)N(=O)=O)C=C1",
	            "ClC1=C(Cl)C(Cl)=C(C(Cl)=C1Cl)N(=O)=O",
	            "O=N(=O)C1=CC=CC=C1",
	            "CNC1=NC2=C(C=C(Cl)C=C2)C(C3=CC=CC=C3)=N(=O)C1",
	            "ClC1=CC=C(S(=O)(=O)NC(=O)NCCC)C=C1",
	            "ClC1C(=O)OC2=C(C=1C)C=CC(OP(=S)(OCC)OCC)=C2",
	            "ClC1=C(Cl)N=C(OP(=S)(OCC)OCC)C(Cl)=C1",
	            "ClCCN(P1(=O)OCCCN1)CCCl",
	            "ClC(Cl)=COP(=O)(OC)OC",
	            "P(=O)(O)(O)C(O)(P(=O)(O)O)CCCN",
	            "P(=O)(O)(O)COC(CN1C(=O)N=C(N)C=C1)CO",
	            "S(P(=S)(OCC)OCC)C1OCCOC1SP(=S)(OCC)OCC",
	            "ClCCOS(=O)OC(C)COC1=CC=C(C(C)(C)C)C=C1",
	            "S(=O)(=O)(C)NC2=C(O)C=CC(C(O)C(C)NCCC1=CC=C(OC)C=C1)=C2",
	            "S2C(CN(C1=CC=CC=C1)CCN(C)C)=CC=C2",
	            "P(=O)(O)(O)C(O)(P(=O)(O)O)CN1C=CN=C1",
	            "ClCC(OP(=O)(OC(CCl)CCl)OC(CCl)CCl)CCl",
	            "BrC(CBr)COP(=O)(OCC(Br)CBr)OCC(Br)CBr",
	            "ClCCOP(=O)(OCCCl)OCCCl",
	            "CS(C)(=O)=O",
	            "COS(=O)OC",
	            "COS(O)(=O)=O",
	            "CP(O)(O)=O",
	            "COP(O)(O)=O"

	            };
	    String[] newSmiles = new String[smiles.length];
	    Molecule m = getMolecule();
		SmilesParser parser = new SmilesParser();
		Fingerprinter fp = new Fingerprinter(1024);
		try {
		    for (int i=0; i < smiles.length;i++) {
				IMolecule mol = parser.parseSmiles(smiles[i]);
				SmilesGenerator gen1 = new SmilesGenerator(mol.getBuilder());
				newSmiles[i] = gen1.createSMILES(mol);
			//	if (i>0) assertEquals(newSmiles[i],newSmiles[i-1]);
				//System.out.println(newSmiles[i]);
				
				IMolecule mol1 = parser.parseSmiles(newSmiles[i]);
				try {
				    BitSet bs = fp.getFingerprint(mol);
				    BitSet bs1 = fp.getFingerprint(mol1);

				    if (!bs.equals(bs1)) {
					    System.out.print((i+1));
					    System.out.println("\tOld\t"+smiles[i]);
					    System.out.println("\tNew\t"+newSmiles[i]);				        
				    }
				} catch (Exception x) {
				    x.printStackTrace();
				    fail();
				}
		    }
			
						
		} catch (InvalidSmilesException x) {
			fail();
		}
	}
	
	public Molecule getMolecule() {
	    
	    String sdf = "$$$$"+
		"\r\r\n"+
		"\n"+ 
		"\r\n"+
		 "18 19  0  0  0  0  0  0  0  0  1 V2000\n"+
		    "4.6054   -1.9895    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "4.6054   -0.6632    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "3.4540   -0.0000    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "5.7567   -0.0000    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "5.7567   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "6.9080   -1.9895    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "8.0594   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "8.0594   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "6.9080   -4.6514    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "5.7567   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "9.2107   -4.6514    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "3.4540   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "3.4540   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "2.3027   -4.6514    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "1.1513   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "1.1513   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "2.3027   -1.9895    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		    "0.0000   -4.6514    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
		  "1  2  2  0  0  0  0\n"+
		  "1  5  1  0  0  0  0\n"+
		  "1 12  1  0  0  0  0\n"+
		  "2  3  1  0  0  0  0\n"+
		  "2  4  1  0  0  0  0\n"+
		  "5  6  1  0  0  0  0\n"+
		  "5 10  2  0  0  0  0\n"+
		  "6  7  2  0  0  0  0\n"+
		  "7  8  1  0  0  0  0\n"+
		  "8  9  2  0  0  0  0\n"+
		  "8 11  1  0  0  0  0\n"+
		  "9 10  1  0  0  0  0\n"+
		 "12 13  2  0  0  0  0\n"+
		 "12 17  1  0  0  0  0\n"+
		 "13 14  1  0  0  0  0\n"+
		 "14 15  2  0  0  0  0\n"+
		 "15 16  1  0  0  0  0\n"+
		 "15 18  1  0  0  0  0\n"+
		 "16 17  2  0  0  0  0\n"+
		"M  END";
	    
	    FilteredMDLReader r = new FilteredMDLReader(new StringReader(sdf));
	    Molecule m = new Molecule();
	    try {
	        m = (Molecule)r.read(m);
	    } catch (CDKException x) {
	        x.printStackTrace();
	    }
	    return m;
	}
	public void testCanonicalFormula() {
		SmilesParser parser = new SmilesParser();
		try {
			Molecule mol1 = parser.parseSmiles("NCCO");
			Molecule mol2 = parser.parseSmiles("OCCN");
			
			MFAnalyser mfa = new MFAnalyser(mol1);
			String formula1 = mfa.getMolecularFormula();
			
			mfa.analyseAtomContainer(mol2);
			String formula2 = mfa.getMolecularFormula();
			
			assertEquals(formula1,formula2);
			System.out.println(formula1);
			
			mfa = new MFAnalyser("NOH7C2", new org.openscience.cdk.AtomContainer());
			String formula3 = mfa.getMolecularFormula();
			assertEquals(formula3,formula2);
			
		} catch (InvalidSmilesException x) {
			fail();
		}
	}	
	
	public void xtestOasisSMILES() {
		try {
			String smiles = "c1ccccc1CN{+1}(.Cl{-1})(CC)(CC)CC";
			smiles = smiles.replaceAll("\u007B+\u007D", "");
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}
	
	public void testTimeout() {

		SmilesParserWithTimeout r = new SmilesParserWithTimeout() ;
		try {
		Molecule mol = r.parseSmiles("CCCC",1000);
		System.out.println(mol);
		mol = r.parseSmiles("n(c(c(c(Nc(c(c(nc(c(c(Nc(c1c(cccc2)c2)c3)c3)c(cccc4)c4)c5)c5)c(cccc6)c6)c7)c7)c(cccc8)c8)cc9)c19",10000);
		 System.out.println(mol);
		 mol = r.parseSmiles("CN{+}",1000);
		 System.out.println(mol);
		} catch (CDKException x) {
		    x.printStackTrace();
		}
		/*
		r.setSmiles();
		Thread t = new Thread(r);
		try {
			t.start();
			Thread.sleep(60000);
			r.cancel();
			t.join();
		} catch (InterruptedException x) {
			x.printStackTrace();
		}
		Molecule mol = r.getMol();
		assertNull(mol);
		*/
	}
	
	public void testNoTimeout() {

		SmilesParser r = new SmilesParser() ;
		try {
		Molecule mol = r.parseSmiles("CCCC");
		System.out.println(mol);
		mol = r.parseSmiles("n(c(c(c(Nc(c(c(nc(c(c(Nc(c1c(cccc2)c2)c3)c3)c(cccc4)c4)c5)c5)c(cccc6)c6)c7)c7)c(cccc8)c8)cc9)c19");
		 System.out.println(mol);
		 mol = r.parseSmiles("CN{+}");
		 System.out.println(mol);
		} catch (CDKException x) {
		    x.printStackTrace();
		}
	}
}


