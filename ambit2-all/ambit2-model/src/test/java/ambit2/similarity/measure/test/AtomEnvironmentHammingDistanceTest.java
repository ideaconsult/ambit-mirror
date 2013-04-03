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

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.descriptors.processors.AtomEnvironmentGenerator;
import ambit2.descriptors.processors.AtomEnvironmentList;
import ambit2.similarity.measure.AtomEnvironmentsDistance;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Mar 13, 2007
 */
public class AtomEnvironmentHammingDistanceTest {

    /**
     * Test method for {@link ambit2.similarity.AtomEnvironmentHammingDistance#getDistance(ambit2.data.descriptors.AtomEnvironmentList, ambit2.data.descriptors.AtomEnvironmentList)}.
     */
	@Test
    public void testGetDistance() throws Exception {
        
            IMolecule mol = MoleculeFactory.makeAlkane(3);
            IMolecule mol1 = MoleculeFactory.makeAlkane(3);
            
            SmilesParserWrapper p =  SmilesParserWrapper.getInstance();
            
            IAtomContainer mol2 = p.parseSmiles("CCCC");
           
            AtomEnvironmentGenerator g = new AtomEnvironmentGenerator();
            g.setUseHydrogens(true);
            g.setMaxLevels(3);
            
            AtomEnvironmentList ae =  g.generateProperty(mol);
            AtomEnvironmentList ae1 = g.generateProperty(mol1);
            AtomEnvironmentList ae2 = g.generateProperty(mol2);
            
            AtomEnvironmentsDistance hd = new AtomEnvironmentsDistance();
            Assert.assertEquals(1.0,hd.getDistance(ae,ae1),1E-10);
            Assert.assertEquals(hd.getDistance(ae1,ae),hd.getDistance(ae,ae1),1E-10);
            
            System.out.println(ae);
            System.out.println(ae2);
            float d = hd.getDistance(ae,ae2);
            //System.out.println(d);
            Assert.assertTrue(d<1.0);
            
            Assert.assertEquals(hd.getDistance(ae2,ae),hd.getDistance(ae1,ae2),1E-10);
        
     
    }

    /**
     * Test method for {@link ambit2.similarity.AtomEnvironmentHammingDistance#getHammingDistance(int[], int[])}.
     */
    public void testGetHammingDistance() throws Exception {
       Assert.fail("Not yet implemented");
    }

}
