package ambit2.plugin.pbt.processors;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.plugin.pbt.PBTWorkBook;
import ambit2.plugin.pbt.PBTWorkBook.WORKSHEET_INDEX;

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
		pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).setB10("Benzene");
		pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).setB12(78.2);
		pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).setB13(10);
		pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).setB14(100);
		pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).setB15(1000);
		pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).setB16("narcotic");
		Assert.assertEquals(78.2,(Double)pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).getB12());
		Assert.assertEquals(10.0,(Double)pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).getB13());
		Assert.assertEquals(100.0,(Double)pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).getB14());
		Assert.assertEquals(1000.0,(Double)pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).getB15());
		Assert.assertEquals(78.2,(Double)pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).get(11,1));
		PBTProperties p = new PBTProperties();
		IStructureRecord record = p.process(pbt);
		Assert.assertEquals(72,record.getNumberOfProperties());
		System.out.println(record.getContent());
		
	}

}
