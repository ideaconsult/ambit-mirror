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

import java.io.StringReader;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.RepositoryReader;
import ambit2.db.processors.PropertyValuesWriter;

public class PropertyWriterTest  extends DbUnitTest {



	@Test
	public void testWrite() throws Exception {
		
		setUpDatabaseFromResource("ambit2/db/processors/test/descriptors-datasets.xml");
        IDatabaseConnection c = getConnection();
        
		ITable names = 	c.createQueryTable("expected_fields","SELECT * FROM property_values where idstructure=100211");
		Assert.assertEquals(0,names.getRowCount());
        PropertyValuesWriter writer = new PropertyValuesWriter();

		StructureRecord record = new StructureRecord(7,100211,"","");
		StringBuilder b = new StringBuilder();
		for (int i=0; i < 255;i++) b.append(i % 10);
		for (int i=0; i < 255;i++) b.append(i % 10);	
		for (int i=0; i < 255;i++) b.append("c");
		for (int i=0; i < 255;i++) b.append("b");		
		record.setRecordProperty(Property.getInstance("Property1","Reference 1"), b.toString());
		record.setRecordProperty(Property.getInstance("Property2","Reference 1"), 0.99);
		
        writer.setConnection(c.getConnection());
        writer.open();
        writer.write(record);
        //second time to test how it behaves :)
        
		names = 	c.createQueryTable("expected_fields","SELECT name,value,status FROM values_string where idstructure=100211 and name='Property1'");
		Assert.assertEquals(1,names.getRowCount());
		Assert.assertEquals(b.toString(),names.getValue(0,"value"));
		Assert.assertEquals("TRUNCATED",names.getValue(0,"status"));		
	
		record.setRecordProperty(Property.getInstance("Property1","Reference 1"),"Value1");
		
        writer.write(record);
        c.close();
        
        c = getConnection();
		names = 	c.createQueryTable("expected_fields","SELECT * FROM property_values where idstructure=100211 and value_num is not null");
		Assert.assertEquals(1,names.getRowCount());

		names = 	c.createQueryTable("expected_fields","SELECT name,value,status FROM values_string  where idstructure=100211 and name='Property1'");
		Assert.assertEquals(1,names.getRowCount());
		Assert.assertEquals("Value1",names.getValue(0,"value"));
		Assert.assertEquals("UNKNOWN",names.getValue(0,"status"));		
	
		names = 	c.createQueryTable("expected_fields","SELECT name,value_num FROM property_values join properties using(idproperty) where idstructure=100211 and name='Property2'");
		Assert.assertEquals(1,names.getRowCount());
		Assert.assertEquals(0.99,Double.parseDouble(names.getValue(0,"value_num").toString()));
		
		c.close();
	}
	

	@Test
	public void testReadWriteProperty() throws Exception {
		setUpDatabaseFromResource("ambit2/db/processors/test/experiments-datasets.xml");
        IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("expected_fields","SELECT * FROM property_values");
		Assert.assertEquals(0,names.getRowCount());
		
	
		
        RepositoryReader reader = new RepositoryReader();
        reader.setConnection(c.getConnection());
        PropertyValuesWriter propertyWriter = new PropertyValuesWriter();
        propertyWriter.setConnection(c.getConnection());
        propertyWriter.open();
        reader.open();
        int records = 0;
		long now = System.currentTimeMillis();
		IChemObjectBuilder b = SilentChemObjectBuilder.getInstance();
		IStructureRecord o  ;
		while (reader.hasNext()) {
			o = reader.next();
			String content = reader.getStructure(o.getIdstructure());
			if (content == null) continue;
			IIteratingChemObjectReader mReader = new IteratingSDFReader(new StringReader(content),b);
			
			if (mReader.hasNext()) {
				Object mol = mReader.next();
				if (mol instanceof IAtomContainer) {
					o.clearProperties();
					o.addRecordProperties(((IAtomContainer)mol).getProperties());
					propertyWriter.write(o);
				}
			}
			o.clear();
			mReader.close();
			mReader = null;
			records ++;
		}
		reader.close();
		propertyWriter.close();
		now = System.currentTimeMillis() - now;
		
		c = getConnection();
		names = 	c.createQueryTable("expected_fields","SELECT * FROM property_values  where idstructure in (105095,109287)");
		Assert.assertEquals(26,names.getRowCount());
		c.close();
		
	}	
}
