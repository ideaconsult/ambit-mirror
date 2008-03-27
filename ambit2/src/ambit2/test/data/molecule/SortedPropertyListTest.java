/* SortedPropertyListTest.java
 * Author: Nina Jeliazkova
 * Date: Nov 8, 2006 
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

package ambit2.test.data.molecule;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;

import junit.framework.TestCase;
import ambit2.data.molecule.MoleculesIterator;
import ambit2.data.molecule.SortedPropertyList;

public class SortedPropertyListTest extends TestCase {

    public SortedPropertyListTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    public void test() {
        SortedPropertyList list = new SortedPropertyList("test");
        list.addProperty(new Double(7),1);
        list.addProperty(new Double(-80),0);
        list.addProperty(new Double(-40),2);
        list.sort(true);
        System.out.println(list);
        list.sort(false);
        System.out.println(list);
        
        assertEquals(1,list.getOriginalIndexOf(new Double(7)));
        assertEquals(0,list.getOriginalIndexOf(new Double(-80)));
        assertEquals(-1,list.getOriginalIndexOf("xxx"));
    }
    public void testMoleculeIterator() {
        String property="test";
        MoleculesIterator m = new MoleculesIterator();
        for (int i=0; i < 200; i++) {
            IAtomContainer c = new Molecule();
            c.setProperty(property, new Integer(200-i));
            m.addAtomContainer(c);
        }
        
        try {
            m.sortBy(property,true);
            System.out.println(m);
            assertEquals(0,m.indexOf(property,new  Integer(200)));
        } catch (Exception x) {
            
            x.printStackTrace();
            fail();
        }
    }
}
