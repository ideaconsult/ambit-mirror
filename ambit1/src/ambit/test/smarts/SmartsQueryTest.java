/*
Copyright (C) 2005-2007  

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

package ambit.test.smarts;

import junit.framework.TestCase;

import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit.data.molecule.Compound;
import ambit.data.molecule.SmartsQuery;

public class SmartsQueryTest extends TestCase  {
	
	public void testSmartsQuery() {
		SmartsQuery q = new SmartsQuery();
		assertNull(q.getSmarts());
	}

	public void testSmartsQueryString() throws Exception  {
		SmartsQuery q = new SmartsQuery("CC");
		assertNotNull(q.getSmarts());
		assertEquals("CC",q.getSmarts());
	}

	public void testMatch()throws Exception {
		SmartsQuery q = new SmartsQuery("CC");
		assertEquals("CC",q.getSmarts());
		assertEquals(4,q.match(MoleculeFactory.makeAlkane(3)));
		assertEquals(6,q.match(MoleculeFactory.makeAlkane(4)));

	}

	public void testSetSmarts() throws Exception {
		SmartsQuery q = new SmartsQuery("CC");
		assertNotNull(q.getSmarts());
		assertEquals("CC",q.getSmarts());
		q.setSmarts("C=O");
		assertEquals("C=O",q.getSmarts());
	}


	public void testMatchAromatic()throws Exception {
		String smarts = "a";
		SmartsQuery q = new SmartsQuery(smarts);
		assertEquals(smarts,q.getSmarts());
		IMolecule mol = MoleculeFactory.makeBenzene();
		HueckelAromaticityDetector.detectAromaticity(mol);
		assertTrue(q.match(mol)>0);
		

	}
	public void testMatchAromaticCML()throws Exception {
		String smarts = "a";
		SmartsQuery q = new SmartsQuery(smarts);
		assertEquals(smarts,q.getSmarts());
		IMolecule mol = Compound.readMolecule(getCML());
		assertTrue(HueckelAromaticityDetector.detectAromaticity(mol));
		assertTrue(q.match(mol)>0);
		

	}	
	
	
	protected String getCML() {
		return
	"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"+
	"<molecule id=\"m1\" xmlns=\"http://www.xml-cml.org/schema\">\n"+
	"  <atomArray>\n"+
	"    <atom id=\"a1\" elementType=\"C\" x2=\"1.9361\" y2=\"-0.6\" formalCharge=\"0\" hydrogenCount=\"1\"/>\n"+
	"    <atom id=\"a2\" elementType=\"C\" x2=\"1.9349\" y2=\"-1.4273\" formalCharge=\"0\" hydrogenCount=\"1\"/>\n"+
	"    <atom id=\"a3\" elementType=\"C\" x2=\"2.6497\" y2=\"-1.8402\" formalCharge=\"0\" hydrogenCount=\"1\"/>\n"+
	"    <atom id=\"a4\" elementType=\"C\" x2=\"3.3662\" y2=\"-1.4269\" formalCharge=\"0\" hydrogenCount=\"1\"/>\n"+
	"    <atom id=\"a5\" elementType=\"C\" x2=\"3.3633\" y2=\"-0.5963\" formalCharge=\"0\" hydrogenCount=\"1\"/>\n"+
	"    <atom id=\"a6\" elementType=\"C\" x2=\"2.6479\" y2=\"-0.1872\" formalCharge=\"0\" hydrogenCount=\"1\"/>\n"+
	"  </atomArray>\n"+
	"  <bondArray>\n"+
	"    <bond id=\"b1\" atomRefs2=\"a1 a2\" order=\"A\"/>\n"+
	"    <bond id=\"b2\" atomRefs2=\"a3 a4\" order=\"A\"/>\n"+
	"    <bond id=\"b3\" atomRefs2=\"a4 a5\" order=\"A\"/>\n"+
	"    <bond id=\"b4\" atomRefs2=\"a2 a3\" order=\"A\"/>\n"+
	"    <bond id=\"b5\" atomRefs2=\"a5 a6\" order=\"A\"/>\n"+
	"    <bond id=\"b6\" atomRefs2=\"a6 a1\" order=\"A\"/>\n"+
	"  </bondArray>\n"+
	"</molecule>\n";
	}
	
}


