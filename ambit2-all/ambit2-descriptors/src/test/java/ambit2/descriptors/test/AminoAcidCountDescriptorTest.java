package ambit2.descriptors.test;

import java.net.URL;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.AminoAcidCountDescriptor;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.AminoAcids;

import ambit2.descriptors.processors.test.MoleculeFactory;
import junit.framework.Assert;


public class AminoAcidCountDescriptorTest {

	@Test
	public void testProcess() throws Exception {
		 IAminoAcid[] aas = AminoAcids.createAAs();
		 Assert.assertEquals(20,aas.length);
		 
		URL url = MoleculeFactory.class.getClassLoader().getResource(
     			"org/openscience/cdk/templates/data/list_aminoacids.cml"
     	)	;
		AminoAcidCountDescriptor group = new AminoAcidCountDescriptor();

		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer m = p.parseSmiles("O=C(O)[C@@H](N)C"); //alanine
		DescriptorValue result = group.calculate(m);
		Assert.assertEquals(1,((IntegerArrayResult)result.getValue()).get(0));

	}

}
