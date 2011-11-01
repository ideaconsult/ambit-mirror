/* RandomAccessTest.java
 * Author: Nina Jeliazkova
 * Date: Jul 12, 2006 
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

package ambit2.core.test.io;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.random.RandomAccessReader;
import org.openscience.cdk.io.random.RandomAccessSDFReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Test for {@link RandomAccessSDFReader}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Jul 12, 2006
 */
public class RandomAccessTest  {


    @Test public void test() throws Exception {
    	int[] numberofatoms = {33,23,22,25,21,27,27};
    	String file = getClass().getClassLoader().getResource("ambit2/core/data/misc/test_properties.sdf").getFile();
        RandomAccessReader rf = new RandomAccessSDFReader(new File(file),SilentChemObjectBuilder.getInstance());
        int i = numberofatoms.length;
        rf.last();
        for (rf.last(); rf.hasNext(); rf.previous()) {
        	i--;
            IMolecule m = (IMolecule)rf.readRecord(i);
            Assert.assertEquals(numberofatoms[i],m.getAtomCount());
            rf.previous();
        }

        rf.close();   
    }
}
