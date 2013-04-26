package ambit2.descriptors.test;

import java.util.BitSet;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.fingerprint.PubchemFingerprinter;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.StringDescriptorResultType;
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
		IMolecule mol = MoleculeFactory.make123Triazole();
		IFingerprinter fp = new PubchemFingerprinter();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		BitSet bs1 = fp.getFingerprint(mol);
		BitSet bs2 = fp.getFingerprint(mol);
		//fails if aromaticity was not detected!
		Assert.assertEquals(bs1,bs2);
		
	}
	public void testDescriptor(Fingerprint2DescriptorWrapper wrapper) throws Exception {

		
		IMolecule mol = MoleculeFactory.make123Triazole();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(mol);
		AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
		
		IFingerprinter fp = wrapper.getFingerprinter();
		BitSet bs = fp.getFingerprint(mol);
		
		DescriptorValue result = wrapper.calculate(mol);
		Assert.assertEquals(bs.toString(),((StringDescriptorResultType)result.getValue()).getValue());
		Assert.assertEquals(fp.getClass().getName(),result.getNames()[0]);
		Assert.assertEquals(wrapper.getClass().getName(),result.getSpecification().getImplementationIdentifier());

	}
}
