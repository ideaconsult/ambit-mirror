/* MopacProcessorTest.java
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

package ambit.test.processors;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.HydrogenAdder;

import ambit.processors.descriptors.MopacProcessor;
import ambit.processors.descriptors.MopacShell;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class MopacProcessorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MopacProcessorTest.class);
    }
	public void test() {
	    try {
		IMolecule mol = MoleculeFactory.makeBenzene();
        HydrogenAdder ha = new HydrogenAdder();
        ha.addExplicitHydrogensToSatisfyValency(mol);		
        StructureDiagramGenerator g = new StructureDiagramGenerator();
        g.setMolecule(mol,false);
        g.generateCoordinates();		
		MopacProcessor gen = new MopacProcessor(new MopacShell());
		
			Object o = gen.process(mol);
			assertTrue(o instanceof IMolecule);
			assertNotNull(((IMolecule) o).getProperty("EHOMO"));
			assertNotNull(((IMolecule) o).getProperty("ELUMO"));

		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
		
	}
}
