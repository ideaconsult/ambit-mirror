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

package ambit2.similarity.measure.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.descriptors.processors.AtomEnvironmentGenerator;
import ambit2.descriptors.processors.AtomEnvironmentList;


/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Mar 13, 2007
 */
public class AtomEnvironmentDistanceTest {
	AtomEnvironmentGenerator g;
	@Before
    public void setUp() throws Exception {
        g = new AtomEnvironmentGenerator();
        g.setUseHydrogens(true);
        g.setMaxLevels(3);

    }

    protected AtomEnvironmentList getAE(AtomEnvironmentGenerator g,IMolecule mol) throws Exception {
    	g.process(mol);
        return(AtomEnvironmentList) mol.getProperty(AmbitCONSTANTS.AtomEnvironment);
    	
    }
    @Test
    public void testHellinger() throws Exception {
	    	
	    	AtomEnvironmentList ae1 = getAE(g, MoleculeFactory.makeAlkane(3));
	    	
	    	Assert.assertTrue(ae1.size()> 1);
	    	
	    	AtomEnvironmentList ae = getAE(g, MoleculeFactory.makeAlkane(1));
	    	Assert.assertEquals(2.0, ae.hellinger(ae),1E-6);
	    	Assert.assertEquals(2.0, ae1.hellinger(ae1),1E-6);
	    	//System.out.println(ae);
	    	
	    	//System.out.println(ae1);
	    	
	    	
	    	float f = ae1.hellinger(ae);
	    	//System.out.println(f);
	    	Assert.assertEquals(f,ae.hellinger(ae1));
	    	
	    	Assert.assertTrue(f< 2.0);
	    	Assert.assertTrue(f> 0.0);
	    	

    }
     /**
     * Test method for {@link ambit2.similarity.AtomEnvironmentHammingDistance#getDistance(ambit2.data.descriptors.AtomEnvironmentList, ambit2.data.descriptors.AtomEnvironmentList)}.
     */
    public void testGetDistance() throws Exception {
        
            SmilesParserWrapper p =  SmilesParserWrapper.getInstance();
            
            IMolecule mol = p.parseSmiles("CCCCCCBr");

            AtomEnvironmentList ae = getAE(g,mol);
            Assert.assertEquals(2.0f,ae.hellinger(ae));

        
     
    }


}
