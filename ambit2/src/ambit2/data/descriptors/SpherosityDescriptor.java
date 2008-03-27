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

package ambit2.data.descriptors;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

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
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:spherocity",
		    this.getClass().getName(),
		    "$Id: SpherocityDescriptor.java,v 0.2 2006/07/29 11:24:05 Nina Jeliazkova Exp $",
            "ambit2.acad.bg");
    };
    
    /**
     * TODO this is quick and dirty - have to think how to store array results into ambit database 
     */
    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
        DescriptorValue value = getSizeDescriptorResult(container);
        
        IDescriptorResult result = value.getValue();
        //[0] is max length, [1] is max diameter [2] is min diameter 
        DoubleArrayResult eval = ((DoubleArrayResult) result);
        
        double min = eval.get(0);
        for (int i=1; i < 3; i++ ) if (min >  eval.get(0)) min =  eval.get(0);
        
        double spherosity = 3*min/( eval.get(0)+ eval.get(0)+ eval.get(0)); 
        //System.out.println(spherosity);
        return new DescriptorValue(getSpecification(), getParameterNames(), 
                getParameters(), new DoubleResult(spherosity),new String[] {"Spherosity"});        
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
    /*
    public double[] doCalculation(AtomContainer container) throws CDKException {        
        IsotopeFactory factory = null;
        try {
            factory = IsotopeFactory.getInstance();
        } catch (Exception e) {
            //logger.debug(e);
        }

        double ccf = 1.000138;
        double eps = 1e-5;

        double[][] imat = new double[container.getAtomCount()][3];
        Point3d centerOfMass = GeometryTools.get3DCenter(container);

                for (int k = 0; k < container.getAtomCount(); k++) {
                    double[] xyz = new double[3];
                    double mass = 0.0;
                    double radius = 0.0;

                    org.openscience.cdk.interfaces.Atom currentAtom = container.getAtomAt(k);
                    if (currentAtom.getPoint3d() == null) {
                        throw new CDKException("Atom "+k+" did not have any 3D coordinates. These are required");
                    }

                    mass = factory.getMajorIsotope( currentAtom.getSymbol() ).getMassNumber();

                    radius = centerOfMass.distance( currentAtom.getPoint3d() );

                    imat[k][0] = currentAtom.getPoint3d().x - centerOfMass.x;
                    imat[k][1] = currentAtom.getPoint3d().y - centerOfMass.y;
                    imat[k][2] = currentAtom.getPoint3d().z - centerOfMass.z;

           
                }
                
   

    			Matrix m = new Matrix(imat);
    			OrthogonalTransform pcaTransform = new OrthogonalTransform();
    			//System.err.println("PCA running ...");
    			int d = imat[0].length;
    			pcaTransform.InitializeFilter(m,d,d);
    			//System.err.println("PCA trasnform ...");			
    			Matrix pcapoints = pcaTransform.TransformPoints(m);
    			double[] max = new double[3];
    			double[] min = new double[3];
    			
    			for (int i=0; i < pcapoints.getRowDimension();i++) {
    			    if (i==0) {
    			        max[0] = pcapoints.get(i,0);
    			        max[1] = pcapoints.get(i,1);
    			        max[2] = pcapoints.get(i,2);    			        
    			        min[0] = pcapoints.get(i,0);
    			        min[1] = pcapoints.get(i,1);
    			        min[2] = pcapoints.get(i,2);    			        
    			    } else 
    			    	for (int k=0;k< 3; k++) {
    			    		if (max[k] <  pcapoints.get(i,k))
    			    			 max[k] = pcapoints.get(i,k);
    			    		if (min[k] >  pcapoints.get(i,k))
   			    			 min[k] = pcapoints.get(i,k);    			    		
    			    	}
    			}
    			for (int k=0;k< 3; k++) {
    				max[k] = max[k]-min[k];
    			}

        return max;

    }
    */
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
