/* RepositoryWriterTest.java
 * Author: nina
 * Date: Jan 9, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package net.idea.ambit2.nano;

import java.io.InputStream;
import java.sql.Connection;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IRawReader;
import ambit2.core.processors.structure.key.PropertyKey;
import ambit2.db.processors.RepositoryWriter;
import ambit2.db.processors.test.DbUnitTest;

public class NanoWriterTest extends DbUnitTest {
	
	@Test
	public void testWriteCML() throws Exception {
		
		setUpDatabase("src/test/resources/net/idea/ambit2/nano/test/empty-datasets.xml");
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
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("net/idea/ambit2/nano/test/nano.nmx");
		Assert.assertNotNull(in);
		IIteratingChemObjectReader reader1 = FileInputState.getReader(in, "nano.nmx");
		IRawReader<IStructureRecord> reader = (IRawReader<IStructureRecord>)reader1;
		//reader.setReference(LiteratureEntry.getInstance("test.txt"));
		write(reader,c.getConnection());
        c.close();
        
        c = getConnection();
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(2,chemicals.getRowCount());
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals where smiles is not null and inchi is not null and formula is not null");
		Assert.assertEquals(0,chemicals.getRowCount());		
		strucs = 	c.createQueryTable("EXPECTED","SELECT idstructure,format,type_structure FROM structure");
		Assert.assertEquals("NANO", strucs.getValue(0,"format"));
		Assert.assertEquals("NANO", strucs.getValue(0,"type_structure"));
		Assert.assertEquals(2,strucs.getRowCount());
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT id_srcdataset,idtemplate FROM src_dataset where name='TEST INPUT'");
		Assert.assertEquals(1,srcdataset.getRowCount());
		
		//verifies if trigger insert_dataset_template works ok
		Assert.assertNotNull(srcdataset.getValue(0,"idtemplate"));
		
		struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(2,struc_src.getRowCount());
		
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(4,property.getRowCount());
		
		//verifies if insert_property_tuple works ok
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM template_def join src_dataset using(idtemplate) where name='TEST INPUT'");
		Assert.assertEquals(4,property.getRowCount());
		
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(5,property_values.getRowCount());		
		ITable tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM tuples");
		Assert.assertEquals(2,tuples.getRowCount());			
		ITable p_tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM property_tuples");
		Assert.assertEquals(5,p_tuples.getRowCount());				
		c.close();
	}
	public int write(IRawReader<IStructureRecord> reader,Connection connection) throws Exception  {
		return write(reader, connection,null);
	}
	public int write(IRawReader<IStructureRecord> reader,Connection connection,PropertyKey key) throws Exception  {
		

		RepositoryWriter writer = new RepositoryWriter();
		if (key != null)
			writer.setPropertyKey(key);
		writer.setDataset(new SourceDataset("TEST INPUT",LiteratureEntry.getInstance("File","file:input.nmx")));
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
