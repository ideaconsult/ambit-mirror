package ambit.test.processors;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit.data.descriptors.AtomEnvironmentList;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.structure.AtomEnvironmentGenerator;

public class AtomEnvironmentGeneratorTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AtomEnvironmentGeneratorTest.class);
	}
	public void test() {
		IMolecule mol = MoleculeFactory.makeBenzene();
		AtomEnvironmentGenerator gen = new AtomEnvironmentGenerator();
		try {
			gen.process(mol);
			Object o = mol.getProperty(AmbitCONSTANTS.AtomEnvironment);
			assertNotNull(o);
			assertTrue(o instanceof AtomEnvironmentList);
			//System.out.println(o);
			
			assertEquals(4,((AtomEnvironmentList) o).size());
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		}
		
	}
}
