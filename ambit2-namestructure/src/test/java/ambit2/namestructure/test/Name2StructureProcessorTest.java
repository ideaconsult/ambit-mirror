package ambit2.namestructure.test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.templates.MoleculeFactory;

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
	public void test() throws Exception {
		Name2StructureProcessor p = new Name2StructureProcessor();
		IMolecule mol =  (IMolecule)p.process("benzene");
		Assert.assertNotNull(mol);
		IMolecule benzene = MoleculeFactory.makeBenzene();
		HydrogenAdderProcessor hp = new HydrogenAdderProcessor();
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(hp.process(benzene),mol));
	}
}
