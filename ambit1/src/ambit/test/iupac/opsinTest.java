/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit.test.iupac;

import junit.framework.TestCase;
import nu.xom.Element;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.templates.MoleculeFactory;

import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import ambit.data.molecule.Compound;


public class opsinTest extends TestCase {
	public void test() {
		/*
		To use within Java

		1) Learn about XOM (http://xom.nu), the XML processing framework used
		   by OPSIN
		2) Create an OPSIN instance, by calling the following static method

		NameToStructure nameToStructure = NameToStructure.getInstance();

		3) Get CML (as XOM Elements), thus:

		Element cmlElement = nameToStructure.parseToCML("acetonitrile");

		4) Whatever you like. Maybe print it out, thus:

		System.out.println(cmlElement.toXML());
		*/
		try {
			NameToStructure nameToStructure = NameToStructure.getInstance();
			//Element cmlElement = nameToStructure.parseToCML("acetonitrile");
			Element cmlElement = nameToStructure.parseToCML("benzene");
			IMolecule mol = Compound.readMolecule(cmlElement.toXML());
			IMolecule benzene = MoleculeFactory.makeBenzene();
			
			assertTrue(UniversalIsomorphismTester.isIsomorph(mol,benzene));
			/*
			Panel2D p = new Panel2D();
			p.setAtomContainer(mol,true);
			p.setPreferredSize(new Dimension(200,200));
			JOptionPane.showMessageDialog(null,p);
			*/
			
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}
}


