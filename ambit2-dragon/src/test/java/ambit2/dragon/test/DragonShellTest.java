package ambit2.dragon.test;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;

import ambit2.core.io.FileInputState;
import ambit2.dragon.DescriptorDragonShell;
import ambit2.dragon.DragonDescriptorDictionary;
import ambit2.dragon.DragonShell;


public class DragonShellTest {
	

	@Test
	public void testDragonDescriptor() throws Exception {
		DescriptorDragonShell dragon = new DescriptorDragonShell();
		dragon.setParameters(new String[]{"MW,MLOGP,Constitutional indices"});
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
			Assert.assertEquals(44,((DoubleArrayResultType)value.getValue()).length());
		}
		reader.close();		
	}	
	@Test
	public void testDragonShell() throws Exception {
		DragonShell shell = new DragonShell();
		shell.setDescriptors(new String[] {"MW","MLOGP","Constitutional indices"});
		IIteratingChemObjectReader reader = FileInputState.getReader(
				getClass().getClassLoader().getResourceAsStream("ambit2/dragon/test/structure.sdf")
				, "structure.sdf");
		
		Assert.assertTrue(reader != null);
		while (reader.hasNext()) {
			Object o = reader.next();
			IAtomContainer mol = (IAtomContainer) o;
			mol = shell.process(mol);
			Assert.assertTrue(mol.getProperties().size()>0);
			
		}
		reader.close();		
	}
	
	@Test
	public void testDescriptorDictionary() throws Exception {
		DragonDescriptorDictionary d = new DragonDescriptorDictionary();
		String[] names = d.getDescriptorNames();
		//System.out.println(d.getDescriptorNamesAsString());
		Assert.assertEquals(4885,names.length);

		int n = d.setSelected(new String[] {"MLOGP","Constitutional indices","Randic molecular profiles"});
		//System.out.println(d.getDescriptorNamesAsString());
		Assert.assertEquals(85,n);
		names = d.getDescriptorNames();
		Assert.assertEquals(85,names.length); //block1,block20 and block28 MLOGP
		for (String name : names) Assert.assertFalse("".equals(name));
		
		n = d.setSelected(new String[] {});
		Assert.assertEquals(4885,n);
		names = d.getDescriptorNames();
		Assert.assertEquals(4885,names.length);
		//System.out.println(d.getDescriptorNamesAsString());
		Assert.assertEquals(29,d.getBlocks().size());
		//System.out.println(d.getSubBlocks());
	}
}


