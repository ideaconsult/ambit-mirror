package ambit2.dragon.test;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;

import ambit2.core.io.FileInputState;
import ambit2.dragon.DescriptorDragonShell;
import ambit2.dragon.DragonShell;


public class DragonShellTest {
	

	@Test
	public void testDragonDescriptor() throws Exception {
		DescriptorDragonShell dragon = new DescriptorDragonShell();
		IIteratingChemObjectReader reader = FileInputState.getReader(
				getClass().getClassLoader().getResourceAsStream("ambit2/dragon/test/structure.sdf")
				, "structure.sdf");
		
		Assert.assertTrue(reader != null);
		while (reader.hasNext()) {
			Object o = reader.next();
			IAtomContainer mol = (IAtomContainer) o;
			DescriptorValue value = dragon.calculate(mol);
			Assert.assertNotNull(value.getValue());
			System.out.println(value.getValue().getClass());
			Assert.assertTrue(value.getValue() instanceof DoubleArrayResultType);
			Assert.assertNotNull(((DoubleArrayResultType)value.getValue()));
		
		}
		reader.close();		
	}	
	@Test
	public void testDragonShell() throws Exception {
		DragonShell some = new DragonShell();
		IIteratingChemObjectReader reader = FileInputState.getReader(
				getClass().getClassLoader().getResourceAsStream("ambit2/dragon/test/structure.sdf")
				, "structure.sdf");
		
		Assert.assertTrue(reader != null);
		while (reader.hasNext()) {
			Object o = reader.next();
			IAtomContainer mol = (IAtomContainer) o;
			mol = some.process(mol);
			Assert.assertTrue(mol.getProperties().size()>0);
			
		}
		reader.close();		
	}	
}


