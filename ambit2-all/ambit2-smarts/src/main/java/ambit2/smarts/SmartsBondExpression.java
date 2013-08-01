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

import java.util.Vector;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class SmartsBondExpression extends SMARTSBond
{
	private static final long serialVersionUID = -93456789328678678L;
	public Vector<Integer> tokens = new Vector<Integer>();
	
    public SmartsBondExpression() {
    }    
    
	public boolean matches(IBond bond) {
		SmartsLogicalExpression sle = new SmartsLogicalExpression();
		for (int i = 0; i< tokens.size(); i++)
		{
			int tok = tokens.get(i).intValue();
			if (tok < SmartsConst.LO)
			{
				sle.addArgument(getArgument(tok, bond));
			}	
			else
				sle.addLogOperation(tok - SmartsConst.LO);
		}
		return (sle.getValue()); 
    };
    
    boolean getArgument(int boType, IBond bond)
    {	
    	switch (boType)
    	{
    	case SmartsConst.BT_SINGLE:
    		if ((bond.getOrder() == IBond.Order.SINGLE) && 
    			(!bond.getFlag(CDKConstants.ISAROMATIC))   )
    			return(true);    		
    		break;	    		
    	case SmartsConst.BT_DOUBLE:
    		if (bond.getOrder() == IBond.Order.DOUBLE)
    			return(true);    		
    		break;
    	case SmartsConst.BT_TRIPLE:	
    		if (bond.getOrder() == IBond.Order.TRIPLE)
    			return(true);    		
    		break;	    		
    	case SmartsConst.BT_AROMATIC:	
    		if (bond.getFlag(CDKConstants.ISAROMATIC))
    			return(true);
    		break;    		
    	case SmartsConst.BT_RING:	
    		//if (bond.getFlag(CDKConstants.ISINRING))
    		//	return(true);
    		if (RingQueryBond.isRingBond(bond))
    			return true;
    		break;    		
    	case SmartsConst.BT_UP:
    	case SmartsConst.BT_DOWN:
    	case SmartsConst.BT_UPUNSPEC:
    	case SmartsConst.BT_DOWNUNSPEC:
    		//A directional bond in a more complex bond expression does not make 
    		//a lot of sence. Hence it is treated as a single one     		
    		if (bond.getOrder() == IBond.Order.SINGLE)
    			return(true);    		
    		break;
    	case SmartsConst.BT_CIS:    		
    		break;
    	case SmartsConst.BT_CISUNSPEC:
    		break;
    	case SmartsConst.BT_TRANS:
    		break;
    	case SmartsConst.BT_TRANSUNSPEC:
    		break;	    		
    	}
    	
    	
    	return(false);
    }
    
    public boolean isIdenticalTo(SmartsBondExpression bondExpression)
    {
    	int nTokens = tokens.size();
    	
    	if (nTokens != bondExpression.tokens.size())
    		return false;
    	
    	for (int i = 0; i < nTokens; i++)
    	{
    		if (tokens.get(i).intValue() != bondExpression.tokens.get(i).intValue())
    			return false;
    	}
    	
    	return true;
    }
        

    public String toString() {
		
		StringBuffer sb = new StringBuffer(); 
		for (int i = 0; i < tokens.size(); i++)
		{
			int tok = tokens.get(i).intValue();
			if (tok < SmartsConst.LO)
			{	
				if (tok < SmartsConst.BT_UPUNSPEC)
					sb.append(SmartsConst.BondChars[tok]);
				else
				if (tok == SmartsConst.BT_UPUNSPEC)	
					sb.append("/?");
				else
					sb.append("\\?");
			}	
			else
				sb.append(SmartsConst.LogOperationChars[tok - SmartsConst.LO]);
		}			
		return sb.toString();	
    }
}
