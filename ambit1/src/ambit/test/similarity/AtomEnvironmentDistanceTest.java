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


/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Mar 13, 2007
 */
public class AtomEnvironmentDistanceTest extends TestCase {
	AtomEnvironmentGenerator g;
    /**
     * @param arg0
     */
    public AtomEnvironmentDistanceTest(String arg0) {
        super(arg0);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        g = new AtomEnvironmentGenerator();
        g.setUseHydrogens(true);
        g.setMaxLevels(3);

    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    protected AtomEnvironmentList getAE(AtomEnvironmentGenerator g,IMolecule mol) throws Exception {
    	g.process(mol);
        return(AtomEnvironmentList) mol.getProperty(AmbitCONSTANTS.AtomEnvironment);
    	
    }
    public void testHellinger() {
    	try {
	    	
	    	AtomEnvironmentList ae1 = getAE(g, MoleculeFactory.makeAlkane(3));
	    	
	    	assertTrue(ae1.size()> 1);
	    	
	    	AtomEnvironmentList ae = getAE(g, MoleculeFactory.makeAlkane(1));
	    	assertEquals(2.0, ae.hellinger(ae),1E-6);
	    	assertEquals(2.0, ae1.hellinger(ae1),1E-6);
	    	//System.out.println(ae);
	    	
	    	//System.out.println(ae1);
	    	
	    	
	    	float f = ae1.hellinger(ae);
	    	//System.out.println(f);
	    	assertEquals(f,ae.hellinger(ae1));
	    	
	    	assertTrue(f< 2.0);
	    	assertTrue(f> 0.0);
	    	
    	} catch (Exception x) {
    		x.printStackTrace();
    		fail();
    	}
    }
     /**
     * Test method for {@link ambit.similarity.AtomEnvironmentHammingDistance#getDistance(ambit.data.descriptors.AtomEnvironmentList, ambit.data.descriptors.AtomEnvironmentList)}.
     */
    public void testGetDistance() {
        
       
        try {

            SmilesParser p = new SmilesParser();
            
            IMolecule mol = p.parseSmiles("CCCCCCBr");

            AtomEnvironmentList ae = getAE(g,mol);
            assertEquals(2.0f,ae.hellinger(ae));

        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }
        
     
    }


}
