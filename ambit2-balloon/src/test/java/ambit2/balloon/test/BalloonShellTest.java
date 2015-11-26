package ambit2.balloon.test;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.balloon.ShellBalloon;

public class BalloonShellTest {
	protected static Logger logger = Logger.getLogger(BalloonShellTest.class.getName());
	protected ShellBalloon shell;
	@Before
	public void setUp() throws Exception {
		shell = new ShellBalloon();

		File homeDir = new File(System.getProperty("java.io.tmpdir") +"/.ambit2/" + System.getProperty("user.name") + "/balloon/*");
		homeDir.delete();
		
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
	public void testRunBalloon() throws Exception {
		IAtomContainer mol = MoleculeFactory.makeAlkane(3);
		IAtomContainer newmol = shell.runShell(mol);
		Assert.assertNotNull(newmol);
		IAtomContainer c = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(newmol);
		UniversalIsomorphismTester uit = new UniversalIsomorphismTester();
		Assert.assertTrue(uit.isIsomorph(mol,c));
		for (int i=0; i < newmol.getAtomCount(); i++) {
			Assert.assertNotNull(newmol.getAtom(i).getPoint3d());
		}	
	}


}

