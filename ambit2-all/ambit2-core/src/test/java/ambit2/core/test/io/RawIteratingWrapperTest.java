package ambit2.core.test.io;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.core.io.RawIteratingWrapper;
import ambit2.core.io.ToxcastAssayReader;

public class RawIteratingWrapperTest {
	@Test
	public void test() throws Exception {
		IIteratingChemObjectReader reader = FileInputState.getReader(
				RawIteratingWrapperTest.class.getClassLoader().getResourceAsStream("ambit2/core/data/io/test.txt")
				, "test.txt");
		
		Assert.assertTrue(reader != null);
		Assert.assertTrue(reader instanceof IteratingDelimitedFileReader);

		RawIteratingWrapper wrapper = new RawIteratingWrapper(reader);
		int count = 0;
		while(wrapper.hasNext()) {
			IStructureRecord record = (IStructureRecord) wrapper.next();
			count++;
		}
		Assert.assertEquals(11,count);
		wrapper.close();
	}
	
	@Test
	public void testToxcast() throws Exception {
		IIteratingChemObjectReader reader = FileInputState.getReader(
				RawIteratingWrapperTest.class.getClassLoader().getResourceAsStream("ambit2/core/data/toxcast/test.txt")
				, "test.txt");
		
		Assert.assertTrue(reader != null);
		Assert.assertTrue(reader instanceof IteratingDelimitedFileReader);
		Assert.assertTrue(reader instanceof ToxcastAssayReader);

		int count = 0;
		while(reader.hasNext()) {
			IMolecule record = (IMolecule) reader.next();
			Assert.assertEquals(10,record.getProperties().size());
			Iterator<Object> keys = record.getProperties().keySet().iterator();
			while (keys.hasNext()) {
				Assert.assertNotNull(record.getProperty(keys.next()));
			}
			count++;
		}
		Assert.assertEquals(4,count);
		reader.close();
	}	
	
}
