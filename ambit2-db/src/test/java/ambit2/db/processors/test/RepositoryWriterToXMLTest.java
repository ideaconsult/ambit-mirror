package ambit2.db.processors.test;

import java.io.InputStream;
import java.sql.Connection;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.db.processors.RepositoryWriter;

public class RepositoryWriterToXMLTest extends DbUnitTest { 

	@Test
	public void testWriteToXML() throws Exception {
		
		setUpDatabase("src/test/resources/ambit2/db/processors/test/empty-datasets.xml");
        IDatabaseConnection c = getConnection();
        
		ITable chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(0,chemicals.getRowCount());
		ITable strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(0,strucs.getRowCount());
		ITable srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset");
		Assert.assertEquals(0,srcdataset.getRowCount());
		ITable struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(0,struc_src.getRowCount());
		ITable property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(0,property.getRowCount());
		ITable property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(0,property_values.getRowCount());
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/toxml/invitromicronucleus_study.xml");
	
		Assert.assertNotNull(in);
		IRawReader reader = (IRawReader) FileInputState.getReader(in,"study.xml");
				//LiteratureEntry.getInstance("study.toxml"));

		write(reader,c.getConnection());
        c.close();
        
        c = getConnection();
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(2,chemicals.getRowCount());
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
		Assert.assertEquals(1,chemicals.getRowCount());		
		strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(2,strucs.getRowCount());
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='TEST INPUT'");
		Assert.assertEquals(1,srcdataset.getRowCount());
		struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(2,struc_src.getRowCount());
		
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(96,property	.getRowCount());
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(191,property_values.getRowCount());		
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset join template_def using(idtemplate) where name='TEST INPUT'");
		Assert.assertEquals(96,srcdataset.getRowCount());											
		c.close();
		/**
		 * Removing redundant properties
insert ignore into property_values
select id,idproperty,idstructure,idvalue,idtype,user_name,status from property_values where idstructure>3
on duplicate key update idstructure=3
delete from property_values where idstructure>3

insert ignore into struc_dataset
select idstructure,id_srcdataset from struc_dataset where idstructure>3
on duplicate key update idstructure=3
delete from struc_dataset where idstructure>3
		 */

	}
	
	public int write(IRawReader<IStructureRecord> reader,Connection connection) throws Exception  {
		return write(reader, connection,null);
	}
	public int write(IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key) throws Exception  {
		

		RepositoryWriter writer = new RepositoryWriter();
		if (key != null)
			writer.setPropertyKey(key);
		writer.setDataset(new SourceDataset("TEST INPUT",LiteratureEntry.getInstance("File","file:study.toxml")));
		writer.setConnection(connection);
        writer.open();
		int records = 0;
		while (reader.hasNext()) {
            IStructureRecord record = reader.nextRecord();
			writer.write(record);
			records ++;

		}
		reader.close();
		writer.close();
		return records;
	}	
	
}
