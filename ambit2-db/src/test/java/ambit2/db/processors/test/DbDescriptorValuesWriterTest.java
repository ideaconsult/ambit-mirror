/* DbDescriptorValuesWriterTest.java
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

/**
 * 
 */
package ambit2.db.processors.test;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.core.data.StructureRecord;
import ambit2.db.processors.DbDescriptorValuesWriter;
import ambit2.db.processors.DbDescriptorWriter;

/**
 * @author nina
 *
 */
public class DbDescriptorValuesWriterTest extends DbUnitTest {
	DbDescriptorValuesWriter writer;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		writer = new DbDescriptorValuesWriter();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link ambit2.db.processors.DbDescriptorValuesWriter#getStructure()}.
	 */
	@Test
	public void testGetStructure() {
		writer.setStructure(new StructureRecord(7,100211,"",""));
		Assert.assertEquals(100211,writer.getStructure().getIdstructure());
	}

	/**
	 * Test method for {@link ambit2.db.processors.DbDescriptorValuesWriter#setStructure(ambit2.core.data.IStructureRecord)}.
	 */
	@Test
	public void testSetStructure() {
		writer.setStructure(new StructureRecord(7,100211,"",""));
		Assert.assertEquals(7,writer.getStructure().getIdchemical());
	}



	/**
	 * Test method for {@link ambit2.db.processors.DbDescriptorWriter#write(org.openscience.cdk.qsar.DescriptorValue)}.
	 */
	@Test
	public void testWriteDescriptorValue() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/src-datasets.xml");			
        IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM DESCRIPTORS");	
		Assert.assertEquals(0,names.getRowCount());
		ITable values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM DVALUES");	
		Assert.assertEquals(0,values.getRowCount());
		
        writer.setConnection(c.getConnection());
        writer.open();
        XLogPDescriptor xlogp = new XLogPDescriptor();
		writer.setStructure(new StructureRecord(7,100211,"",""));
        writer.write(xlogp.calculate(MoleculeFactory.makeAlkane(10)));
		names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM DESCRIPTORS");	
		Assert.assertEquals(1,names.getRowCount());
		values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM DVALUES");	
		Assert.assertEquals(1,values.getRowCount());	        
        
        DescriptorValue v = new DescriptorValue(
                new DescriptorSpecification("XLogPReference","XLogPTitle","XLogPIdentifier","XLogPVendor"),
                new String[] {},
                new Object[] {},
                new DoubleResult(5),
                new String[] {"XLogP"}
                );
		writer.setStructure(new StructureRecord(10,100214,"",""));        
        writer.write(v);
        c.close();
        
        c = getConnection();
		names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM DESCRIPTORS");	
		Assert.assertEquals(2,names.getRowCount());
		values = 	c.createQueryTable("EXPECTED_VALUES","SELECT value FROM DVALUES WHERE idstructure=100214");	
		Assert.assertEquals(1,values.getRowCount());
		Assert.assertEquals(5.0,(Double)DataType.DOUBLE.typeCast(values.getValue(0,"value")),1E-10);	
		c.close();
	}

}
