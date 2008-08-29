/* DbDescriptorWriterTest.java
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

package ambit2.db.test;

import java.sql.Connection;

import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.db.processors.DbDescriptorWriter;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> May 5, 2008
 */
public class DbDescriptorWriterTest extends RepositoryTest {

    /* (non-Javadoc)
     * @see ambit2.test.repository.RepositoryTest#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see ambit2.test.repository.RepositoryTest#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public void test() throws Exception {
        DbDescriptorWriter writer = new DbDescriptorWriter();
        Connection c = datasource.getConnection();
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
    }

}
