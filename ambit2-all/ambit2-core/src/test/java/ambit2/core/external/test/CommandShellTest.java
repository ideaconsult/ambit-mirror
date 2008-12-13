package ambit2.core.external.test;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.core.external.CommandShell;
import ambit2.core.external.ShellSDFoutput;
import ambit2.core.log.AmbitLogger;
import ambit2.core.smiles.OpenBabelShell;
import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;

public class CommandShellTest {
	protected CommandShell shell;
	@Before
	public void setUp() throws Exception {
		shell = new ShellSDFoutput() {
			@Override
			protected Object transform(Object mol) {
				return mol;
			}
		};
		AmbitLogger.configureLog4j(true);
		File homeDir = new File(System.getProperty("user.home") +"/.ambit2/*");
		homeDir.delete();
		

	}
/*
	public void testGetInstance() throws Exception {
		shell = CommandShell.getInstance();
		assertNotNull(shell);
		CommandShell shell1 = CommandShell.getInstance();
		assertNotNull(shell1);
		assertEquals(shell,shell1);
	}
*/

/*
	@Test
	public void testRunSMI2SDF() throws Exception {
		ShellSmi2SDF smi2sdf = new ShellSmi2SDF();
		SmilesParserWrapper p =  SmilesParserWrapper.getInstance(SMILES_PARSER.CDK);
		String smiles = "Nc3ccc2c(ccc1ccccc12)c3";
		//"[H]C1=C([H])C([H])=C([H])C([H])=C1([H])";
		IMolecule mol = p.parseSmiles(smiles); 
		mol.setProperty("SMILES",smiles);
		mol.setProperty("TITLE",smiles);
		smi2sdf.setReadOutput(true);
		smi2sdf.setInputFile("smi2sdf_test.smi");		
		smi2sdf.setOutputFile("smi2sdf_test.sdf");
		IMolecule newmol = smi2sdf.runShell(mol);
		Assert.assertEquals(mol.getAtomCount(),newmol.getAtomCount());
		Assert.assertEquals(mol.getBondCount(),newmol.getBondCount());
		for (int i=0; i < newmol.getAtomCount(); i++) {
			Assert.assertNotNull(newmol.getAtom(i).getPoint3d());
		}
		SmilesGenerator g = new SmilesGenerator();
		String newsmiles = g.createSMILES(newmol);
		//assertEquals(smiles,newsmiles);
		//isisomorph returns false if createSmiles was not run before; perhaps smth to do with atom types configuration
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(mol,newmol));			
	}
	@Test	
	public void testRunMENGINE() throws Exception {
		ShellMengine mengine = new ShellMengine();
		IMolecule mol = MoleculeFactory.makeAlkane(3);
		mengine.runShell(mol);
	}
	@Test	
	public void testRunSMI23D() throws Exception {
		ShellSmi2SDF smi2sdf = new ShellSmi2SDF();
		IMolecule mol = MoleculeFactory.makeAlkane(3);
		smi2sdf.setOutputFile("smi23d_test.sdf");
		smi2sdf.runShell(mol);
		ShellMengine mengine = new ShellMengine();
		mengine.setInputFile("smi23d_test.sdf");
		mengine.setOutputFile("smi23d_test_opt.sdf");
		IMolecule newmol = mengine.runShell(mol);
		MFAnalyser mf = new MFAnalyser(newmol);
		IAtomContainer c = mf.removeHydrogensPreserveMultiplyBonded();
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(mol,c));
		for (int i=0; i < newmol.getAtomCount(); i++) {
			Assert.assertNotNull(newmol.getAtom(i).getPoint3d());
//			System.out.println(newmol.getAtom(i).getSymbol() + "\t" + newmol.getAtom(i).getPoint3d());
		}	

	}
*/
	@Test	
	public void testOpenBabel() throws Exception {
		String testfile = "babel_test.sdf";
		OpenBabelShell babel = new OpenBabelShell();
		babel.setOutputFile(testfile);
		IMolecule newmol = babel.runShell("c1ccccc1");
		MFAnalyser mf = new MFAnalyser(newmol);
		IAtomContainer c = mf.removeHydrogensPreserveMultiplyBonded();
		
		IMolecule mol = MoleculeFactory.makeBenzene();
		/*
		for (int i=0; i < newmol.getBondCount(); i++) {
			System.out.println(newmol.getBond(i).getOrder());
		}
		*/	
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(mol,c));
		new File(babel.getOutputFile()).delete();
	}	


}

