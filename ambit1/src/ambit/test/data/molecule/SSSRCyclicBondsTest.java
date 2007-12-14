/**
 * <b>Filename</b> SSSRCyclicBondsTest.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-5-26
 * <b>Project</b> ambit
 */
package ambit.test.data.molecule;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit.data.molecule.MoleculeConvertor;

/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-5-26
 */
public class SSSRCyclicBondsTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SSSRCyclicBondsTest.class);
    }
    public void testCyclicBonds() {
        IMolecule mol = MoleculeFactory.makeAzulene();
    	assertEquals(MoleculeConvertor.getCyclicBondsBySSSR(mol),mol.getBondCount());
        mol = MoleculeFactory.make4x3CondensedRings();
    	assertEquals(MoleculeConvertor.getCyclicBondsBySSSR(mol),mol.getBondCount());
        mol = MoleculeFactory.makeAlkane(10);
    	assertEquals(MoleculeConvertor.getCyclicBondsBySSSR(mol),0);
        mol = MoleculeFactory.makeEthylCyclohexane();
    	assertEquals(MoleculeConvertor.getCyclicBondsBySSSR(mol),6);
    }
}

