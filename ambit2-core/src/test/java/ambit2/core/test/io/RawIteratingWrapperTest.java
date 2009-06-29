package ambit2.core.test.io;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.core.io.RawIteratingWrapper;

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
}
