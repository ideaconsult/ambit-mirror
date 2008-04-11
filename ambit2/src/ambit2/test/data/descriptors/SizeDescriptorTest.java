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

package ambit2.test.data.descriptors;

import java.io.FileInputStream;

import junit.framework.TestCase;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.data.descriptors.CrossSectionalDiameterDescriptor;
import ambit2.data.descriptors.DescriptorFactory;
import ambit2.data.descriptors.DescriptorsHashtable;
import ambit2.data.descriptors.MaximumDiameterDescriptor;
import ambit2.data.descriptors.PlanarityDescriptor;
import ambit2.data.descriptors.SizeDescriptor;
import ambit2.data.molecule.Compound;
import ambit2.data.molecule.MoleculeTools;
import ambit2.processors.descriptors.CalculateDescriptors;

public class SizeDescriptorTest extends TestCase {
	IMolecule molecule = null;
	public static void main(String[] args) {
		junit.textui.TestRunner.run(SizeDescriptorTest.class);
	}
	protected void setUp() throws Exception {
		super.setUp();
		molecule = getTestMolecule();
	}
	protected void tearDown() throws Exception {
		molecule = null;
		super.tearDown();
	}
	public IMolecule getTestMolecule1() throws Exception {
		String cml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<molecule id=\"m1\">"+
		"<atomArray>"+
		"<atom id=\"a1\" elementType=\"C\" x2=\"6.4666\" y2=\"-7.3175\"/>"+
		"<atom id=\"a2\" elementType=\"S\" x2=\"7.6259\" y2=\"-6.6474\"/>"+
		"<atom id=\"a3\" elementType=\"S\" x2=\"5.3179\" y2=\"-6.6474\"/>"+
		"<atom id=\"a4\" elementType=\"Te\" x2=\"5.3179\" y2=\"-5.3179\"/>"+
		"<atom id=\"a5\" elementType=\"S\" x2=\"3.9884\" y2=\"-5.3179\"/>"+
		"<atom id=\"a6\" elementType=\"C\" x2=\"3.3184\" y2=\"-6.4666\"/>"+
		"<atom id=\"a7\" elementType=\"S\" x2=\"3.9884\" y2=\"-7.6259\"/>"+
		"<atom id=\"a8\" elementType=\"N\" x2=\"1.9889\" y2=\"-6.4666\"/>"+
		"<atom id=\"a9\" elementType=\"C\" x2=\"1.3295\" y2=\"-7.6259\" hydrogenCount=\"2\"/>"+
		"<atom id=\"a10\" elementType=\"C\" x2=\"0.0\" y2=\"-7.6259\" hydrogenCount=\"3\"/>"+
		"<atom id=\"a11\" elementType=\"C\" x2=\"1.3295\" y2=\"-5.3179\" hydrogenCount=\"2\"/>"+
		"<atom id=\"a12\" elementType=\"C\" x2=\"1.9889\" y2=\"-4.1692\" hydrogenCount=\"3\"/>"+
		"<atom id=\"a13\" elementType=\"S\" x2=\"5.3179\" y2=\"-3.9884\"/>"+
		"<atom id=\"a14\" elementType=\"C\" x2=\"4.1692\" y2=\"-3.329\"/>"+
		"<atom id=\"a15\" elementType=\"S\" x2=\"3.0099\" y2=\"-3.9884\"/>"+
		"<atom id=\"a16\" elementType=\"N\" x2=\"4.1692\" y2=\"-1.9889\"/>"+
		"<atom id=\"a17\" elementType=\"C\" x2=\"5.3179\" y2=\"-1.3295\" hydrogenCount=\"2\"/>"+
		"<atom id=\"a18\" elementType=\"C\" x2=\"6.4666\" y2=\"-1.9889\" hydrogenCount=\"3\"/>"+
		"<atom id=\"a19\" elementType=\"C\" x2=\"3.0099\" y2=\"-1.3295\" hydrogenCount=\"2\"/>"+
		"<atom id=\"a20\" elementType=\"C\" x2=\"3.0099\" y2=\"-0.0\" hydrogenCount=\"3\"/>"+
		"<atom id=\"a21\" elementType=\"S\" x2=\"6.6474\" y2=\"-5.3179\"/>"+
		"<atom id=\"a22\" elementType=\"C\" x2=\"7.3068\" y2=\"-4.1692\"/>"+
		"<atom id=\"a23\" elementType=\"S\" x2=\"6.6474\" y2=\"-3.0099\"/>"+
		"<atom id=\"a24\" elementType=\"N\" x2=\"8.6469\" y2=\"-4.1692\"/>"+
		"<atom id=\"a25\" elementType=\"C\" x2=\"9.3064\" y2=\"-3.0099\" hydrogenCount=\"2\"/>"+
		"<atom id=\"a26\" elementType=\"C\" x2=\"8.6469\" y2=\"-1.8613\" hydrogenCount=\"3\"/>"+
		"<atom id=\"a27\" elementType=\"C\" x2=\"9.3064\" y2=\"-5.3179\" hydrogenCount=\"2\"/>"+
		"<atom id=\"a28\" elementType=\"C\" x2=\"10.6358\" y2=\"-5.3179\" hydrogenCount=\"3\"/>"+
		"<atom id=\"a29\" elementType=\"N\" x2=\"6.4666\" y2=\"-8.6469\"/>"+
		"<atom id=\"a30\" elementType=\"C\" x2=\"5.3179\" y2=\"-9.3064\" hydrogenCount=\"2\"/>"+
		"<atom id=\"a31\" elementType=\"C\" x2=\"4.1692\" y2=\"-8.6469\" hydrogenCount=\"3\"/>"+
		"<atom id=\"a32\" elementType=\"C\" x2=\"7.6259\" y2=\"-9.3064\" hydrogenCount=\"2\"/>"+
		"<atom id=\"a33\" elementType=\"C\" x2=\"7.6259\" y2=\"-10.6358\" hydrogenCount=\"3\"/>"+
		"</atomArray>"+
		"<bondArray>"+
		"<bond id=\"b1\" atomRefs2=\"a1 a2\" order=\"D\"/>"+
		"<bond id=\"b2\" atomRefs2=\"a1 a3\" order=\"S\"/>"+
		"<bond id=\"b3\" atomRefs2=\"a1 a29\" order=\"S\"/>"+
		"<bond id=\"b4\" atomRefs2=\"a3 a4\" order=\"S\"/>"+
		"<bond id=\"b5\" atomRefs2=\"a4 a5\" order=\"S\"/>"+
		"<bond id=\"b6\" atomRefs2=\"a4 a13\" order=\"S\"/>"+
		"<bond id=\"b7\" atomRefs2=\"a4 a21\" order=\"S\"/>"+
		"<bond id=\"b8\" atomRefs2=\"a5 a6\" order=\"S\"/>"+
		"<bond id=\"b9\" atomRefs2=\"a6 a7\" order=\"D\"/>"+
		"<bond id=\"b10\" atomRefs2=\"a6 a8\" order=\"S\"/>"+
		"<bond id=\"b11\" atomRefs2=\"a8 a9\" order=\"S\"/>"+
		"<bond id=\"b12\" atomRefs2=\"a8 a11\" order=\"S\"/>"+
		"<bond id=\"b13\" atomRefs2=\"a9 a10\" order=\"S\"/>"+
		"<bond id=\"b14\" atomRefs2=\"a11 a12\" order=\"S\"/>"+
		"<bond id=\"b15\" atomRefs2=\"a13 a14\" order=\"S\"/>"+
		"<bond id=\"b16\" atomRefs2=\"a14 a15\" order=\"D\"/>"+
		"<bond id=\"b17\" atomRefs2=\"a14 a16\" order=\"S\"/>"+
		"<bond id=\"b18\" atomRefs2=\"a16 a17\" order=\"S\"/>"+
		"<bond id=\"b19\" atomRefs2=\"a16 a19\" order=\"S\"/>"+
		"<bond id=\"b20\" atomRefs2=\"a17 a18\" order=\"S\"/>"+
		"<bond id=\"b21\" atomRefs2=\"a19 a20\" order=\"S\"/>"+
		"<bond id=\"b22\" atomRefs2=\"a21 a22\" order=\"S\"/>"+
		"<bond id=\"b23\" atomRefs2=\"a22 a23\" order=\"D\"/>"+
		"<bond id=\"b24\" atomRefs2=\"a22 a24\" order=\"S\"/>"+
		"<bond id=\"b25\" atomRefs2=\"a24 a25\" order=\"S\"/>"+
		"<bond id=\"b26\" atomRefs2=\"a24 a27\" order=\"S\"/>"+
		"<bond id=\"b27\" atomRefs2=\"a25 a26\" order=\"S\"/>"+
		"<bond id=\"b28\" atomRefs2=\"a27 a28\" order=\"S\"/>"+
		"<bond id=\"b29\" atomRefs2=\"a29 a30\" order=\"S\"/>"+
		"<bond id=\"b30\" atomRefs2=\"a29 a32\" order=\"S\"/>"+
		"<bond id=\"b31\" atomRefs2=\"a30 a31\" order=\"S\"/>"+
		"<bond id=\"b32\" atomRefs2=\"a32 a33\" order=\"S\"/>"+
		"</bondArray>"+
		"</molecule>"
		;
		IMolecule mol = MoleculeTools.readCMLMolecule(cml);
		//HydrogenAdder adder = new HydrogenAdder();
		//adder.addExplicitHydrogensToSatisfyValency(mol);
		return mol;
		
	}
	
	public IMolecule getTestMolecule() throws Exception {
		IMolecule mol = new org.openscience.cdk. Molecule();
		MDLReader reader = new MDLReader(new FileInputStream("data/misc/224824.sdf"));
		
		mol = (Molecule) reader.read(mol);
		reader.close();
		return mol;
	}
	public void testProcessor() {
		try {
			IMolecule mol = getTestMolecule1();
			DescriptorsHashtable descriptors = new DescriptorsHashtable();
			IDescriptor cdkDescriptor = new CrossSectionalDiameterDescriptor();
			descriptors.addDescriptorPair(cdkDescriptor, DescriptorFactory.createAmbitDescriptorFromCDKdescriptor(cdkDescriptor, null,"Angstrom","Effective cross sectional diameter - the diameter of minimal cyllinder circumscribing the molecule"));
			
			CalculateDescriptors processor = new CalculateDescriptors(descriptors);
			processor.process(mol);
			assertNotNull(mol.getProperty(cdkDescriptor));
			assertTrue(mol.getProperty(cdkDescriptor) instanceof Exception);
		} catch (Exception x) {
			fail();
		}
	}
	public void testSizeDescriptor() {
		SizeDescriptor d = new SizeDescriptor();
		try {
			DescriptorValue value = d.calculate(molecule);
			IDescriptorResult result = value.getValue();
			assertTrue(result instanceof DoubleArrayResult);
			
			DoubleArrayResult a = (DoubleArrayResult) result;
			assertEquals(3,a.length());
			System.out.println(a.get(0));
			System.out.println(a.get(1));
			System.out.println(a.get(2));
	
			IMolecularDescriptor p = new PlanarityDescriptor();
			value = p.calculate(molecule);
			assertEquals(a.get(2),((DoubleResult) (value.getValue())).doubleValue(),0.00001);

			p = new MaximumDiameterDescriptor();
			value = p.calculate(molecule);
			assertEquals(a.get(0),((DoubleResult) (value.getValue())).doubleValue(),0.00001);			
			
			p = new CrossSectionalDiameterDescriptor();
			value = p.calculate(molecule);
			assertEquals(a.get(1),((DoubleResult) (value.getValue())).doubleValue(),0.00001);
			
			System.out.println(molecule.getProperties());
			
		} catch (CDKException x) {
			x.printStackTrace();
			fail();
		}
	}
	
	
}


