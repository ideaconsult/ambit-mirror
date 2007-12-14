/* MCSSDistance.java
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

package ambit.similarity;

import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;

/**
 * The MCSS of two compounds is the largest possible substructure that is present in both structures. 
 * The MCS is a measure and a description of the similarity of two structures whose numerical value MCS is the number of common elements provided by the matching conditions, i.e. a measure of the size of the maximum common substructure.
 * 
 * @author Nina Jeliazkova
 *
 */
public class MCSSDistance implements IDistanceFunction<IAtomContainer> {



    public float getNativeComparison(IAtomContainer object1, IAtomContainer object2) throws Exception {        
        try {
            List commonSubstructures =  UniversalIsomorphismTester.getOverlaps(object1,object2);
            
            int max = -1;
            IAtomContainer mcss = null;
            for (int i = 0; i < commonSubstructures.size(); i++){
               IAtomContainer a = (IAtomContainer)commonSubstructures.get(i);
               if ((i==0) || (a.getAtomCount() > max)) {
                  max = a.getAtomCount();
                  mcss = a;
               }
            }
            float mcssAB = mcss.getAtomCount() + mcss.getElectronContainerCount();
            float c1 = object1.getAtomCount() + object1.getElectronContainerCount();
            float c2 = object2.getAtomCount() + object2.getElectronContainerCount();
            if ((c1>0)&&(c2>0)) {
                return (mcssAB*mcssAB)/(c1*c2);
            } else return 0;
        } catch (CDKException x) {
            throw new Exception(x);
        }
    }
    public float getDistance(IAtomContainer object1, IAtomContainer object2) throws Exception {
        return getNativeComparison(object1, object2);
    }
    @Override
    public String toString() {
        return "Maximum Common Substructure (A+B)*(A+B)/(A*B)";
    }
}
