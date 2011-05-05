package ambit2.namestructure.test;

import java.io.IOException;
import java.io.OutputStream;

import junit.framework.Assert;

import nu.xom.Serializer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.templates.MoleculeFactory;

import uk.ac.cam.ch.wwmm.opsin.NameToInchi;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult.OPSIN_RESULT_STATUS;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
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
	
	
	@Test
	public void testSMILES() throws Exception {
		 NameToStructure nts = NameToStructure.getInstance();
		 String smiles = nts.parseToSmiles("benzene");
		 Assert.assertEquals("C1=CC=CC=C1",smiles);
	}	
	@Test
	public void testCML() throws Exception {
		Name2StructureProcessor p = new Name2StructureProcessor();
	
		OpsinResult result = p.name2structure("benzene");

		Assert.assertEquals(OPSIN_RESULT_STATUS.SUCCESS,result.getStatus()) ;
		
		System.out.println(result.getCml().toXML());

	}
	
}
