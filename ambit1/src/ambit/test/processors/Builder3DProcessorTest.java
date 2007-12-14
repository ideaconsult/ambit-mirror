/* Builder3DProcessorTest.java
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

package ambit.test.processors;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.modeling.builder3d.ModelBuilder3D;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.HydrogenAdder;

import ambit.io.FastTemplateHandler3D;

import junit.framework.TestCase;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Jul 12, 2006
 */
public class Builder3DProcessorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Builder3DProcessorTest.class);
    }

    public void testProcess() {
    }
    public void testModelBuilder3D_c1ccccc1C0(){

    	
		ModelBuilder3D mb3d=new ModelBuilder3D();
    HydrogenAdder hAdder=new HydrogenAdder();
    String smile="c1ccccc1C=0";
		try {
			SmilesParser sp = new SmilesParser();
			IMolecule mol = sp.parseSmiles(smile);
			hAdder.addExplicitHydrogensToSatisfyValency(mol);
			mb3d.setTemplateHandler(FastTemplateHandler3D.getInstance());
			
			mb3d.setMolecule(mol,false);
			mb3d.generate3DCoordinates();
			mol = mb3d.getMolecule();
			for (int i=0;i<mol.getAtomCount();i++){
				assertNotNull(mol.getAtomAt(i).getPoint3d());
				System.out.println(mol.getAtomAt(i).getPoint3d());
			}			
		} catch (Exception exc) {
			System.out.println("Cannot layout molecule with SMILES: "+smile);
			
			fail(exc.toString());
		}
	}

}
