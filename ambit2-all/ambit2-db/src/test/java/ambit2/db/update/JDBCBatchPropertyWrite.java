package ambit2.db.update;

import java.sql.Connection;
import java.sql.ResultSet;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Assert;
import org.junit.Test;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ValueWriterNew;
import ambit2.db.processors.test.DbUnitTest;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.property.RetieveFeatures;

public class JDBCBatchPropertyWrite extends DbUnitTest {

	

	QueryExecutor x = new QueryExecutor();
	
	@Override
	protected boolean isProfileSQL() {
		return false;
	}
	
	@Test
	public void testValueWriter() throws Exception {

		setUpDatabase("src/test/resources/ambit2/db/processors/test/src-datasets.xml");
		IDatabaseConnection c = getConnection();

		ValueWriterNew writer = new ValueWriterNew();
		Connection connection = c.getConnection();
		writer.setConnection(connection);
		int nprops = 4;
		try {
			ITable table = 	c.createQueryTable("STRUCTURES","SELECT idchemical, idstructure FROM structure");
			
			for (int row = 0; row < table.getRowCount(); row++) {
				int idchemical = Integer.parseInt(table.getValue(row, "idchemical").toString());
				int idstructure = Integer.parseInt(table.getValue(row, "idstructure").toString());
				IStructureRecord recordToStore = getStructure(idchemical,idstructure,nprops);
				long now = System.currentTimeMillis();
				recordToStore = writer.process(recordToStore);
				logger.fine(String.format("Elapsed: %s ms",System.currentTimeMillis()-now));
				for (Property p : recordToStore.getRecordProperties()) Assert.assertTrue(p.getId()>0);
				
			}
							
			ITable props = 	c.createQueryTable("P","SELECT idproperty,name,title,ptype FROM properties join catalog_references using(idreference) where name regexp '^Property' and ptype='STRING'");
			Assert.assertEquals(nprops, props.getRowCount());
			ITable propsNum = 	c.createQueryTable("P","SELECT idproperty,name,title,ptype FROM properties join catalog_references using(idreference) where name regexp '^num' and FIND_IN_SET('NUMERIC',ptype)>0");
			Assert.assertEquals(nprops*3, propsNum.getRowCount());
			ITable longprops = 	c.createQueryTable("P","SELECT idproperty,name,title,ptype FROM properties join catalog_references using(idreference) where name regexp '^Property' and ptype='STRING'");
			Assert.assertEquals(nprops, longprops.getRowCount());	
			
			ITable valuesString = c.createQueryTable("P","SELECT * from property_values join properties using(idproperty) where idtype='STRING'");
			Assert.assertEquals((nprops+1)*table.getRowCount(), valuesString.getRowCount());
			
			ITable valuesStringLong = c.createQueryTable("P","SELECT * from property_values join properties using(idproperty) where idtype='STRING' and status='TRUNCATED'");
			Assert.assertEquals(table.getRowCount(), valuesStringLong.getRowCount());
			
			ITable valuesNumERROR = c.createQueryTable("P","SELECT * from property_values join properties using(idproperty) where name regexp '^num_was_NaN' and idtype='NUMERIC' and status='ERROR'");
			Assert.assertEquals((nprops)*table.getRowCount(), valuesNumERROR.getRowCount());			

			ITable valuesNumOK = c.createQueryTable("P","SELECT * from property_values join properties using(idproperty) where name regexp '^num_' and idtype='NUMERIC'");
			Assert.assertEquals(3*nprops*table.getRowCount(), valuesNumOK.getRowCount());
			
		} finally {
			
			writer.close();
			c.close();
		}
	}
	
	
	protected IStructureRecord getStructure() {
		return getStructure(10,100214,4);
	}	
	protected IStructureRecord getStructure(int idchemical,int idstructure,int nproperties) {

		StructureRecord record = new StructureRecord();
		
		record.setIdchemical(idchemical);
		record.setIdstructure(idstructure);
		LiteratureEntry e = new LiteratureEntry("test_entry", "blabla");
		LiteratureEntry dummy = new LiteratureEntry("Dummy", "");
		for (int i=0; i < nproperties; i++) {
			record.setRecordProperty(new Property(String.format("num_was_string_%d",i+1),e), i+0.555);
			record.setRecordProperty(new Property(String.format("num_was_num_%d",i+1),e), i+100);
			record.setRecordProperty(new Property(String.format("num_was_NaN_%d",i+1),e), Double.NaN);
			record.setRecordProperty(new Property(String.format("Property %d",i+1),dummy), String.format("ABCDE_%d", i+1));
		}
		StringBuilder longString = new StringBuilder(); 
		for (int i=0; i < 255;i++) longString.append("AGTC");
		record.setRecordProperty(new Property("Long Property",dummy), longString.toString());
		for (Property p : record.getRecordProperties()) p.setEnabled(true);
		return record;
		
	}
	
	@Test
	public void testRetrieveProperties() throws Exception {

		setUpDatabase("src/test/resources/ambit2/db/processors/test/src-datasets.xml");
		IDatabaseConnection c = getConnection();
		x.setConnection(c.getConnection());
		try {
			IStructureRecord record = getStructure();

			retrieveProperties(record);
					
			for (Property property : record.getRecordProperties()) {
				if ("Property 1".equals(property.getName()) || "Property 3".equals(property.getName()) || "Property 3".equals(property.getName()))
				Assert.assertTrue(property.getId()>0);
			}

						
		} finally {
			x.close();
			c.close();
		}
	}
	public void retrieveProperties(IStructureRecord record) throws Exception {
		RetieveFeatures features = new RetieveFeatures();
		features.setValue(record);


		ResultSet rs = x.process(features);
		while (rs.next()) {
			Property property = features.getObject(rs);
			Object value = record.getRecordProperty(property);
			if (value != null) {
				record.setRecordProperty(property, null);
				record.setRecordProperty(property, value); 
			}

		}
	}
}
