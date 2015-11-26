package ambit2.namestructure.test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import uk.ac.cam.ch.wwmm.opsin.NameToInchi;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult.OPSIN_RESULT_STATUS;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.namestructure.Name2StructureProcessor;

public class Name2StructureProcessorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIUPACName() throws Exception {
		Name2StructureProcessor p = new Name2StructureProcessor();
		IAtomContainer mol = (IAtomContainer) p
				.process("2-[(3Z)-6-fluoro-2-methyl-3-[(4-methylsulfinylphenyl)methylidene]inden-1-yl]acetic acid");
		Assert.assertNotNull(mol);
		Assert.assertTrue(mol.getAtomCount() > 0);
	}

	@Test
	public void test() throws Exception {
		Name2StructureProcessor p = new Name2StructureProcessor();
		IAtomContainer mol = (IAtomContainer) p.process("benzene");
		Assert.assertNotNull(mol);

		IAtomContainer benzene = MoleculeFactory.makeBenzene();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(benzene);
		CDKHueckelAromaticityDetector.detectAromaticity(benzene);
		HydrogenAdderProcessor hp = new HydrogenAdderProcessor();
		UniversalIsomorphismTester uit = new UniversalIsomorphismTester();
		Assert.assertTrue(uit.isIsomorph(hp.process(benzene), mol));
	}

	@Test
	public void testSMILES() throws Exception {
		NameToStructure nts = NameToStructure.getInstance();
		String smiles = nts.parseToSmiles("benzene");
		Assert.assertEquals("C1=CC=CC=C1", smiles);

	}

	@Test
	public void testCML() throws Exception {
		Name2StructureProcessor p = new Name2StructureProcessor();

		OpsinResult result = p.name2structure("benzene");

		Assert.assertEquals(OPSIN_RESULT_STATUS.SUCCESS, result.getStatus());

		System.out.println(result.getCml());

	}

	@Test
	public void testNitroGroup() throws Exception {
		Name2StructureProcessor p = new Name2StructureProcessor();

		OpsinResult result = p.name2structure("(2-Butyl-5-nitro-1-benzofuran-3-yl)(4-hydroxyphenyl)methanone");

		Assert.assertEquals(OPSIN_RESULT_STATUS.SUCCESS, result.getStatus());

		// System.out.println(result.getCml().toXML());
		String inchi = NameToInchi.convertResultToInChI(result);
		// System.out.println(inchi);
		Assert.assertEquals("C(CCC)C=1OC2=C(C1C(=O)C1=CC=C(C=C1)O)C=C(C=C2)[N+](=O)[O-]", result.getSmiles());

	}

	@Test
	public void testFailed() throws Exception {
		String[] names = new String[] {
				"cis-1,2,3,5,6,7,8,8a-octahydro-1,8a-dimethyl-7-(1-methylethylidene)naphthalene",
				"Polydimethylsiloxane",
				"Poly(imino(1-oxo-1,12-dodecanediyl))",
				"Polyoxymethylene",
				"Poly(iminocarbonimidoyliminocarbonimidoylimino-1,6-hexanediyl), hydrochloride",
				"polydimethylsiloxane",
				"Polysiloxane",
				"Poly[[6-[(1,1,3,3-tetramethylbutyl)amino]-s-triazine-2,4-diyl]-[(2,2,6,6-tetramethyl-4-piperidyl)imino]-hexamethylene-[(2,2,6,6-tetramethyl-4-piperidyl)imino]]",
				"Polypropylene", "Polyoxyethylene monooleate", "POLYOXYPROPYLENE POLYOXYETHYLENE ETHYLENEDIAMINE",
				"polysiloxane", "Polydimethyl-siloxane", "polyoxyethylene tristyrylphenyl ether",
				"Poly(iminocarbonimidoyliminocarbonimidoylimino-1,6-hexanediyl)", "polybutylene terephthalate" };

		for (String name : names) {
			Name2StructureProcessor p = new Name2StructureProcessor();

			OpsinResult result = p.name2structure(name);
			System.out.print(String.format("%s %s ", result.getStatus(), name));
			if (result.getStatus() == OPSIN_RESULT_STATUS.SUCCESS) {
				String inchi = NameToInchi.convertResultToInChI(result);
				System.out.println(inchi == null ? "No inchi" : inchi);
				System.out.println(result.getSmiles());

			}

			System.out.println();
		}
	}

}
