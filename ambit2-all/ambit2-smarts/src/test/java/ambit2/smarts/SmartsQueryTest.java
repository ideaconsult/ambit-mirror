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

package ambit2.test.smarts;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.data.molecule.SmartsQuery;

public class SmartsQueryTest {
	
	@Test
	public void testSmartsQuery() {
		SmartsQuery q = new SmartsQuery();
		assertNull(q.getSmarts());
	}

	@Test
	public void testSmartsQueryString() throws Exception  {
		SmartsQuery q = new SmartsQuery("CC");
		assertNotNull(q.getSmarts());
		assertEquals("CC",q.getSmarts());
	}

	@Test
	public void testMatch()throws Exception {
		SmartsQuery q = new SmartsQuery("CC");
		assertEquals("CC",q.getSmarts());
		assertEquals(4,q.match(MoleculeFactory.makeAlkane(3)));
		assertEquals(6,q.match(MoleculeFactory.makeAlkane(4)));

	}

	@Test
	public void testSetSmarts() throws Exception {
		SmartsQuery q = new SmartsQuery("CC");
		assertNotNull(q.getSmarts());
		assertEquals("CC",q.getSmarts());
		q.setSmarts("C=O");
		assertEquals("C=O",q.getSmarts());
	}

}


