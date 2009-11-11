package ambit2.smarts.processors;

import java.util.BitSet;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.HydrogenAdder;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.MoleculeReader;

public class StructureKeysBitSetGeneratorTest {
	@Test
	public void test() throws Exception {
		SmilesParser p = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
	
		IAtomContainer c = p.parseSmiles("c1ccccc1");

		StructureKeysBitSetGenerator g = new StructureKeysBitSetGenerator();
		BitSet bs = g.process(c);
		IAtomContainer c1 = p.parseSmiles("C1=CC=CC=C1");
		BitSet bs1 = g.process(c1);
		Assert.assertEquals(bs,bs1);
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(c1);
		BitSet bs2 = g.process(c1);
		Assert.assertEquals(bs2,bs1);
		
		IStructureRecord record = new StructureRecord(-1,-1,getBenzene(),MOL_TYPE.SDF.toString());
		MoleculeReader reader = new MoleculeReader();
		IAtomContainer mol = reader.process(record);
		
		
		BitSet bs3 = g.process(mol);
		Assert.assertEquals(bs,bs3);
	}
	
	protected String getBenzene() {
		return
	
	"c1ccccc1\n"+
	"  CDK    11/11/09,17:19\n"+
	"	\n"+
	" 12 12  0  0  0  0  0  0  0  0999 V2000\n"+
	"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
	"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
	"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
	"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
	"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
	"    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
	"    0.0000    0.0000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n"+
	"    0.0000    0.0000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n"+
	"    0.0000    0.0000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n"+
	"    0.0000    0.0000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n"+
	"    0.0000    0.0000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n"+
	"    0.0000    0.0000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n"+
	"  1  6  2  0  0  0  0 \n"+
	"  1  2  1  0  0  0  0 \n"+
	"  1  7  1  0  0  0  0 \n"+
	"  2  3  2  0  0  0  0 \n"+
	"  2  8  1  0  0  0  0 \n"+
	"  3  4  1  0  0  0  0 \n"+
	"  3  9  1  0  0  0  0 \n"+
	"  4  5  2  0  0  0  0 \n"+
	"  4 10  1  0  0  0  0 \n"+
	"  5  6  1  0  0  0  0 \n"+
	"  5 11  1  0  0  0  0 \n"+
	"  6 12  1  0  0  0  0 \n"+
	"M  END\n";
	}
}
