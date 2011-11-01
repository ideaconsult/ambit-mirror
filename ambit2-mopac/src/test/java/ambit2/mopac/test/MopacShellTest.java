package ambit2.mopac.test;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.Property;
import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.base.log.AmbitLogger;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.core.smiles.SmilesParserWrapper.SMILES_PARSER;
import ambit2.mopac.MopacShell;

public class MopacShellTest {
	protected SmilesParserWrapper parser;
	protected MopacShell shell;
	@Before
	public void setUp() throws Exception {
		shell = new MopacShell();
		AmbitLogger.configureLog4j(true);
		File homeDir = new File(System.getProperty("user.home") +"/.ambit2/*");
		homeDir.delete();
		
		parser =  SmilesParserWrapper.getInstance(SMILES_PARSER.CDK);		
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

	@Test
	public void getExecutableWin()  throws Exception {
		String exec = shell.getExecutable(CommandShell.os_WINDOWS);
		//Assert.assertEquals(name,exec);
		File file = new File(exec);
//		System.out.println(file.getAbsolutePath());
		Assert.assertTrue(file.exists());
	}

	@Test
	public void testSimple() throws Exception {
		runShell("C=C");
	}
	@Test
	public void testLarger() throws Exception {
		runShell("[H]C1=C([H])C([H])=C([H])C([H])=C1([H])");
	}
	@Test
	public void testDisconnected() throws Exception {
		shell.setErrorIfDisconnected(false);
		runShell("CCC.CCC");
	}	
	public void runShell(String smiles) throws Exception {
		//"[H]C1=C([H])C([H])=C([H])C([H])=C1([H])";
		IMolecule mol = parser.parseSmiles(smiles); 
		mol.setProperty("SMILES",smiles);
		mol.setProperty("TITLE",smiles);

		IMolecule newmol = (IMolecule)shell.runShell(mol);
		//Assert.assertEquals(mol.getAtomCount(),newmol.getAtomCount());
		//Assert.assertEquals(mol.getBondCount(),newmol.getBondCount());
		Assert.assertNotNull(newmol.getProperty("ELUMO"));
		Assert.assertNotNull(newmol.getProperty("EHOMO"));
		Assert.assertNotNull(newmol.getProperty("ELECTRONIC ENERGY"));
		
		for (int i=0; i < newmol.getAtomCount(); i++) {
			Assert.assertNotNull(newmol.getAtom(i).getPoint3d());
		}
		SmilesGenerator g = new SmilesGenerator(true);
		//String newsmiles = g.createSMILES(newmol);
		//assertEquals(smiles,newsmiles);
		//isisomorph returns false if createSmiles was not run before; perhaps smth to do with atom types configuration
		
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(newmol);
        
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
        adder.addImplicitHydrogens(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        

        
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(mol,newmol));			
	}

	@Test
	public void testCalculateUnsupportedAtom() throws Exception {

		try {
			runShell("C[Si]");
			Assert.fail("Shouldn't get here");
		} catch (ShellException x) {
			Assert.assertEquals(MopacShell.MESSAGE_UNSUPPORTED_TYPE + "Si",x.getMessage());
		}
	}

	@Test
	   public void testPredictions() throws Exception {
		shell.setMopac_commands("AM1 NOINTER NOMM BONDS MULLIK PRECISE GNORM=0.0");
    	InputStream in = MopacShell.class.getClassLoader().getResourceAsStream("ambit2/mopac/qsar6train.csv");
    	//DelimitedFileWriter writer = new DelimitedFileWriter(new FileOutputStream(file));
    	IteratingDelimitedFileReader reader = new IteratingDelimitedFileReader(in);
    	double r2 = 0;
    	double n = 0;
    	
    	double sxy = 0;
    	double sx = 0;
    	double sy = 0;
    	double sx2 = 0;
    	double sy2 = 0;
    	while (reader.hasNext()) {
    		Object o = reader.next();
    		Assert.assertTrue(o instanceof IAtomContainer);
    		
    		IAtomContainer a = (IAtomContainer)o;
        	AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(a);
            CDKHueckelAromaticityDetector.detectAromaticity(a); 

            
            IAtomContainer mol = shell.runShell(a);
            double x = Double.parseDouble(mol.getProperty("ELUMO").toString());
            Iterator i = a.getProperties().keySet().iterator();
            while (i.hasNext()) {
            	Property oo = (Property)i.next();
            	if ("LUMO (eV) AM1_MOPAC 4.10".equals(oo.getName())) {
                    double y = Double.parseDouble(a.getProperty(oo).toString());
                    double error = x - y;
                    r2 += error*error;
                    n++;
                    
                    sxy += x*y;
                    sx += x;
                    sy += y;
                    sx2 += x*x;
                    sy2 += y*y;                    
            		break;
            	}
            }
            
            
           /*
            System.out.print(a.getAtomCount());
            System.out.print(',');
            System.out.print(mol.getAtomCount());
            System.out.print(',');
            System.out.print(x);
            System.out.print(',');
            System.out.print(y);
           System.out.print(',');
            System.out.print(error);
            System.out.println();
            */
                     


    	}
    	in.close();
    	
    	//calculate Pearson correlation coefficient
    	double r= (n*sxy-sx*sy)/(Math.sqrt(n*sx2-sx*sx)*Math.sqrt(n*sy2-sy*sy));
    	//System.out.println(r);
    	//System.out.println(r*r);
    	Assert.assertTrue(Math.abs(r) > 0.96);
    	/*
    	 * ehomo
r=0.9736302412992341
r^2=0.9479558467724049
elumo
r=0.9974458630252169
r^2=0.9948982496661197

    	 */
    	
    }
}

