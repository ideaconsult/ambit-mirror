package ambit2.test.processors;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.config.AmbitCONSTANTS;
import ambit2.data.descriptors.AtomEnvironmentList;
import ambit2.exceptions.AmbitException;
import ambit2.processors.structure.AtomEnvironmentGenerator;

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
