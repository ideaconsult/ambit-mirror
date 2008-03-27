/* SubstructureSearchProcessor.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-22 
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

package ambit2.database.processors;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.exceptions.AmbitException;
import ambit2.processors.DefaultAmbitProcessor;
import ambit2.processors.IAmbitResult;
import ambit2.ui.editors.DefaultProcessorEditor;
import ambit2.ui.editors.IAmbitEditor;

/**
 * Substructure search. For efficiency, use it with {@link ambit2.database.search.DbSubstructurePrescreenReader}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-22
 */
public class SubstructureSearchProcessor extends DefaultAmbitProcessor {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4874638903751677946L;
	protected IAtomContainer query = null;
    /**
     * 
     */
    public SubstructureSearchProcessor(IAtomContainer mol) {
        super();
        this.query = mol;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#process(java.lang.Object)
     */
    public Object process(Object object) throws AmbitException {
        if (object instanceof IAtomContainer) {
            IAtomContainer mol = (IAtomContainer) object;
            try {
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
				CDKHueckelAromaticityDetector.detectAromaticity(mol);                
                if ((mol != null) && (UniversalIsomorphismTester.isSubgraph(mol,query))) {
                    return mol;
                }
            } catch (CDKException x) {
                throw new AmbitException(x);
            }
        } return null;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#createResult()
     */
    public IAmbitResult createResult() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#getResult()
     */
    public IAmbitResult getResult() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#setResult(ambit2.processors.IAmbitResult)
     */
    public void setResult(IAmbitResult result) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#close()
     */
    public void close() {
        // TODO Auto-generated method stub

    }
    public IAmbitEditor getEditor() {

    	return new DefaultProcessorEditor(this);
    }
    @Override
    public String toString() {
    	return "Substructure";
    }

}
