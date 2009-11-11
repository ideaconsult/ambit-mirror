package ambit2.smarts.processors;

import java.util.BitSet;

import org.junit.Test;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.core.processors.structure.AtomConfigurator;

public class StructureKeysBitSetGeneratorTest {
	@Test
	public void test() throws Exception {
		SmilesParser p = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		AtomConfigurator a = new AtomConfigurator();
		
		IAtomContainer c = a.process(p.parseSmiles("c1ccccc1"));
		CDKHueckelAromaticityDetector d = new CDKHueckelAromaticityDetector();
		d.detectAromaticity(c);
		StructureKeysBitSetGenerator g = new StructureKeysBitSetGenerator();
		BitSet bs = g.process(c);
		System.out.println(bs);
		c = a.process(p.parseSmiles("C1=CC=CC=1"));
		d.detectAromaticity(c);
		bs = g.process(c);
		System.out.println(bs);
	}
}
