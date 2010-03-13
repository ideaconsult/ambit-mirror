/* NNAtomEnvironmentSimilarity.java
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

import ambit2.descriptors.processors.AtomEnvironmentGenerator;
import ambit2.descriptors.processors.AtomEnvironmentList;
import ambit2.similarity.measure.AtomEnvironmentsDistance;
import ambit2.similarity.measure.IDistanceFunction;

public class NNAtomEnvironmentSimilarity<ID> extends NearestNeighborsSimilarity<ID,AtomEnvironmentList> {
    protected AtomEnvironmentGenerator generator;
    public NNAtomEnvironmentSimilarity(IDistanceFunction<AtomEnvironmentList> distanceFunction, int knn) {
        super(distanceFunction, knn);
        generator = new AtomEnvironmentGenerator();
        generator.setUseHydrogens(false);
    }
    public NNAtomEnvironmentSimilarity(int knn) {
        this(new AtomEnvironmentsDistance(), knn);
    }
    public NNAtomEnvironmentSimilarity() {
        this(10);
    }
    
    /*
    @Override
    protected AtomEnvironmentList calculateComparableProperty(Object object)
            throws AmbitException {
        IAtomContainer ac = ((IAtomContainer)object);
        Object ae = ac.getProperty(AmbitCONSTANTS.AtomEnvironment);
        if ((ae != null) && (!"".equals(ae)) && (ae instanceof AtomEnvironmentList)) 
            return (AtomEnvironmentList) ae;
        generator.process(ac);
        ae = ac.getProperty(AmbitCONSTANTS.AtomEnvironment);
        if (ae instanceof AtomEnvironmentList) return (AtomEnvironmentList) ae; else return null;
    }
    */
	@Override
	protected AtomEnvironmentList getSelectedAttributes(AtomEnvironmentList attributes) {
		return attributes;
	}

}
