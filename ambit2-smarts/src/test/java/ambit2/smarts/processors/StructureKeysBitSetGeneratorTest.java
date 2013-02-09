package ambit2.smarts.processors;

import java.io.InputStreamReader;
import java.util.BitSet;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.smarts.query.ISmartsPattern;
import ambit2.smarts.query.SmartsPatternFactory;
import ambit2.smarts.query.SmartsPatternFactory.SmartsParser;

public class StructureKeysBitSetGeneratorTest {
	protected static Logger logger = Logger.getLogger(StructureKeysBitSetGeneratorTest.class.getName());
	
	@Test
	public void test() throws Exception {
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
	
		IAtomContainer c = p.parseSmiles("c1ccccc1");

		StructureKeysBitSetGenerator g = new StructureKeysBitSetGenerator();
		BitSet bs = g.process(c);
		IAtomContainer c1 = p.parseSmiles("C1=CC=CC=C1");
		BitSet bs1 = g.process(c1);
		Assert.assertEquals(bs,bs1);
		CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
        hAdder.addImplicitHydrogens(c1);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(c1);

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
	@Test
	public void testBiphenyl4CDK() throws Exception {
		IteratingMDLReader reader = new IteratingMDLReader(
				new InputStreamReader(getClass().getClassLoader().getResourceAsStream("biphenyl.sdf")),
				SilentChemObjectBuilder.getInstance());
		
		IMolecule biphenyl_kekule=null;
		while (reader.hasNext()) {
			IChemObject o = reader.next();
			Assert.assertTrue(o instanceof IMolecule);
			biphenyl_kekule = (IMolecule) o;
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(biphenyl_kekule);
			//well, Hydrogens are already in the file, but we need to mimic the generic read/processing workflow
			CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(biphenyl_kekule.getBuilder());
			hAdder.addImplicitHydrogens(biphenyl_kekule);
			CDKHueckelAromaticityDetector.detectAromaticity(biphenyl_kekule);
			AtomContainerManipulator.convertImplicitToExplicitHydrogens(biphenyl_kekule);
			biphenyl_kekule.setID("biphenyl_kekule");
			
			break;

		}
		reader.close();
		Assert.assertNotNull(biphenyl_kekule);
		
		//get the biphenyl as aromatic smiles
		SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IMolecule biphenyl_aromaticsmiles = parser.parseSmiles("c1ccccc1c2ccccc2");
		biphenyl_aromaticsmiles.setID("biphenyl_aromatic");
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(biphenyl_aromaticsmiles);
		CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(biphenyl_aromaticsmiles.getBuilder());
		hAdder.addImplicitHydrogens(biphenyl_aromaticsmiles);
		CDKHueckelAromaticityDetector.detectAromaticity(biphenyl_aromaticsmiles);		
		AtomContainerManipulator.convertImplicitToExplicitHydrogens(biphenyl_aromaticsmiles);
		
		
		//get the biphenyl as Kekule smiles
		IMolecule biphenyl_kekulesmiles = parser.parseSmiles("C1=C(C=CC=C1)C2=CC=CC=C2");
		biphenyl_kekulesmiles.setID("biphenyl_kekulesmiles");
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(biphenyl_kekulesmiles);
		hAdder = CDKHydrogenAdder.getInstance(biphenyl_kekulesmiles.getBuilder());
		hAdder.addImplicitHydrogens(biphenyl_kekulesmiles);
		CDKHueckelAromaticityDetector.detectAromaticity(biphenyl_kekulesmiles);		
		AtomContainerManipulator.convertImplicitToExplicitHydrogens(biphenyl_kekulesmiles);
		

		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(biphenyl_aromaticsmiles,biphenyl_kekule));
		
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(biphenyl_aromaticsmiles,biphenyl_kekulesmiles));
		
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(biphenyl_kekulesmiles,biphenyl_kekule));
		
		
		// #1 with the latest fix, we'll not find double bonds :)
		ISmartsPattern smartsPattern_ambit = SmartsPatternFactory.createSmartsPattern(SmartsParser.smarts_nk, "[#6]=[#6]", false);
		Assert.assertTrue(smartsPattern_ambit.match(biphenyl_aromaticsmiles)==0);
		Assert.assertTrue(smartsPattern_ambit.match(biphenyl_kekule)==0);
		Assert.assertTrue(smartsPattern_ambit.match(biphenyl_kekulesmiles)==0);	
			
		// #1 with the latest fix, we'll not find double bonds :)
		ISmartsPattern smartsPattern_fast = SmartsPatternFactory.createSmartsPattern(SmartsParser.smarts_fast, "[#6]=[#6]", false);
		Assert.assertTrue(smartsPattern_fast.match(biphenyl_aromaticsmiles)==0);
		Assert.assertTrue(smartsPattern_fast.match(biphenyl_kekule)==0);	
		Assert.assertTrue(smartsPattern_fast.match(biphenyl_kekulesmiles)==0);
		
		// #3 cdk does find double bonds in kekule representation
		ISmartsPattern smartsPattern_cdk = SmartsPatternFactory.createSmartsPattern(SmartsParser.smarts_cdk, "[#6]=[#6]", false);
		Assert.assertTrue(smartsPattern_cdk.match(biphenyl_aromaticsmiles)==0);
		//this is fixed in cdk 1.4.4
	    Assert.assertTrue(smartsPattern_cdk.match(biphenyl_kekule)==0);
	    Assert.assertTrue(smartsPattern_cdk.match(biphenyl_kekulesmiles)==0);
		
		
		//same as #3 but using CDK code only
		SMARTSQueryTool sqt = new SMARTSQueryTool("[#6]=[#6]");
		//there is no any double bond in biphenyl, created via aromatic smiles
		//Fixed in cdk 1.4.4
		Assert.assertFalse(sqt.matches(biphenyl_aromaticsmiles));
		//there is at least one :) double bond in the biphenyl , read from SDF
		Assert.assertFalse(sqt.matches(biphenyl_kekule));
		//there is at least one :) double bond in the biphenyl , read from SDF
		Assert.assertFalse(sqt.matches(biphenyl_kekulesmiles));
	}
	/*
	protected void printBonds(IAtomContainer mol) {
		System.out.println(mol.getID());
		for (IBond bond: mol.bonds()) {
			if ("H".equals(bond.getAtom(0).getSymbol()) || "H".equals(bond.getAtom(1).getSymbol())) continue;
			System.out.println(String.format("%s %s",
						bond.getOrder(),bond.getFlag(CDKConstants.ISAROMATIC)));
		}
	}
	*/
	

	@Test
	public void testBiphenylKeys_kekuleBondscleaned() throws Exception {
		testBiphenylKeys(true);
	}
	@Test
	public void testBiphenylKeys_kekuleBondsNOTcleaned() throws Exception {
		testBiphenylKeys(false);
	}	

	public void testBiphenylKeys(boolean cleanKekuleBonds) throws Exception {		
		MoleculeReader mr = new MoleculeReader();
		AtomConfigurator config = new AtomConfigurator();

		RawIteratingSDFReader reader = new RawIteratingSDFReader(
				new InputStreamReader(getClass().getClassLoader().getResourceAsStream("biphenyl.sdf")));
		
		BitSet bitsetKekule = null;
		StructureKeysBitSetGenerator bitsetGenerator = new StructureKeysBitSetGenerator();
		bitsetGenerator.setCleanKekuleBonds(cleanKekuleBonds);
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			IAtomContainer c = config.process(mr.process(record));
			bitsetKekule = bitsetGenerator.process(c);
		}
		reader.close();
		
		SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IMolecule mol = parser.parseSmiles("c1ccccc1c2ccccc2");
	
		BitSet bitsetAromatic = bitsetGenerator.process(mol);
		
		Assert.assertEquals(bitsetKekule,bitsetAromatic);
	}
	
	@Test
	public void testTriazoleKeys_kekuleBondscleaned() throws Exception {
		testTriazoleKeys(true);
	}
	@Test
	public void testTriazoleKeys_kekuleBondsNOTcleaned() throws Exception {
		testTriazoleKeys(false);
	}	
	/**
	 * aromatic :<{113, 852}>
	 * 113: c(c)n
	 * 852: c(cn)n
	 * sdf 
	 * 8: N, 
	 * 12 CC, 
	 * 22 NC, 
	 * 27 C(C)N, 
	 * 71 C, 
	 * 444 C(CN)N, 
	 * 911 NN
	 * @param cleanKekuleBonds
	 * @throws Exception
	 */
	public void testTriazoleKeys(boolean cleanKekuleBonds) throws Exception {		
		MoleculeReader mr = new MoleculeReader();
		AtomConfigurator config = new AtomConfigurator();

		RawIteratingSDFReader reader = new RawIteratingSDFReader(
				new InputStreamReader(getClass().getClassLoader().getResourceAsStream("triazole.sdf")));
		
		BitSet bitsetKekule = null;
		StructureKeysBitSetGenerator bitsetGenerator = new StructureKeysBitSetGenerator();
		bitsetGenerator.setCleanKekuleBonds(cleanKekuleBonds);
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			IAtomContainer c = config.process(mr.process(record));
			//CDKHueckelAromaticityDetector.detectAromaticity(c);
			bitsetKekule = bitsetGenerator.process(c);
			logger.fine(bitsetKekule.toString());
		}
		reader.close();
		
		SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer mol = parser.parseSmiles("c1cnn[nH]1");
		for (IBond b: mol.bonds()) logger.fine(Boolean.toString(b.getFlag(CDKConstants.ISAROMATIC)));
		mol = config.process(mol);
		//CDKHueckelAromaticityDetector.detectAromaticity(mol);
		for (IBond b: mol.bonds()) logger.fine(Boolean.toString(b.getFlag(CDKConstants.ISAROMATIC)));
		BitSet bitsetAromatic = bitsetGenerator.process(mol);
		
		logger.fine(bitsetAromatic.toString());
		Assert.assertEquals(bitsetKekule,bitsetAromatic);
		
		parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		mol = parser.parseSmiles("C1=CN=NN1");
		for (IBond b: mol.bonds()) logger.fine(Boolean.toString(b.getFlag(CDKConstants.ISAROMATIC)));
		mol = config.process(mol);
		//CDKHueckelAromaticityDetector.detectAromaticity(mol);
		for (IBond b: mol.bonds()) logger.fine(Boolean.toString(b.getFlag(CDKConstants.ISAROMATIC)));
		BitSet bitsetKekuleSMILES = bitsetGenerator.process(mol);
		
		logger.fine(bitsetKekuleSMILES.toString());
		Assert.assertEquals(bitsetKekule,bitsetKekuleSMILES);
		
	}
	@Test
	public void parseTriazole() throws Exception {
		SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer mol = parser.parseSmiles("c1cnn[nH]1"); //C1=CN=NN1
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		for (IBond b: mol.bonds()) logger.fine(Boolean.toString(b.getFlag(CDKConstants.ISAROMATIC)));
		for (IAtom a: mol.atoms()) logger.fine(Boolean.toString(a.getFlag(CDKConstants.ISAROMATIC)));
	}
}
