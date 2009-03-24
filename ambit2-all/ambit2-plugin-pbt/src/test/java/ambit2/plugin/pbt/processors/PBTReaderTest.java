package ambit2.plugin.pbt.processors;


import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.base.data.StructureRecord;
import ambit2.plugin.pbt.DbUnitTest;
import ambit2.plugin.pbt.PBTWorkBook;
import ambit2.plugin.pbt.PBTWorkBook.WORKSHEET_INDEX;

public class PBTReaderTest extends DbUnitTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() throws Exception {
		setUpDatabase("src/test/resources/ambit2/plugin/pbt/pbt-dataset.xml");
		IDatabaseConnection c = getConnection();
		ITable structures = c.createQueryTable("EXPECTED_STRUCTURES","SELECT uncompress(structure) as s FROM structure");
		Assert.assertEquals(1, structures.getRowCount());
		System.out.println(structures.getValue(0, "s"));
		ITable names = c.createQueryTable("EXPECTED_NAMES",
				"SELECT * FROM properties");
		Assert.assertEquals(194, names.getRowCount());
		ITable values = c.createQueryTable("EXPECTED_VALUES",
				"SELECT * FROM property_values");
		Assert.assertEquals(194, values.getRowCount());
		ITable templates = c.createQueryTable("EXPECTED_TEMPLATES",
				"SELECT * FROM template where name='"+PBTWorkBook.PBT_TITLE+"'");
		Assert.assertEquals(1, templates.getRowCount());
		ITable dictionary = c.createQueryTable("EXPECTED_ONTOLOGY",
				"SELECT * FROM dictionary");
		Assert.assertEquals(1, dictionary.getRowCount());

		
		PBTReader reader = new PBTReader();
		reader.setConnection(c.getConnection());
		PBTWorkBook book = reader.process(new StructureRecord(1,1,null,null));
		Assert.assertEquals("72.2", book.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).getB12());
		reader.close();
		

		c.close();
		
	}
	
}
