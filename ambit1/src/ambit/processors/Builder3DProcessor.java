/* Builder3DProcessor.java
 * Author: Nina Jeliazkova
 * Date: Jul 12, 2006 
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

package ambit.processors;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.modeling.builder3d.ModelBuilder3D;
import org.openscience.cdk.modeling.builder3d.TemplateHandler3D;
import org.openscience.cdk.tools.HydrogenAdder;

import ambit.exceptions.AmbitException;
import ambit.io.FastTemplateHandler3D;

/**
 * Generates 3D coordinates of a molecule by {@link org.openscience.cdk.modeling.builder3d.ModelBuilder3D}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Jul 12, 2006
 */
public class Builder3DProcessor extends DefaultAmbitProcessor {
    ModelBuilder3D mb3d;
	HydrogenAdder hAdder=new HydrogenAdder();
	TemplateHandler3D templateHandler;
    /**
     * 
     */
	public Builder3DProcessor() {
        super();
        mb3d = null;
        this.templateHandler = FastTemplateHandler3D.getInstance();
    }
    public Builder3DProcessor(TemplateHandler3D templateHandler) {
        super();
        mb3d = null;
        this.templateHandler = templateHandler;
    }
    /* (non-Javadoc)
     * @see ambit.processors.DefaultAmbitProcessor#process(java.lang.Object)
     */
    public Object process(Object object) throws AmbitException {
        if (object instanceof IMolecule) {
            IMolecule mol = (IMolecule) object;
            try {
		        hAdder.addExplicitHydrogensToSatisfyValency(mol);
		        if (mb3d == null) {
		            mb3d = new ModelBuilder3D();
		            if (templateHandler == null) templateHandler = FastTemplateHandler3D.getInstance();
			        mb3d.setTemplateHandler(templateHandler);
					mb3d.setForceField("mm2");
		        }
				mb3d.setMolecule(mol,false);
				mb3d.generate3DCoordinates();
				mol = mb3d.getMolecule();
				mol.setProperty(CDKConstants.REMARK, mb3d.getClass().getName());
				
				for (int i=0;i < mol.getAtomCount();i++) {
					IAtom a = mol.getAtomAt(i);
					if (Double.isNaN(a.getX3d()) || 
						(Double.isNaN(a.getY3d())) ||
						(Double.isNaN(a.getZ3d())))  throw new AmbitException("3D coordinate is NaN");
				}
				
				return mol; 
            } catch (Exception x) {
            	for (int i=0;i < mol.getAtomCount();i++) {
					IAtom a = mol.getAtomAt(i);
					a.setPoint3d(null);
				}
                throw new AmbitException(x);
            }
        } 
        return object; 
    }
    /* (non-Javadoc)
     * @see ambit.processors.DefaultAmbitProcessor#toString()
     */
    public String toString() {
        return "Generates 3D coordinates by org.openscience.cdk.modeling.builder3d.ModelBuilder3D";
    }
}
