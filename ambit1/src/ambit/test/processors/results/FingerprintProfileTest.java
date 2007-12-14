package ambit.test.processors.results;

import java.util.BitSet;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit.exceptions.AmbitException;
import ambit.processors.IAmbitProcessor;
import ambit.processors.IAmbitResult;
import ambit.processors.results.FingerprintProfile;
import ambit.processors.structure.FingerprintProfileGenerator;

public class FingerprintProfileTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(FingerprintProfileTest.class);
	}

	public FingerprintProfileTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public void testMol() {
		try {
			IAmbitProcessor processor = new FingerprintProfileGenerator();
			IAmbitResult profile = processor.createResult();
			processor.setResult(profile);
			assertTrue(profile instanceof FingerprintProfile);
			
			FingerprintProfile fp = (FingerprintProfile) profile;
			
			IMolecule mol = MoleculeFactory.makeAlkane(10);
			processor.process(mol);
			int c = 0;
			for (int i=0; i < fp.getLength();i++) {
				assertTrue(fp.getBitFrequency(i)<=1);
				if (fp.getBitFrequency(i)>0) c++;
			}	
			assertTrue(c>0);
			mol = MoleculeFactory.makeBenzene();
			processor.process(mol);
			int cc = 0;
			for (int i=0; i < fp.getLength();i++) {
				assertTrue(fp.getBitFrequency(i)<=2);
				if (fp.getBitFrequency(i)>0) cc++;
			}	
			assertTrue(cc>0);
			assertTrue(cc != c);
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		}
		
	}
	public void testSimple() {
		try {
			FingerprintProfile fp = new FingerprintProfile("",8);
			BitSet b = new BitSet();
			b.set(1);b.set(3);b.set(5);b.set(7);
			fp.update(b);
			
			assertEquals(1.0,fp.getBitFrequency(1),0.001);
			assertEquals(1.0,fp.getBitFrequency(3),0.001);
			assertEquals(1.0,fp.getBitFrequency(5),0.001);
			assertEquals(1.0,fp.getBitFrequency(7),0.001);
			
			b.clear();
			b.set(0);b.set(3);b.set(5);
			fp.update(b);
			
			assertEquals(0.5,fp.getBitFrequency(0),0.001);
			assertEquals(0.5,fp.getBitFrequency(1),0.001);
			assertEquals(0,fp.getBitFrequency(2),0.001);
			assertEquals(1.0,fp.getBitFrequency(3),0.001);
			assertEquals(1.0,fp.getBitFrequency(5),0.001);
			assertEquals(0.5,fp.getBitFrequency(7),0.001);			
			
			b.clear();
			b.set(2);b.set(3);b.set(5);b.set(7);
			fp.update(b);
			assertEquals(0.333,fp.getBitFrequency(0),0.001);
			assertEquals(0.333,fp.getBitFrequency(1),0.001);
			assertEquals(0.333,fp.getBitFrequency(2),0.001);
			assertEquals(1.0,fp.getBitFrequency(3),0.001);
			assertEquals(1.0,fp.getBitFrequency(5),0.001);
			assertEquals(0.666,fp.getBitFrequency(7),0.001);			

			
			
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		}
		
	}
}
