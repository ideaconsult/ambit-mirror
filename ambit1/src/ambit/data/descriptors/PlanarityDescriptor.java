/* PlanarityDescriptor.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-17 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit.data.descriptors;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
/**
 * Planarity descriptor (0,1); zero if planar .  Returns {@link org.openscience.cdk.qsar.result.DoubleResult}[1]. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-17
 */
public class PlanarityDescriptor extends SpherosityDescriptor {

    /**
     * 
     */
    public PlanarityDescriptor() {
        super();

    }
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:planarity",
		    this.getClass().getName(),
		    "$Id: PlanarityDescriptor.java,v 1.11 2006/04/17 8:15:05 ninaj Exp $",
            "The Chemistry Development Kit");
    };    
    /*
    public DescriptorValue calculate(AtomContainer container) throws CDKException {
        double[] eval = doCalculation(container);
        double planarity = eval[2]; 
        return new DescriptorValue(getSpecification(), getParameterNames(), 
                getParameters(), new DoubleResult(planarity));        
    }
    */
    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
        DescriptorValue value =  getSizeDescriptorResult(container);
        IDescriptorResult result = value.getValue();
        //[0] is max length, [1] is max diameter [2] is min diameter - =0 if planar 
        DoubleArrayResult eval = ((DoubleArrayResult) result);
        
        return new DescriptorValue(getSpecification(), getParameterNames(), 
                getParameters(), new DoubleResult( eval.get(2)));    
    }
    public String toString() {
    	return "Planarity";
    }
    
}
