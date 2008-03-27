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
import ambit2.similarity.HammingDistance;

public class HammingDistanceTest extends TestCase {

	public void testGetDistance() {
		HammingDistance d = new HammingDistance();
		try {
			assertEquals(3.0,
                    d.getDistance(new int[] {0,1,3,2,8},new int[] {1,1,6,2,4}),1E-6);
			assertEquals(3.0,
                    d.getDistance(new int[] {1,1,6,2,4},new int[] {0,1,3,2,8}),1E-6);			
			assertEquals(0.0,
                    d.getDistance(new int[] {0,1,3,2,8},new int[] {0,1,3,2,8}),1E-6);			
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}

}


