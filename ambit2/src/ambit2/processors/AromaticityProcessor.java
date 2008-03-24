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
import org.openscience.cdk.geometry.AtomTools;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.exceptions.AmbitException;


/**
 * TODO - add atom configuration
 * Detects aromaticity
 * @author nina
 *
 */
public class AromaticityProcessor extends DefaultAmbitProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8862107297592763780L;
	@Override
	public Object process(Object object) throws AmbitException {
		if (object instanceof IAtomContainer)
			try {
				
				CDKHueckelAromaticityDetector.detectAromaticity((IAtomContainer)object);
			} catch (CDKException x) {
				throw new AmbitException(x);
			}
		return object;
	}

	   @Override
	public String toString() {
		return "Aromaticity detection";
	}
}


