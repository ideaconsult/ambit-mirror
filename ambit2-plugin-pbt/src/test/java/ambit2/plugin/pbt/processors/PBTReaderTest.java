package ambit2.plugin.pbt.processors;


import java.sql.Statement;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.plugin.pbt.DbUnitTest;
import ambit2.plugin.pbt.PBTWorkBook;


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
		//System.out.println(structures.getValue(0, "s"));
		ITable names = c.createQueryTable("EXPECTED_NAMES",
				"SELECT * FROM properties");
		Assert.assertEquals(72, names.getRowCount());
		ITable values = c.createQueryTable("EXPECTED_VALUES",
				"SELECT * FROM property_values");
		Assert.assertEquals(72, values.getRowCount());
		ITable templates = c.createQueryTable("EXPECTED_TEMPLATES",
				"SELECT * FROM template where name='"+PBTWorkBook.PBT_TITLE+"'");
		Assert.assertEquals(1, templates.getRowCount());
		ITable dictionary = c.createQueryTable("EXPECTED_ONTOLOGY",
				"SELECT * FROM dictionary");
		Assert.assertEquals(1, dictionary.getRowCount());

		Statement t = c.getConnection().createStatement();
		t.executeUpdate("update property_values set value_num=72.2 where idproperty=65 and id=65 and idstructure=1");
		t.close();
		templates = c.createQueryTable("EXPECTED_VALUES",	"SELECT name,value_num FROM property_values join properties using(idproperty) where name=\"SUBSTANCE_B12\" and (value_num - 72.2) <= 1E-4");
		Assert.assertEquals(1, templates.getRowCount());
		
		/*
		PBTReader reader = new PBTReader();
		reader.setConnection(c.getConnection());
		PBTWorkBook book = reader.process(new StructureRecord(1,1,null,null));
		Assert.assertEquals(72.2, (Double)book.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).getB12(),1E-4);
		reader.close();
		
	*/
		c.close();

		
	}
	
}
