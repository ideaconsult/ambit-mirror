/* ReadStructureProcessorTest.java
 * Author: Nina Jeliazkova
 * Date: Apr 1, 2007 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
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

package ambit.test.database.processors;

import static org.junit.Assert.*;

import java.sql.Connection;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit.database.ConnectionPool;
import ambit.database.processors.DefaultDbProcessor;
import ambit.database.processors.ReadCASProcessor;
import ambit.database.processors.ReadSMILESProcessor;
import ambit.database.processors.ReadStructureProcessor;
import ambit.database.processors.ReadSubstanceProcessor;
import ambit.database.search.DbSimilarityByFingerprintsReader;
import ambit.io.DelimitedFileWriter;
import ambit.io.batch.DefaultBatchConfig;
import ambit.io.batch.DefaultBatchProcessing;
import ambit.io.batch.DefaultBatchStatistics;
import ambit.io.batch.IBatch;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.ProcessorsChain;
import ambit.test.ITestDB;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Apr 1, 2007
 */
public class ReadStructureProcessorTest extends TestCase  {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }
    protected void read(DefaultDbProcessor p, Object id, Object queryID) throws Exception {
        Object o = p.process(id);
        assertTrue(o != null);
        assertTrue(o instanceof IAtomContainer);
        if (id instanceof Integer)
            assertEquals(id,((IAtomContainer)o).getProperty(queryID));
        assertTrue(((IAtomContainer)o).getAtomCount()>0);
    }
    /**
     * Test method for {@link ambit.database.processors.ReadStructureProcessor#process(java.lang.Object)}.
     */
    @Test
    public void testProcess() {
        try {
            ConnectionPool pool = new ConnectionPool(
                    ITestDB.host,ITestDB.port,"ambit","root","",1,1);
            Connection conn = pool.getConnection();
            ReadStructureProcessor p = new ReadStructureProcessor(conn);
            read(p,new Integer(1),p.getQueryID());
            read(p,new Integer(2),p.getQueryID());            
            p.close();
            
            ReadSubstanceProcessor p1 = new ReadSubstanceProcessor(conn);
            for (int i=1; i < 20; i++)
                read(p1,new Integer(i),p1.getQueryID());            
            
            
            IMolecule m = new Molecule();
            m.setProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE, new Integer(1));
            read(p1,m,p.getQueryID());
            p1.close();            
            pool.returnConnection(conn);
        } catch (Exception x) {
            fail();
        }
    }
}
