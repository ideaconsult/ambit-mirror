package ambit2.some.test;

import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.qsar.DescriptorValue;

import ambit2.core.data.StringDescriptorResultType;
import ambit2.core.io.FileInputState;
import ambit2.some.DescriptorSOMEShell;
import ambit2.some.SOMERawReader;
import ambit2.some.SOMEResultsParser;
import ambit2.some.SOMEShell;

public class SOMEWrapperTest {
	
	@Test
	public void testSOMERawReader() throws Exception {
		InputStreamReader f = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("ambit2/some/test/structure.some"));
		SOMERawReader reader = new SOMERawReader(f);
		SOMEResultsParser parser = new SOMEResultsParser();
		int count = 0;
		while (reader.hasNext()) {
			String some = reader.nextRecord();
			count++;
			parser.parseRecord(some);
		}
		f.close();
		Assert.assertEquals(1,count);
	}
	@Test
	public void testSOMEDescriptor() throws Exception {
		DescriptorSOMEShell some = new DescriptorSOMEShell();
		IIteratingChemObjectReader reader = FileInputState.getReader(
				getClass().getClassLoader().getResourceAsStream("ambit2/some/test/structure.sdf")
				, "structure.sdf");
		
		Assert.assertTrue(reader != null);
		while (reader.hasNext()) {
			Object o = reader.next();
			IAtomContainer mol = (IAtomContainer) o;
			DescriptorValue value = some.calculate(mol);
			Assert.assertNotNull(value.getValue());
			Assert.assertNotNull(value.getValue() instanceof StringDescriptorResultType);
			Assert.assertNotNull(((StringDescriptorResultType)value.getValue()).getValue());
		
		}
		reader.close();		
	}	
	@Test
	public void testSOMEShell() throws Exception {
		SOMEShell some = new SOMEShell();
		IIteratingChemObjectReader reader = FileInputState.getReader(
				getClass().getClassLoader().getResourceAsStream("ambit2/some/test/structure.sdf")
				, "structure.sdf");
		
		Assert.assertTrue(reader != null);
		while (reader.hasNext()) {
			Object o = reader.next();
			IAtomContainer mol = (IAtomContainer) o;
			mol = some.process(mol);
			Assert.assertNotNull(mol.getProperty(SOMEShell.SOME_RESULT));
		}
		reader.close();		
	}	
}
