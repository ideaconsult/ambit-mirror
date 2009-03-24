package ambit2.plugin.pbt.processors;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.plugin.pbt.PBTWorkBook;

public class PBTPropertiesTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() throws Exception {
		PBTWorkBook pbt = new PBTWorkBook();
		pbt.setStructure(MoleculeFactory.makeBenzene());
		PBTProperties p = new PBTProperties();
		IStructureRecord record = p.process(pbt);
		Assert.assertEquals(193,record.getProperties().size());
		System.out.println(record.getContent());
		
	}

}
