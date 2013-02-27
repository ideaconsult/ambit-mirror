/* SpherosityDescriptor.java
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

package ambit2.descriptors;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.base.data.Property;

/**
 * Spherosity descriptor. Todeschini , Handbook of Molecular descriptors.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-17
 */
public class SpherosityDescriptor implements IMolecularDescriptor {
    protected SizeDescriptor sizeDescriptor = null;
    /**
     * 
     */
    public SpherosityDescriptor() {
        sizeDescriptor = new SizeDescriptor();
    }
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
        	String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"SpherosityDescriptor"),
		    this.getClass().getName(),
		    "$Id: SpherocityDescriptor.java,v 0.2 2006/07/29 11:24:05 Nina Jeliazkova Exp $",
            "http:///ambit.sourceforge.net");
    };
    
    public DescriptorValue calculate(IAtomContainer container) {
    	try {
	        DescriptorValue value = getSizeDescriptorResult(container);
	        
	        IDescriptorResult result = value.getValue();
	        //[0] is max length, [1] is max diameter [2] is min diameter 
	        DoubleArrayResult eval = ((DoubleArrayResult) result);
	        
	        double min = eval.get(0);
	        for (int i=1; i < 3; i++ ) if (min >  eval.get(0)) min =  eval.get(0);
	        
	        double spherosity = 3*min/( eval.get(0)+ eval.get(0)+ eval.get(0)); 
	        return new DescriptorValue(getSpecification(), getParameterNames(), 
	                getParameters(), new DoubleResult(spherosity),getDescriptorNames());       
    	} catch (Exception x) {
	        return new DescriptorValue(getSpecification(), getParameterNames(), 
	                getParameters(),null,getDescriptorNames(),x);  
    	}
    }
    public String[] getDescriptorNames() {
    	return
    	new String[] {"Spherosity"};
    }
    public IDescriptorResult getDescriptorResultType() {
    	return new DoubleResult(0);
    }
    protected DescriptorValue getSizeDescriptorResult(IAtomContainer container) throws CDKException {
        Object o = container.getProperty(sizeDescriptor);
        DescriptorValue value = null;
        if (o == null) { 
        	sizeDescriptor = new SizeDescriptor();
        	value = sizeDescriptor.calculate(container);
        	container.setProperty(sizeDescriptor,value);	
        } else value = (DescriptorValue) o;
        return value;
    }
   
    /* (non-Javadoc)
     * @see org.openscience.cdk.qsar.Descriptor#getParameterNames()
     */
    public String[] getParameterNames() {

        return null;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.qsar.Descriptor#getParameters()
     */
    public Object[] getParameters() {
     
        return null;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.qsar.Descriptor#getParameterType(java.lang.String)
     */
    public Object getParameterType(String name) {
     
        return null;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.qsar.Descriptor#setParameters(java.lang.Object[])
     */
    public void setParameters(Object[] params) throws CDKException {
     
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Spherosity descriptor";
    }
}
