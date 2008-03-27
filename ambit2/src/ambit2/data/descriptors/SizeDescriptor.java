/* SizeDescriptor.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-29 
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

package ambit2.data.descriptors;

import javax.vecmath.Point3d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryToolsInternalCoordinates;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 * Size descriptor.  Returns {@link org.openscience.cdk.qsar.result.DoubleResult}[3], where <br> 
 * length, height and depth.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class SizeDescriptor implements IMolecularDescriptor {

    /**
     * 
     */
    public SizeDescriptor() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.qsar.Descriptor#getSpecification()
     */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:SizeDescriptor",
		    this.getClass().getName(),
		    "$Id: SizeDescriptor.java,v 0.1 2006/07/29 11:14:00 Nina Jeliazkova Exp $",
            "ambit2.acad.bg");
    };

    /* (non-Javadoc)
     * @see org.openscience.cdk.qsar.Descriptor#getParameterNames()
     */
    public String[] getParameterNames() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.qsar.Descriptor#getParameterType(java.lang.String)
     */
    public Object getParameterType(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.qsar.Descriptor#setParameters(java.lang.Object[])
     */
    public void setParameters(Object[] params) throws CDKException {

    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.qsar.Descriptor#getParameters()
     */
    public Object[] getParameters() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.qsar.IDescriptor#calculate(org.openscience.cdk.interfaces.AtomContainer)
     */
    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
        double[] eval = doCalculation(container);
        //double min = eval[0];
        DoubleArrayResult value = new DoubleArrayResult(eval.length);
        for (int i=0; i < eval.length; i++)
            value.add(eval[i]);
        return new DescriptorValue(getSpecification(), getParameterNames(), 
                getParameters(),value,new String[]{
        	CrossSectionalDiameterDescriptor.MAX_LENGTH,CrossSectionalDiameterDescriptor.MAX_DIAMETER,CrossSectionalDiameterDescriptor.MIN_DIAMETER});        
    }
    /**
     * Transforms coordinates to PCA space (this making sizes invariant to rotation)
     * Sizes are ordered (max first)
     * @param container
     * @return length, height and depth in double[3] array
     * @throws CDKException
     */
    public double[] doCalculation(IAtomContainer container) throws CDKException {
    	/*
        IsotopeFactory factory = null;
        try {
            factory = IsotopeFactory.getInstance();
        } catch (Exception e) {
            //logger.debug(e);
        }
		
        double ccf = 1.000138;
        
        double eps = 1e-5;
        */

        double[][] imat = new double[container.getAtomCount()][3];
        Point3d centerOfMass = GeometryToolsInternalCoordinates.get3DCenter(container);

                for (int k = 0; k < container.getAtomCount(); k++) {
                    double[] xyz = new double[3];
                    //double mass = 0.0;
                    //double radius = 0.0;

                    IAtom currentAtom = container.getAtom(k);
                    if (currentAtom.getPoint3d() == null) {
                        throw new CDKException("Atom "+k+" did not have any 3D coordinates. These are required");
                    }

                    //mass = factory.getMajorIsotope( currentAtom.getSymbol() ).getMassNumber();

                    //radius = centerOfMass.distance( currentAtom.getPoint3d() );

                    imat[k][0] = currentAtom.getPoint3d().x - centerOfMass.x;
                    imat[k][1] = currentAtom.getPoint3d().y - centerOfMass.y;
                    imat[k][2] = currentAtom.getPoint3d().z - centerOfMass.z;

                    if (Double.isNaN(imat[k][0]) || Double.isNaN(imat[k][1]) || Double.isNaN(imat[k][2])) 
                    	throw new CDKException("3D coordinate is NaN");
           
                }
                
    			Matrix m = new Matrix(imat);
    			try {
    				
	    			System.out.println("SingularValueDecomposition "+container.getAtomCount());
	    			long now = System.currentTimeMillis();
	    		    SingularValueDecomposition svd = m.svd();
	    		    now  = System.currentTimeMillis() - now;
	    		    System.out.println("SingularValueDecomposition DONE "+Long.toString(now) + " ms.");
	    		    Matrix pcapoints =  m.times(svd.getV());
    		    
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
    			} catch (Exception x) {
    				throw new CDKException(x.getMessage());
    			}


        

    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Molecule size";
    }
    
    @Override
    public int hashCode() {
    	String c = getClass().getName();
    	int hash = 7;
    	int var_code = (null == c ? 0 : c.hashCode());
    	hash = 31 * hash + var_code; 
    	return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
    	return (obj instanceof SizeDescriptor);
    }

	public IDescriptorResult getDescriptorResultType() {
		return new DoubleArrayResult();
	}
}
