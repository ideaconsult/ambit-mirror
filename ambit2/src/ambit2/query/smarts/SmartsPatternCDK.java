/*
Copyright (C) 2005-2007  

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

package ambit2.query.smarts;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;

import ambit2.config.Resources;

/**
 * Encapsulates CDK SMARTS parser.
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class SmartsPatternCDK extends AbstractSmartsPattern<IAtomContainer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -63030468038251612L;
	protected transient SMARTSQueryTool sqt;
	public SmartsPatternCDK() {
		sqt = null;
	}
	public SmartsPatternCDK(String smarts) throws SMARTSException {
		this(smarts,false);
	}
	public SmartsPatternCDK(String smarts,boolean negate) throws SMARTSException {
		setSmarts(smarts);
		setNegate(negate);
	}
	public String getSmarts() {
		if (sqt == null) {
			return "";
		}
		else {
			return sqt.getSmarts();
		}
	}
	public void setSmarts(String smarts) throws SMARTSException {
		try {
			if (sqt == null) {
				sqt = new SMARTSQueryTool(smarts, false);
			}
			else {
				sqt.setSmarts(smarts);
			}
			super.setSmarts(smarts);
		} catch (CDKException x) {
			throw new SMARTSException(x);
		}
	}
	public int hasSMARTSPattern(IAtomContainer mol) throws SMARTSException {
		//System.out.println(getClass().getName() + " hasSMARTSPattern");
    	if (sqt == null) {
    		throw new SMARTSException("Undefined SMARTS pattern");
    	}
    	try {
		        if (sqt.matches(mol)) {
		        	return sqt.countMatches();
		        }
		        	
                    
		            //return sqt.getUniqueMatchingAtoms().size();
		        else {
		        	return 0;
		        }
    	} catch (CDKException x) {
    		throw new SMARTSException(x);
    	}
    }
	public IAtomContainer getObjectToVerify(IAtomContainer mol) {
		return mol;
	}
	public String getImplementationDetails() {
		if (sqt == null)
			{
			return "CDK SMARTS";
			}
		else {
			return sqt.getClass().getName();	
		}
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		try {
			setSmarts(smarts);
		} catch (Exception x) {
			sqt = null;
		}
	}
	public List<List<Integer>> getUniqueMatchingAtoms() throws SMARTSException {
    	if (sqt == null) {
    		throw new SMARTSException(Resources.getString(Resources.SMARTS_UNDEFINED));
    	}
    	return sqt.getUniqueMatchingAtoms(); 
	}
}


