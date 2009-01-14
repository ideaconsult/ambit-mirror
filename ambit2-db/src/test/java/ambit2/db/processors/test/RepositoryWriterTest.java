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

package ambit2.db.processors.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.LiteratureEntry;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.db.SourceDataset;
import ambit2.db.processors.RepositoryWriter;

public class RepositoryWriterTest extends DbUnitTest {

	@Test
	public void testWrite() throws Exception {
		
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
		
		write(c.getConnection());
        c.close();
        
        c = getConnection();
		chemicals = 	c.createQueryTable("EXPECTED","SELECT * FROM chemicals");
		Assert.assertEquals(3,chemicals.getRowCount());
		strucs = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(5,strucs.getRowCount());
		srcdataset = 	c.createQueryTable("EXPECTED","SELECT * FROM src_dataset where name='TEST INPUT'");
		Assert.assertEquals(1,srcdataset.getRowCount());
		struc_src = 	c.createQueryTable("EXPECTED","SELECT * FROM struc_dataset");
		Assert.assertEquals(5,struc_src.getRowCount());
		
		property = 	c.createQueryTable("EXPECTED","SELECT * FROM properties");
		Assert.assertEquals(73,property.getRowCount());
		property_values = 	c.createQueryTable("EXPECTED","SELECT * FROM property_values");
		Assert.assertEquals(202,property_values.getRowCount());		
		ITable tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM tuples");
		Assert.assertEquals(5,tuples.getRowCount());			
		ITable p_tuples = 	c.createQueryTable("EXPECTED","SELECT * FROM property_tuples");
		Assert.assertEquals(202,p_tuples.getRowCount());				
		c.close();

	}
	
	public int write(Connection connection) throws Exception  {
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/db/processors/test/input.sdf");
		Assert.assertNotNull(in);
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
		RepositoryWriter writer = new RepositoryWriter();
		writer.setDataset(new SourceDataset("TEST INPUT",new LiteratureEntry("File","file:input.sdf")));
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
