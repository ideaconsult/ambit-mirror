package ambit2.tautomers.test;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.tautomers.TautomerManager;
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
	public void testH() throws Exception {
		TautomerProcessor tm = new TautomerProcessor(Logger.getAnonymousLogger());

		String[] smiles = new String[] { "C1=CC=C2NC(C(C3=NC4=C(C=CC(=C4)N5CCN(CC5)C)N3)=C(C2=C1NC)N)=O"
				// "CNc1cccc2NC(=O)C(=C(N)c12)c3nc4cc(ccc4[nH]3)N5CCN(C)CC5"
		};
		for (String s : smiles) {
			SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
			IAtomContainer mol = p.parseSmiles(s);
			// IAtomContainer t = tm.process(mol);
			// System.out.println(SmilesGenerator.isomeric().create(t));

			TautomerManager tautomerManager = new TautomerManager();
			tautomerManager.tautomerFilter.setFlagApplyDuplicationCheckIsomorphism(false);
			tautomerManager.tautomerFilter.setFlagApplyDuplicationCheckInChI(true);
			tautomerManager.FlagCalculateCACTVSEnergyRank = true;
			tautomerManager.FlagRegisterOnlyBestRankTautomers = true;
			tautomerManager.FlagSetStereoElementsOnTautomerProcess = true;
			/**
			 * solves https://github.com/cdk/cdk/issues/279
			 */
			tautomerManager.FlagAddImplicitHAtomsOnTautomerProcess = false;
			//tautomerManager.getKnowledgeBase().use17ShiftRules(false);
			//tautomerManager.f
			
			tautomerManager.setStructure(mol);
			

			List<IAtomContainer> resultTautomers = tautomerManager.generateTautomersIncrementaly();
			System.out.println(resultTautomers.size());
			for (int i = 0; i < resultTautomers.size(); i++) {
				IAtomContainer a = resultTautomers.get(i);
				String ss = null;
				try {
					ss = SmilesGenerator.isomeric().create(a);
				} catch (Exception x) {
					ss=x.getMessage();
				} finally {
					System.out.println(String.format("%d. %s", i + 1, ss));
				}
			}
		}

		// testSmiles(smiles);

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
		st.setImplicitHydrogens(false);
		st.setNeutralise(false);
		st.setSplitFragments(false);

		st.setClearIsotopes(false);
		SmilesGenerator g = SmilesGenerator.absolute();
		for (String smi : smiles) {
			IAtomContainer mol = sp.parseSmiles(smi);
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

			for (IAtom atom : mol.atoms())
				System.out.println(atom.getImplicitHydrogenCount());
			/*
			 * IAtomContainer tautomer = p.process(mol); for (IBond b :
			 * tautomer.bonds()) { Assert.assertNotSame(IBond.Order.UNSET,
			 * b.getOrder()); }
			 */

			IAtomContainer smol = st.process(mol);

			for (IBond b : smol.bonds()) {
				Assert.assertNotSame(IBond.Order.UNSET, b.getOrder());
			}
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			for (IAtom atom : mol.atoms())
				if (atom.getAtomTypeName().indexOf("S") >= 0)
					System.out.print(atom.getAtomTypeName() + "\t");
			System.out.println(String.format("\n%s\t-->\t%s", smi, g.create(smol)));
			Assert.assertFalse(smol.getProperties().get("http://www.opentox.org/api/1.1#InChI").toString()
					.startsWith("InChI=1S/"));
		}
	}
}
