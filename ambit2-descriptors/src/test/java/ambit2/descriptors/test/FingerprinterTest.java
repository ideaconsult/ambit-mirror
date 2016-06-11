package ambit2.descriptors.test;

import java.util.BitSet;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.fingerprint.CircularFingerprinter;
import org.openscience.cdk.fingerprint.IBitFingerprint;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.fingerprint.PubchemFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.MoleculeTools;
import ambit2.core.data.StringDescriptorResultType;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.descriptors.fingerprints.EStateFingerprinterWrapper;
import ambit2.descriptors.fingerprints.ExtendedFingerprinterWrapper;
import ambit2.descriptors.fingerprints.Fingerprint2DescriptorWrapper;
import ambit2.descriptors.fingerprints.HybridizationFingerprinterWrapper;
import ambit2.descriptors.fingerprints.KlekotaRothFingerprinterWrapper;
import ambit2.descriptors.fingerprints.MACCSFingerprinterWrapper;
import ambit2.descriptors.fingerprints.PubChemFingerprinterWrapper;
import ambit2.descriptors.fingerprints.SubstructureFingerprinterWrapper;

public class FingerprinterTest {
	@Test
	public void testEStateFP() throws Exception {

		testDescriptor(new EStateFingerprinterWrapper());
	}

	@Test
	public void testPubchemFP() throws Exception {

		testDescriptor(new PubChemFingerprinterWrapper());
	}

	@Test
	public void testHybridizationFP() throws Exception {

		testDescriptor(new HybridizationFingerprinterWrapper());
	}

	@Test
	public void testExtendedFP() throws Exception {

		testDescriptor(new ExtendedFingerprinterWrapper());
	}

	@Test
	public void testSubstructureFP() throws Exception {

		testDescriptor(new SubstructureFingerprinterWrapper());
	}

	@Test
	public void testMACCSFP() throws Exception {

		testDescriptor(new MACCSFingerprinterWrapper());
	}

	@Test
	public void testKlekotaRothFP() throws Exception {

		testDescriptor(new KlekotaRothFingerprinterWrapper());
	}

	@Test
	public void testPubChem() throws Exception {
		IAtomContainer mol = MoleculeFactory.make123Triazole();
		IFingerprinter fp = new PubchemFingerprinter(SilentChemObjectBuilder.getInstance());
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		CDKHydrogenAdder h = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		h.addImplicitHydrogens(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		BitSet bs1 = fp.getBitFingerprint(mol).asBitSet();
		BitSet bs2 = fp.getBitFingerprint(mol).asBitSet();
		// fails if aromaticity was not detected!
		Assert.assertEquals(bs1, bs2);

	}

	public void testDescriptor(Fingerprint2DescriptorWrapper wrapper)
			throws Exception {

		IAtomContainer mol = MoleculeFactory.make123Triazole();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		CDKHydrogenAdder adder = CDKHydrogenAdder
				.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(mol);
		MoleculeTools.convertImplicitToExplicitHydrogens(mol);

		IFingerprinter fp = wrapper.getFingerprinter();
		BitSet bs = fp.getBitFingerprint(mol).asBitSet();

		DescriptorValue result = wrapper.calculate(mol);
		Assert.assertEquals(bs.toString(),
				((StringDescriptorResultType) result.getValue()).getValue());
		Assert.assertEquals(fp.getClass().getName(), result.getNames()[0]);
		Assert.assertEquals(wrapper.getClass().getName(), result
				.getSpecification().getImplementationIdentifier());

	}
	

	@Test
	public void test_fp() throws Exception {
		String[] smiles = new String[] { "O(CC(NCC1=C(C=C(C=C1)C)C)C)C",
				"O(CC(NCC=1C(=CC(=CC1)C)C)C)C" };
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		SmilesGenerator absolute_aromatic = SmilesGenerator.absolute()
				.aromatic();
		SmilesGenerator isomeric_aromatic = SmilesGenerator.isomeric()
				.aromatic();

		CircularFingerprinter fp = new CircularFingerprinter();
		BitSet[] bs = new BitSet[] {null,null};
		for (int i=0; i < smiles.length; i++) {
			
			IAtomContainer mol = p.parseSmiles(smiles[i]);
			//AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			//CDKHueckelAromaticityDetector.detectAromaticity(mol);

			IBitFingerprint cf = fp.getBitFingerprint(mol);
			bs[i] = cf.asBitSet();
			String newsmiles = absolute_aromatic.create(mol);
			Assert.assertEquals(1.0f, Tanimoto.calculate(bs[i],fp.getBitFingerprint(p.parseSmiles(newsmiles)).asBitSet()));
			String newsmiles_i = isomeric_aromatic.create(mol);
			Assert.assertEquals(1.0f, Tanimoto.calculate(bs[i],fp.getBitFingerprint(p.parseSmiles(newsmiles_i)).asBitSet()));
		}
		Assert.assertEquals(1.0f, Tanimoto.calculate(bs[0],bs[1]));
	}

}
