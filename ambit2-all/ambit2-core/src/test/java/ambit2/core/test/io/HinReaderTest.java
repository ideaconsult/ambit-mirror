package ambit2.core.test.io;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import ambit2.core.io.FileInputState;
import ambit2.core.processors.structure.HydrogenAdderProcessor;

public class HinReaderTest {
	protected static Logger logger = Logger.getLogger(HinReaderTest.class.getName());
	/**
	 * CDK bug https://sourceforge.net/tracker/?func=detail&aid=2984581&group_id=20024&atid=120024
	 * @throws Exception
	 */
	@Test
	public void testHIN() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/core/data/hin/test.hin");
		IIteratingChemObjectReader reader = 
			 FileInputState.getReader(in,
					 "test.hin");
		int count = 0;
		while (reader.hasNext()) {
			Object o = reader.next();
			logger.fine(o.toString());
			/*
			Assert.assertTrue(o instanceof IAtomContainer);
			Assert.assertEquals(23,((IAtomContainer)o).getAtomCount());
			*/
			count++;
		}
		reader.close();
		Assert.assertEquals(1,count);
	}	
	@Test
	public void testNitro() throws Exception {
		String smiles  = "[H]C([H])([H])C([H])([H])C([H])([H])C([H])([H])N(C1=C(F)C(F)=C(C(F)=C1(F))C(F)(F)F)N(=O)[O-]";
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer target = p.parseSmiles(smiles);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(target);
		HydrogenAdderProcessor ha = new HydrogenAdderProcessor();
		target = ha.process(target);
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/core/data/hin/nitro.hin");
		IIteratingChemObjectReader reader = 
			 FileInputState.getReader(in,
					 "nitro.hin");
		int count = 0;
		while (reader.hasNext()) {
			Object o = reader.next();
			logger.fine(o.toString());
			Assert.assertTrue(o instanceof IAtomContainer);
			//may be extra H atom?
			Assert.assertEquals(target.getAtomCount(),((IAtomContainer)o).getAtomCount());
			SmilesGenerator g = new SmilesGenerator();
			if (logger.isLoggable(Level.FINE))
				logger.fine(g.createSMILES((IAtomContainer)o));			
			UniversalIsomorphismTester uit = new UniversalIsomorphismTester();
			Assert.assertTrue(uit.isIsomorph(target,((IAtomContainer)o)));
			count++;

		}
		Assert.assertEquals(1,count);
		reader.close();

	}		
}
