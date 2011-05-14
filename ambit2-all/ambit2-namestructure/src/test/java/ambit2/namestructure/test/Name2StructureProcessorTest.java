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
		"Polypropylene",
		"Polyoxyethylene monooleate",
		"POLYOXYPROPYLENE POLYOXYETHYLENE ETHYLENEDIAMINE",
		"polysiloxane",
		"Polydimethyl-siloxane",
		"polyoxyethylene tristyrylphenyl ether",
		"Poly(iminocarbonimidoyliminocarbonimidoylimino-1,6-hexanediyl)",
		"polybutylene terephthalate"
		};
		
		for (String name:names) {
			Name2StructureProcessor p = new Name2StructureProcessor();
			
			OpsinResult result = p.name2structure(name);
			System.out.print(String.format("%s %s ",result.getStatus(),name));
			if (result.getStatus()==OPSIN_RESULT_STATUS.SUCCESS) {
				String inchi = NameToInchi.convertResultToInChI(result);
				System.out.println(inchi==null?"No inchi":inchi);
				System.out.println(result.getSmiles());
				
				
			}
			
			System.out.println();
		}
	}
	
}
