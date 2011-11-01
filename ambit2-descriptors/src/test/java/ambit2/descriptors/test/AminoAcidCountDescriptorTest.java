package ambit2.descriptors.test;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.AminoAcidCountDescriptor;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.AminoAcids;
import org.openscience.cdk.templates.MoleculeFactory;

public class AminoAcidCountDescriptorTest {

	@Test
	public void testProcess() throws Exception {
		 IAminoAcid[] aas = AminoAcids.createAAs();
		 System.out.println(aas.length);
		 
		URL url = MoleculeFactory.class.getClassLoader().getResource(
     			"org/openscience/cdk/templates/data/list_aminoacids.cml"
     	)	;
		AminoAcidCountDescriptor group = new AminoAcidCountDescriptor();

		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IMolecule m = p.parseSmiles("O=C(O)[C@@H](N)C"); //alanine
		DescriptorValue result = group.calculate(m);
		Assert.assertEquals(1,((IntegerArrayResult)result.getValue()).get(0));
		/*
		m = p.parseSmiles("CCCOC(=O)CC(C1=CC=C(O)C=C1)C=2C=CC=3NC=4C(=CC=CC=4(C=3(C=2)))C%13CCNC%13(COCCC(COCC=5C=C(O)C=C(C=5)C(C#N)COCC=6C=CC=C(C=6)CC(CCO)C(CC(=O)CC=9C=C(COCC(CC)C7CC(OC(C)=O)SC7)C=C8C=CC=CC8=9)CC%11CC%12=CC=C(CCSCC%10CSCC%10(CSCC=O))C=C%12(N%11))C(=O)O)");
		result = group.calculate(m);
		System.out.println(result.getValue());
		*/
	}

}
