package ambit2.dragon.test;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.core.io.FileInputState;
import ambit2.vega.VegaShell;
import junit.framework.Assert;

public class VegaShellTest {

	@Test
	public void testVegaShell() throws Exception {
		//VegaShell shell = new VegaShell("ambit2/dragon/test/test.pref");
		VegaShell shell = new VegaShell(null);

		try (IIteratingChemObjectReader reader = FileInputState.getReader(
				getClass().getClassLoader().getResourceAsStream("ambit2/dragon/test/structure.sdf"), "structure.sdf")) {

			Assert.assertTrue(reader != null);
			while (reader.hasNext()) {
				Object o = reader.next();
				IAtomContainer mol = (IAtomContainer) o;
				mol = shell.process(mol);
				
				System.out.println(mol.getProperties());
				Assert.assertTrue(mol.getProperties().size() > 0);
			}
		}
	}
}
