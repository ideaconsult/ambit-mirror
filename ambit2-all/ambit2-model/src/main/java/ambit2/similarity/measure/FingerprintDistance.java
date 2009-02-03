/* FingerprintDistance.java
 * Author: Nina Jeliazkova
 * Date: Feb 4, 2007 
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

package ambit2.similarity.measure;

import java.util.BitSet;


/**
 * 
 * Tanimoto similarity index between fingerprints.
 *<pre>
 1 - objects are same
 0 - objects are different
 </pre>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Feb 18, 2007
 */
public class FingerprintDistance implements IDistanceFunction<BitSet> {

    public float getDistance(BitSet object1, BitSet object2) throws Exception {
        //should be 1-sim
        return getNativeComparison(object1, object2);
    }
    public float getNativeComparison(BitSet bitset1, BitSet bitset2) throws Exception {
        //return Tanimoto.calculate(object1, object2);
        float _bitset1_cardinality = bitset1.cardinality();
        float _bitset2_cardinality = bitset2.cardinality();
        BitSet one_and_two = (BitSet)bitset1.clone();
        one_and_two.and(bitset2);
        float _common_bit_count = one_and_two.cardinality();
        return _common_bit_count/(_bitset1_cardinality + _bitset2_cardinality - _common_bit_count);
    }    
    @Override
    public String toString() {
        return "Fingerprints, Tanimoto distance";
    }
}
