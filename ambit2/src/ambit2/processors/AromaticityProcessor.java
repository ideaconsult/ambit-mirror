/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.processors;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.processors.structure.AllRingsFinderTimeoutEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.exceptions.AmbitException;

public class AromaticityProcessor extends DefaultAmbitProcessor {
	public AromaticityProcessor() {
		super();
	}
	@Override
	public Object process(Object object) throws AmbitException {
		if (object instanceof IAtomContainer)
			try {
				IAtomContainer ac = (IAtomContainer) object;
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
				CDKHueckelAromaticityDetector.detectAromaticity(ac);
				
			} catch (CDKException x) {
				throw new AmbitException(x);
			}
		return object;
	}

	public IAmbitEditor getEditor() {
	    	return null;
	}	
	   @Override
	public String toString() {
		return "Aromaticity detection";
	}
}


