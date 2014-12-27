/* DbDescriptorWriterTest.java
 * Author: Nina Jeliazkova
 * Date: May 5, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
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

import java.util.List;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.db.processors.DbDescriptorWriter;
import ambit2.descriptors.FunctionalGroupDescriptor;

/**
 * Tests writing two descriptors with different specifications
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Jan 7, 2009
 */
public class DbDescriptorWriterTest extends DbUnitTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
	
	}

	@Test
    public void test() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");			
        DbDescriptorWriter writer = new DbDescriptorWriter();
        IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(5,names.getRowCount());
		
        writer.setConnection(c.getConnection());
        writer.open();
        XLogPDescriptor xlogp = new XLogPDescriptor();
        writer.write(xlogp.calculate(MoleculeFactory.makeAlkane(10)));
        DescriptorValue v = new DescriptorValue(
                new DescriptorSpecification("XLogPReference","XLogPTitle","XLogPIdentifier","XLogPVendor"),
                new String[] {},
                new Object[] {},
                new DoubleResult(5),
                new String[] {"XLogP"}
                );
        writer.write(xlogp.calculate(MoleculeFactory.makeAlkane(10)));
        writer.write(v);
        c.close();
        
        c = getConnection();
		names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(7,names.getRowCount());
		ITable values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values");	
		Assert.assertEquals(2,values.getRowCount());		
		c.close();
    }
	@Test
    public void testWriteAllDescriptors() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");			
        DbDescriptorWriter writer = new DbDescriptorWriter();
        IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(5,names.getRowCount());
		
		
        writer.setConnection(c.getConnection());
        writer.open();
        XLogPDescriptor xlogp = new XLogPDescriptor();
        writer.write(xlogp.calculate(MoleculeFactory.makeAlkane(10)));
        DescriptorValue v = new DescriptorValue(
                new DescriptorSpecification("XLogPReference","XLogPTitle","XLogPIdentifier","XLogPVendor"),
                new String[] {},
                new Object[] {},
                new DoubleResult(5),
                new String[] {"XLogP"}
                );
        writer.write(xlogp.calculate(MoleculeFactory.makeAlkane(10)));
        writer.write(v);
        c.close();
        
        c = getConnection();
		names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(7,names.getRowCount());
		ITable values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values");	
		Assert.assertEquals(2,values.getRowCount());		
		c.close();
    }
	@Test
    public void testWriteFunctionalGroups() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");			
        DbDescriptorWriter writer = new DbDescriptorWriter();
        IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(5,names.getRowCount());
		
		
        writer.setConnection(c.getConnection());
        writer.open();
        FunctionalGroupDescriptor d = new FunctionalGroupDescriptor();
        Assert.assertEquals(84,((List)d.getParameters()[0]).size());

        DescriptorValue v = d.calculate(MoleculeFactory.makePhenylAmine());
        Assert.assertEquals(84,v.getNames().length);
        writer.write(v);
        c.close();
        
        c = getConnection();
		names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(89,names.getRowCount());
		ITable values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values");	
		Assert.assertEquals(2,values.getRowCount());		
		ITable templates = 	c.createQueryTable("EXPECTED_TEMPLATES","SELECT * FROM properties join catalog_references using(idreference) where title=\""+d.getClass().getName()+"\"");	
		Assert.assertEquals(84,templates.getRowCount());			


		c.close();
    }	
}
