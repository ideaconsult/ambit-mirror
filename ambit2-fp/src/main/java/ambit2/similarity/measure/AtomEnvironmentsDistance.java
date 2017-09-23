/* AtomEnvironmentsDistance.java
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

import ambit2.descriptors.processors.AtomEnvironmentList;



public class AtomEnvironmentsDistance implements IDistanceFunction<AtomEnvironmentList> {
    /**
     * Calls {@link AtomEnvironmentList#hellinger(AtomEnvironmentList)} and divides on 2 to 
     * get it in (0,1) range. 
     * <pre>
     * 1 - objects are same
     * 0 - objects are different
     * </pre>
     */
    public float getDistance(AtomEnvironmentList object1, AtomEnvironmentList object2) throws Exception {
        return getNativeComparison(object1, object2)/2;
    }
    public float getNativeComparison(AtomEnvironmentList object1, AtomEnvironmentList object2) throws Exception {
        return object1.hellinger(object2);
    }
    @Override
    public String toString() {
        return "Atom environments, Hellinger distance SUMi(( sqrt(Pi) + sqrt(Qi) )^2)";
    }
    /*
     * Sst = Sum(Xsj*Xtj)/[Sum(Xsj*Xsj)+Sum(Xtj*Xtj)-Sum(Xsj*Xtj)]
     */

}
