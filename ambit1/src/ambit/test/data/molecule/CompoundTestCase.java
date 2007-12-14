/**
 * Created on 2005-3-28
 *
 */
package ambit.test.data.molecule;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit.data.molecule.Compound;
import ambit.data.molecule.CompoundFactory;
import ambit.data.molecule.CompoundType;
import ambit.data.molecule.StructureType;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;


/**
 * JUnit test for {@link ambit.data.molecule.Compound} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class CompoundTestCase extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(CompoundTestCase.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Constructor for CompoundTestCase.
	 * @param arg0
	 */
	public CompoundTestCase(String arg0) {
		super(arg0);
	}

	/*
	 * Class under test for void Compound(Molecule)
	 */
	public void testCompoundMolecule() {
		IMolecule mol = MoleculeFactory.makeBenzene();
		mol.setProperty(AmbitCONSTANTS.CASRN,"71-43-2");
		mol.setProperty(AmbitCONSTANTS.NAMES,"Benzene");		
		Compound c1 = new Compound(mol);
		assertEquals(c1.getCAS_RN(),"71-43-2");
		assertEquals(c1.getSMILES(),"");
		assertEquals(c1.getName(),"Benzene");		
		assertEquals(78,c1.getMass(),0.1);

		c1.updateMolecule();
		assertEquals(c1.getSMILES(),"c1ccccc1");		

		Compound c2 = CompoundFactory.createBenzene();
		assertEquals(c1,c2);
		assertEquals(new StructureType("SMILES"),c2.getStructureType());
		assertEquals(new CompoundType("organic"),c2.getSubstanceType());
		
		c2.updateMolecule();
		assertEquals(c2.isUniqueSMILES(),true);
		
		c2.clear();
		try {
		    c2.setMolecule(MoleculeFactory.makeBranchedAliphatic(),null);
		} catch (AmbitException x) {
		    x.printStackTrace();
		}
		assertEquals(c2.getSMILES(),"");
		assertEquals(c2.getCAS_RN(),"");
		assertEquals(c2.getName(),"");
	}



}
