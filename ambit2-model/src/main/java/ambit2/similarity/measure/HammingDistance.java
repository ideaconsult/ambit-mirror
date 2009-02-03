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

package ambit2.similarity.measure;


/**
 * The Hamming distance between two strings of equal length is the number of positions for which the corresponding symbols are different/
 * @author Nina Jeliazkova
 *
 */
public class HammingDistance implements IDistanceFunction<int[]> {

	public HammingDistance() {

	}

	public float getDistance(int[] object1, int[] object2) throws Exception {
		return getNativeComparison(object1, object2);
	}
	/**
     * Count only nonzero items 
	 */
	public float getNativeComparison(int[] object1, int[] object2)
			throws Exception {
		if (object1.length != object2.length) return 0;
		else {
			float places = 0;
			for (int i=0; i < object1.length;i++) {
				if (object1[i] != object2[i]) places++;
                
			}	
			return (float)places;
		}		
	}



}


