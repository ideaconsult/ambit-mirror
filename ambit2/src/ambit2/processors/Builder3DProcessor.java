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

package ambit2.processors;

import ambit2.exceptions.AmbitException;

/**
 * Generates 3D coordinates of a molecule by {@link org.openscience.cdk.modeling.builder3d.ModelBuilder3D}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Jul 12, 2006
 */
public class Builder3DProcessor extends DefaultAmbitProcessor {
    /**
     * 
     */
	public Builder3DProcessor() {
        super();

    }

    /* (non-Javadoc)
     * @see ambit2.processors.DefaultAmbitProcessor#process(java.lang.Object)
     */
    public Object process(Object object) throws AmbitException {
 
        throw new AmbitException("Not implemented"); 
    }
    /* (non-Javadoc)
     * @see ambit2.processors.DefaultAmbitProcessor#toString()
     */
    public String toString() {
        return "Generates 3D coordinates by smi23d";
    }
}
