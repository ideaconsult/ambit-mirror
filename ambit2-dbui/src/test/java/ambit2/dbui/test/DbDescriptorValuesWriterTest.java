/* DbDescriptorValuesWriterTest.java
 * Author: Nina Jeliazkova
 * Date: May 5, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
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

package ambit2.dbui.test;

import java.sql.Connection;
import java.util.List;

import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.StructureRecord;
import ambit2.db.processors.DbDescriptorValuesWriter;
import ambit2.db.processors.RepositoryWriter;


public class DbDescriptorValuesWriterTest extends RepositoryTest {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public void test() throws Exception {
        Connection c = datasource.getConnection();        
        RepositoryWriter r = new RepositoryWriter();
        r.setConnection(c);
        r.open();
        
        IStructureRecord record = new StructureRecord(-1,-1,"test","SDF");
        List<IStructureRecord> struc = r.write(record);
        assertEquals(1,struc.size());
        
        
        DbDescriptorValuesWriter writer = new DbDescriptorValuesWriter();

        writer.setStructure(struc.get(0));
        writer.setConnection(c);
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
        writer.close();
        r.close();
    }
}
