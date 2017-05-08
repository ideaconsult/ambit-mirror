package ambit2.tautomers.test;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.tautomers.processor.StructureStandardizer;
import ambit2.tautomers.processor.TautomerProcessor;
import junit.framework.Assert;

public class TestTautomersProcessor {
	protected static final TautomerProcessor p = new TautomerProcessor(null);
	protected static final StructureStandardizer st = new StructureStandardizer();

	@Test
	public void test() throws Exception {

		String[] smiles = new String[] { "Cc1cc2nc(NCCCO)n(CC(=O)c3cc(c(O)c(c3)C(C)(C)C)C(C)(C)C)c2cc1C",
				"CCc1cc2c(cc1OC(C)=O)oc(C)c(-c1nc3ccccc3n1C)c2=O", "CCCCCCCN(C(=O)CCl)c1cccc(C)c1",
				"CCc1ccc(OCC(=O)Nc2nnc[nH]2)cc1", "CN(C)Cc1c(nnn1-c1nonc1N)C(=O)NN=Cc1ccccc1OCc1ccc(F)cc1",
				"CCOC(=O)C(C)Oc1cccc2c1ccn(CC(=O)Nc1ccc(OC)cc1)c2=O" };
		testSmiles(smiles);
	}

	@Test
	public void test7() throws Exception {

		String[] smiles = new String[] { "CC1CN(CC(O1)C)C2=NC3=CC=CC=C3N=C2C(C#N)S(=O)(=O)C4=CC=CC(=C4)C" };
		testSmiles(smiles);
	}
	
	public void testSmiles(String[] smiles) throws Exception {
		SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());

		st.setGenerateTautomers(true);
		st.setGenerateInChI(true);
		st.setGenerateSMILES_Canonical(true);
		st.setImplicitHydrogens(true);
		st.setNeutralise(true);
		st.setSplitFragments(true);

		st.setClearIsotopes(true);
		SmilesGenerator g = SmilesGenerator.absolute();
		for (String smi : smiles) {
			IAtomContainer mol = sp.parseSmiles(smi);
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			/*
			 * IAtomContainer tautomer = p.process(mol); for (IBond b :
			 * tautomer.bonds()) { Assert.assertNotSame(IBond.Order.UNSET,
			 * b.getOrder()); }
			 */

			IAtomContainer smol = st.process(mol);

			for (IBond b : smol.bonds()) {
				Assert.assertNotSame(IBond.Order.UNSET, b.getOrder());
			}
			System.out.println(String.format("%s\t-->\t%s", smi, g.create(smol)));
			Assert.assertFalse(smol.getProperties().get("http://www.opentox.org/api/1.1#InChI").toString()
					.startsWith("InChI=1S/"));
		}
	}
}
