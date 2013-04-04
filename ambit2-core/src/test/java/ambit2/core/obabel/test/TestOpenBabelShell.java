package ambit2.core.obabel.test;


import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;
import ambit2.core.smiles.OpenBabelDepiction;
import ambit2.core.smiles.OpenBabelGen3D;
import ambit2.core.smiles.OpenBabelShell;


public class TestOpenBabelShell {

	protected static Logger logger = Logger.getLogger(TestOpenBabelShell.class.getName());
	
	@Before
	public void setUp() throws Exception {
        Logger tempLogger = logger;
        Level level = Level.ALL;
        while(tempLogger != null) {
           tempLogger.setLevel(level);
           for(Handler handler : tempLogger.getHandlers())
              handler.setLevel(level);
           tempLogger = tempLogger.getParent();
        }
	}
	
	@Test
	public void test() throws Exception {
		String osName = System.getProperty("os.name");
		if (CommandShell.os_WINDOWS.equals(osName) 
				|| CommandShell.os_WINDOWSVISTA.equals(osName)
				|| CommandShell.os_WINDOWS7.equals(osName)){
			OpenBabelShell ob = new OpenBabelShell();
			IAtomContainer mol = ob.process("c12-c3c(cccc3)Nc1nc(N)cc2");
			Assert.assertTrue(mol.getAtomCount()>0);
		}
	}
	
	@Test
	public void testImage() throws Exception {
		String osName = System.getProperty("os.name");
		if (CommandShell.os_WINDOWS.equals(osName) 
				|| CommandShell.os_WINDOWSVISTA.equals(osName)
				|| CommandShell.os_WINDOWS7.equals(osName)){
			OpenBabelDepiction ob = new OpenBabelDepiction();
			ob.process("c12-c3c(cccc3)Nc1nc(N)cc2");
			Assert.assertNotNull(ob.getImage());
		}
	}
	
	@Test
	public void test3D() throws Exception {
		String osName = System.getProperty("os.name");
		if (CommandShell.os_WINDOWS.equals(osName) 
				|| CommandShell.os_WINDOWSVISTA.equals(osName)
				|| CommandShell.os_WINDOWS7.equals(osName)){
			OpenBabelGen3D ob = new OpenBabelGen3D() {
				@Override
				protected String getOBabelHome() throws ShellException {
					return System.getenv(OBABEL_HOME);
				}
			};
			IAtomContainer mol = MoleculeFactory.make123Triazole();
			IAtomContainer newmol = ob.process(mol);
			Assert.assertNotNull(newmol);
			Assert.assertTrue(newmol.getAtomCount()>0);
		}
	}

}
