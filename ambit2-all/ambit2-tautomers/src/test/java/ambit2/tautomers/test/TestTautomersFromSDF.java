package ambit2.tautomers.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.core.io.FileInputState;
import ambit2.tautomers.TautomerManager;


public class TestTautomersFromSDF {
	TautomerManager tman = new TautomerManager();
	
	@Test
	public void testWarfarin() throws Exception {
		URL uri = TestTautomersFromSDF.class.getResource("/ambit2/tautomers/test/warfarin_aromatic.sdf");
		Assert.assertNotNull(uri);
		System.out.println(uri.getFile());
		
		InputStream in = new FileInputStream(new File(uri.getFile()));
		Assert.assertNotNull(in);
		try {
			IIteratingChemObjectReader<IAtomContainer> reader = FileInputState.getReader(in,".sdf");
			while (reader.hasNext()) {
				IAtomContainer mol = reader.next();
				System.out.println(mol);
			}
		} catch (Exception x) {
			throw x;
		} finally {
			if (in !=null) in.close();
		}
	}
	
	
	public void testTautomerGeneration(IAtomContainer mol) throws Exception {
		
		tman.setStructure(mol);
		
		Vector<IAtomContainer> resultTautomers = tman.generateTautomersIncrementaly();
	}
}
