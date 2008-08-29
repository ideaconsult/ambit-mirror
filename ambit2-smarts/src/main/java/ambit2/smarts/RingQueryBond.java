/*
Copyright (C) 2007-2008  

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

package ambit2.smarts;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class RingQueryBond extends SMARTSBond
{
	private static final long serialVersionUID = -90236069308675679L;
	
	public RingQueryBond() {
    }
	
	public boolean matches(IBond bond) 
	{		 
		//if (bond.getFlag(CDKConstants.ISINRING)) 
		//	return true;
		if (RingQueryBond.isRingBond(bond))
			return true;
		return false;
    };
    
    public static boolean isRingBond(IBond bond)
    {
    	//This function uses atom ring info for the two atoms of this bond
    	//in order to determine wether this bond is a ring bond
    	int atomRings0[] = (int[])bond.getAtom(0).getProperty("RingData");
    	int atomRings1[] = (int[])bond.getAtom(1).getProperty("RingData"); 
    	if ((atomRings0 == null) || (atomRings1 == null))
    		return(false);
    	for (int i = 0; i < atomRings0.length; i++)
    		for (int j = 0; j < atomRings1.length; j++)
    			if (atomRings0[i] == atomRings1[j])
    				return(true);
    	
    	return(false);
    }
}
