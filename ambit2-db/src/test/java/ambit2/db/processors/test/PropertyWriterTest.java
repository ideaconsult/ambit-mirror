/* PropertyWriterTest.java
 * Author: nina
 * Date: Jan 7, 2009
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

import java.util.Hashtable;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.core.data.StructureRecord;
import ambit2.db.processors.PropertyWriter;

public class PropertyWriterTest  extends DbUnitTest {



	@Test
	public void testWrite() throws Exception {
		
		setUpDatabase("src/test/resources/ambit2/db/processors/test/src-datasets.xml");
        IDatabaseConnection c = getConnection();
        
		ITable names = 	c.createQueryTable("EXPECTED_FIELDS","SELECT * FROM structure_fields where idstructure=100211");
		Assert.assertEquals(26,names.getRowCount());
        PropertyWriter writer = new PropertyWriter();

		StructureRecord record = new StructureRecord(7,100211,"","");
		record.setProperties(new Hashtable());
		record.getProperties().put("Property1", "Value1");
		record.getProperties().put("Property2", 0.99);
		
        writer.setConnection(c.getConnection());
        writer.open();
        writer.write(record);
        c.close();
        
        c = getConnection();
		names = 	c.createQueryTable("EXPECTED_FIELDS","SELECT * FROM structure_fields where idstructure=100211");
		Assert.assertEquals(28,names.getRowCount());

		names = 	c.createQueryTable("EXPECTED_FIELDS","SELECT name,value FROM structure_fields join field_names using(idfieldname) where idstructure=100211 and name='Property1'");
		Assert.assertEquals(1,names.getRowCount());
		Assert.assertEquals("Value1",names.getValue(0,"value"));
	
		names = 	c.createQueryTable("EXPECTED_FIELDS","SELECT name,value FROM structure_fields join field_names using(idfieldname) where idstructure=100211 and name='Property2'");
		Assert.assertEquals(1,names.getRowCount());
		Assert.assertEquals("0.99",names.getValue(0,"value"));
		
		c.close();
	}

}
