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

package ambit2.test.similarity;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.exceptions.AmbitException;
import ambit2.similarity.DescriptorSimilarityProcessor;

public class DescriptorSimilarityProcessorTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DescriptorSimilarityProcessorTest.class);
	}
	public void test() {
		DescriptorSimilarityProcessor p = new DescriptorSimilarityProcessor();
		IAtomContainer a = MoleculeFactory.makeAlkane(2);
		a.setProperty("CLOGP", 2);
		IAtomContainer a1 = MoleculeFactory.makeAlkane(4);
		a1.setProperty("CLOGP", 4);
		try {
			p.process(a);
			p.process(a1);
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		}
		p.close();
		try {
			p.process(a);
			assertNotNull(a.getProperty(p.getSimilarityProperty()));
			p.process(a1);
			assertNotNull(a1.getProperty(p.getSimilarityProperty()));
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		}		
		p.close();
	}
}


