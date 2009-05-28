package ambit2.descriptors.test;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.descriptors.MolecularWeight;
import ambit2.descriptors.processors.DescriptorResultFormatter;

public class MolecularWeightDescriptorTest {
	@Test
	public void test() throws Exception {
		MolecularWeight mw = new MolecularWeight();
		IMolecule mol = MoleculeFactory.makeBenzene();
		HydrogenAdderProcessor p = new HydrogenAdderProcessor();
		
		DescriptorValue value = mw.calculate(p.process(mol));
		Assert.assertEquals(78.111f,((DoubleResult) value.getValue()).doubleValue(),1E-3);
		DescriptorResultFormatter f = new DescriptorResultFormatter(false);
		Assert.assertEquals(78.111, ((Double)f.process(value)).doubleValue(),1E-3);
		f = new DescriptorResultFormatter(true);
		Assert.assertEquals("78.111", f.process(value));		
	}
}
