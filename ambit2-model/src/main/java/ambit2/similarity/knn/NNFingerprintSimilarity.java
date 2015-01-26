/* NNFingerprintSimilarity.java
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

package ambit2.similarity.knn;

import java.util.BitSet;

import org.openscience.cdk.fingerprint.Fingerprinter;

import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.similarity.measure.FingerprintDistance;
import ambit2.similarity.measure.IDistanceFunction;

/**
 * kNN similarity on bitsets {@link BitSet}
 * 
 * @author nina
 * 
 */
public class NNFingerprintSimilarity<ID> extends NearestNeighborsSimilarity<ID, BitSet> {

    protected FingerprintGenerator generator = null;

    public NNFingerprintSimilarity(IDistanceFunction<BitSet> distanceFunction, int knn) {
	super(distanceFunction, knn);
	generator = new FingerprintGenerator(new Fingerprinter());
	generator.setHydrogens(false);

    }

    /**
     * Uses {@link FingerprintDistance}.
     * 
     * @param knn
     */
    public NNFingerprintSimilarity(int knn) {
	this(new FingerprintDistance(), knn);
    }

    /*
     * @Override protected BitSet calculateComparableProperty(Object object)
     * throws AmbitException { if (object == null) return null; Object bitset =
     * null; IAtomContainer ac = (IAtomContainer) object;
     * 
     * bitset = ac.getProperty(AmbitCONSTANTS.Fingerprint);
     * 
     * if (bitset == null) { if (generator == null) { generator = new
     * FingerprintGenerator(); } generator.process(object); bitset =
     * ac.getProperty(AmbitCONSTANTS.Fingerprint); } if (bitset == null) throw
     * new AmbitException("Can't generate fingerprint!"); return (BitSet)bitset;
     * }
     */
    @Override
    protected BitSet getSelectedAttributes(BitSet attributes) {
	return attributes;
    }

}
