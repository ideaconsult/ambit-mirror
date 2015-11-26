package ambit2.descriptors.test;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.BCUTDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.descriptors.MolecularWeight;
import ambit2.descriptors.processors.DescriptorResultFormatter;

public class MolecularWeightDescriptorTest {
	@Test
	public void test() throws Exception {
		MolecularWeight mw = new MolecularWeight();
		IAtomContainer mol = MoleculeFactory.makeBenzene();
		HydrogenAdderProcessor p = new HydrogenAdderProcessor();
		
		DescriptorValue value = mw.calculate(p.process(mol));
		Assert.assertEquals(78.111f,((DoubleResult) value.getValue()).doubleValue(),1E-3);
		DescriptorResultFormatter f = new DescriptorResultFormatter(false);
		Assert.assertEquals(78.111, ((Double)f.process(value)).doubleValue(),1E-3);
		f = new DescriptorResultFormatter(true);
		f.setLocale(Locale.US);
		Assert.assertEquals("78.111", f.process(value));		
	}
	
	//this will hang!
	public void testBCUT() throws Exception {
		BCUTDescriptor mw = new BCUTDescriptor();
		SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
		//IAtomContainer mol = sp.parseSmiles("O[As](O)O");
		IAtomContainer mol = sp.parseSmiles("O1[As]2O[As]1O2");
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		HydrogenAdderProcessor p = new HydrogenAdderProcessor();
		
		DescriptorValue value = mw.calculate(p.process(mol));
		System.out.println(value.getValue());
	}
}
