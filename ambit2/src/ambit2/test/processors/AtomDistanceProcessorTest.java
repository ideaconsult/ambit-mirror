/* AtomDistanceProcessorTest.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-9 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit2.test.processors;

import javax.vecmath.Point3d;

import junit.framework.TestCase;

import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.exceptions.AmbitException;
import ambit2.processors.results.AtomDistancesResult;
import ambit2.processors.structure.AtomDistanceProcessor;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class AtomDistanceProcessorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AtomDistanceProcessorTest.class);
    }
    public void test3D() {
        
        IMolecule methane = DefaultChemObjectBuilder.getInstance().newMolecule();
        methane.addAtom(new Atom("C", new Point3d(0.0, 0.0, 0.0)));
        methane.addAtom(new Atom("H", new Point3d(0.6, 0.6, 0.6)));
        methane.addAtom(new Atom("H", new Point3d(-0.6, -0.6, 0.6)));
        methane.addAtom(new Atom("H", new Point3d(0.6, -0.6, -0.6)));
        methane.addAtom(new Atom("H", new Point3d(-0.6, 0.6, -0.6)));
		
        AtomDistanceProcessor p = new AtomDistanceProcessor();
        p.setSkipH(false);
        try {
            p.process(methane);
            Object property = methane.getProperty(AtomDistancesResult.property);
            assertNotNull(property);
            assertEquals("[C	H	1, H	H	1.7]",property.toString());
            //System.out.println(property.toString());
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
        }
        
    }
    public void testNo3D() {
        Molecule methane = new org.openscience.cdk.Molecule();
        methane.addAtom(new Atom("C"));
        methane.addAtom(new Atom("H"));
        methane.addAtom(new Atom("H"));
        methane.addAtom(new Atom("H"));
        methane.addAtom(new Atom("H"));
		
        AtomDistanceProcessor p = new AtomDistanceProcessor();
        try {
            p.process(methane);
            Object property = methane.getProperty(AtomDistancesResult.property);
            assertNull(property);
        } catch (AmbitException x) {
            assertEquals(AtomDistanceProcessor.ERR_3DNOTDEFINED,x.getMessage());
        }
        
    }    

}
