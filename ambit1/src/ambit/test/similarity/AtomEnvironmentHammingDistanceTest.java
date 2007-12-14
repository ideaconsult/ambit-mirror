/* AtomEnvironmentHammingDistanceTest.java
 * Author: Nina Jeliazkova
 * Date: Mar 13, 2007 
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

package ambit.test.similarity;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit.data.descriptors.AtomEnvironmentList;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.structure.AtomEnvironmentGenerator;
import ambit.similarity.AtomEnvironmentsDistance;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Mar 13, 2007
 */
public class AtomEnvironmentHammingDistanceTest extends TestCase {

    /**
     * @param arg0
     */
    public AtomEnvironmentHammingDistanceTest(String arg0) {
        super(arg0);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link ambit.similarity.AtomEnvironmentHammingDistance#getDistance(ambit.data.descriptors.AtomEnvironmentList, ambit.data.descriptors.AtomEnvironmentList)}.
     */
    public void testGetDistance() {
        
       
        try {

            IMolecule mol = MoleculeFactory.makeAlkane(3);
            IMolecule mol1 = MoleculeFactory.makeAlkane(3);
            
            SmilesParser p = new SmilesParser();
            
            IMolecule mol2 = p.parseSmiles("CCCC");
           
            AtomEnvironmentGenerator g = new AtomEnvironmentGenerator();
            g.setUseHydrogens(true);
            g.setMaxLevels(3);
            
            g.process(mol);
            g.process(mol1);
            g.process(mol2);
            
            AtomEnvironmentList ae = (AtomEnvironmentList) mol.getProperty(AmbitCONSTANTS.AtomEnvironment);
            AtomEnvironmentList ae1 = (AtomEnvironmentList) mol1.getProperty(AmbitCONSTANTS.AtomEnvironment);
            AtomEnvironmentList ae2 = (AtomEnvironmentList) mol2.getProperty(AmbitCONSTANTS.AtomEnvironment);
            
            AtomEnvironmentsDistance hd = new AtomEnvironmentsDistance();
            assertEquals(1.0,hd.getDistance(ae,ae1),1E-10);
            assertEquals(hd.getDistance(ae1,ae),hd.getDistance(ae,ae1),1E-10);
            
            System.out.println(ae);
            System.out.println(ae2);
            float d = hd.getDistance(ae,ae2);
            System.out.println(d);
            assertTrue(d<1.0);
            
            assertEquals(hd.getDistance(ae2,ae),hd.getDistance(ae1,ae2),1E-10);
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }
        
     
    }

    /**
     * Test method for {@link ambit.similarity.AtomEnvironmentHammingDistance#getHammingDistance(int[], int[])}.
     */
    public void testGetHammingDistance() {
        fail("Not yet implemented");
    }

}
