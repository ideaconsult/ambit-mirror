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
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
//import org.openscience.cdk.interfaces.IRingSet;
//import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class SmartsAtomExpression extends SMARTSAtom 
{
	private static final long serialVersionUID = -123453467895564563L;
	public Vector<SmartsExpressionToken> tokens = new Vector<SmartsExpressionToken>();
	//Each recursive Smarts is represented with a string and a separate QueryAtomContainer 
	public Vector<String> recSmartsStrings = new Vector<String>();
	public Vector<QueryAtomContainer> recSmartsContainers = new Vector<QueryAtomContainer>();
	
	//This data must be filled from outside for each different target 
	//in order to be used for mathching. 
	//If this data is not filled (i.e. null pointer) the current token is assumed to be 'TRUE' 
	public Vector<Vector<IAtom>> recSmartsMatches = null;
	
	public SmartsAtomExpression() {
    }
	
	public boolean matches(IAtom atom) {
		SmartsLogicalExpression sle = new SmartsLogicalExpression();
		for (int i = 0; i< tokens.size(); i++)
		{
			SmartsExpressionToken tok = tokens.get(i);
			if (tok.type < SmartsConst.LO)
			{	
				sle.addArgument(getArgument(tok, atom));
			}	
			else
				sle.addLogOperation(tok.type - SmartsConst.LO);
		}
		return (sle.getValue()); 
		
    };
    
    public SmartsExpressionToken getLastToken()
    {
    	return(tokens.lastElement());
    }
    
    boolean getArgument(SmartsExpressionToken tok, IAtom atom)
    {
    	switch (tok.type)
    	{
    	case SmartsConst.AP_ANY:
    		return(true);
    		
    	case SmartsConst.AP_a:
    		if (atom.getFlag(CDKConstants.ISAROMATIC))
    		{
    			if (tok.param == 0)
    				return(true);
    			else
    				if (SmartsConst.elSymbols[tok.param].equals(atom.getSymbol()))
    					return(true);
    				else
    					return(false);
    		}
    		else
    			return(false);
    		
    	case SmartsConst.AP_A:    		    		
    		if (!atom.getFlag(CDKConstants.ISAROMATIC))
    		{
    			if (tok.param == 0)
    				return(true);
    			else
    				if (SmartsConst.elSymbols[tok.param].equals(atom.getSymbol()))
    					return(true);
    				else
    					return(false);
    		}
    		else
    			return(false);
    		
    	case SmartsConst.AP_D:
    		if (tok.param == atom.getFormalNeighbourCount())
    			return(true);
    		else	
    			return(false);
    		
    	case SmartsConst.AP_v:
    		if (tok.param == atom.getValency())
    			return(true);
    		else	
    			return(false);
    		
    	case SmartsConst.AP_X:
    	{	
    		Integer hci = atom.getHydrogenCount();
    		int hc = 0;
    		if (hci != null)
    			hc = hci.intValue();
    		if (tok.param == atom.getFormalNeighbourCount() + hc)
    			return(true);
    		else	
    			return(false);	
    	}
    	case SmartsConst.AP_H:
    	{	
    		Integer hci = atom.getHydrogenCount();
    		int totalH = 0;
    		if (hci != null)
    			totalH = hci.intValue();    		
    		
    		Integer explicitH = (Integer)atom.getProperty("ExplicitH");
    		if (explicitH != null)
    			totalH+=explicitH.intValue();
    		if (tok.param == totalH)
    			return(true);
    		else	
    			return(false);	
    	}	
    	case SmartsConst.AP_R:    		 
    		int atomRings[] = (int[])atom.getProperty("RingData");
    		return(match_R(atomRings, tok.param, atom));
    	
    	case SmartsConst.AP_r:    		 
    		int atomRings2[] = (int[])atom.getProperty("RingData");    		
    		return(match_r(atomRings2, tok.param, atom));
    		
    	case SmartsConst.AP_Mass:    		
    		if (atom.getMassNumber()== 0) 
    			return(false); //When atom mass is unspecified false is returned
    		if (atom.getMassNumber()== tok.param)
    			return(true);
    		else
    			return(false);
    		
    	case SmartsConst.AP_Charge:
    		if (atom.getFormalCharge() == tok.param)
    			return(true);
    		else
    			return(false);
    		
    	case SmartsConst.AP_AtNum:
    		if (SmartsConst.elSymbols[tok.param].equals(atom.getSymbol()))
				return(true);
			else
				return(false);
    		
    	case SmartsConst.AP_Chiral:	
    		//It is assusmed that PLUS is R and MINUS is S
    		if (tok.param == SmartsConst.ChC_R)
    		{
    			if (atom.getStereoParity() == CDKConstants.STEREO_ATOM_PARITY_MINUS)
    				return (false);
    			else
    				if (atom.getStereoParity() == CDKConstants.STEREO_ATOM_PARITY_PLUS)
        				return (true);
    				else
    					return(true); //pariti is undefined
    		}
    		else
    			if (tok.param == SmartsConst.ChC_S)
        		{
    				if (atom.getStereoParity() == CDKConstants.STEREO_ATOM_PARITY_MINUS)
        				return (true);
        			else
        				if (atom.getStereoParity() == CDKConstants.STEREO_ATOM_PARITY_PLUS)
            				return (false);
        				else
        					return(true); //parity is undefined
        		}
    			else
    				return(true); //undefined chirality
    		
    	case SmartsConst.AP_Recursive:  
    		//System.out.println("Match recursive token");
    		if (recSmartsMatches == null)    		
				return(true);
			else
			{	
				//System.out.println("recSmartsMatches.size()=" + recSmartsMatches.size());
				Vector<IAtom> atomMaps = recSmartsMatches.get(tok.param);				
				for (int i = 0; i < atomMaps.size(); i++)
					if (atomMaps.get(i) == atom)
						return(true);
				return(false);
			}
    	case SmartsConst.AP_x: 
    		return(match_x(tok.param, atom));
    		    		
    	default:
    		return(true);
    	}
    }

    String tokenToString(SmartsExpressionToken tok)
    {
    	if (tok.type >= SmartsConst.LO)
    		return(Character.toString(SmartsConst.LogOperationChars[tok.type-SmartsConst.LO]));
    	else
    	{
    		switch (tok.type)
    		{
    		case SmartsConst.AP_ANY:
    			return("*");
    		case SmartsConst.AP_a:
    			if (tok.param > 0) 
    				return(SmartsConst.elSymbols[tok.param].toLowerCase());
    			else
    				return("a");
    		case SmartsConst.AP_A:    			
    			if (tok.param > 0) 
    				return(SmartsConst.elSymbols[tok.param]);
    			else
    				return("A");
    		case SmartsConst.AP_D:
    		case SmartsConst.AP_H:
    		case SmartsConst.AP_h:
    		case SmartsConst.AP_R:
    		case SmartsConst.AP_r:
    		case SmartsConst.AP_v:
    		case SmartsConst.AP_X:
    		case SmartsConst.AP_x:
    			String s = Character.toString(SmartsConst.AtomPrimChars[tok.type]);
    			if (tok.param != 1)
    				s+= tok.param;
    			return(s);
    		
    		case SmartsConst.AP_Charge:
    			String s1;
    			if (tok.param > 0)
    				s1 = "+";
    			else
    				s1 = "-";
    			if (Math.abs(tok.param) != 1)
    				s1+=Math.abs(tok.param);
    			return(s1);
    			
    		case SmartsConst.AP_AtNum:
    			return("#"+tok.param);
    			
    		case SmartsConst.AP_Chiral:
    			if (tok.param == SmartsConst.ChC_AntiClock)
    				return("@");
    			else
    				return("@@");
    			
    		case SmartsConst.AP_Mass:
    			return(""+tok.param);
    		
    		case SmartsConst.AP_Recursive:
    			//return("$("+(String)recSmartsStrings.get(tok.param)+")");
    			SmartsHelper sw = new SmartsHelper();    			
    			return("$("+sw.toSmarts((QueryAtomContainer)recSmartsContainers.get(tok.param))+")");
    		}
    	}
    	return("");
    }
    
    public boolean match_R(int atomRings[], int param,  IAtom atom)
    {
    	if (atomRings == null)
		{
			if (param == 0)
				return(true);
			else
				return(false);
		}
		else
		{	
			if (param == -1) //This is a special value for default definition "R" only without an integer
			{
				if (atomRings.length > 0)
					return(true);
				else
					return(false);
			}
			else
			{
				if (param == atomRings.length)
					return(true);
				else
					return(false);
			}
		}
    }
    
    public boolean match_r(int atomRings[], int param,  IAtom atom)
    {
    	if (atomRings == null)
		{
			if (param == 0)
				return(true);
			else
				return(false);
		}
		else
		{
			if (param < 3) // value 1 is possible here 
			{
				if (atomRings.length > 0)
					return(true);
				else
					return(false);
			}
			else
			{
				for (int i = 0; i < atomRings.length; i++)
				{
					if (atomRings[i] == param)
						return(true);
				}
				return(false);
			}
		}
    }
    
    public boolean match_x(int param, IAtom atom)
    {
    	int atomRings[] = (int[])atom.getProperty("RingData2");
    	if (atomRings == null)
			return(false);
		
    	//System.out.print("target atom rings: ");
    	//SmartsHelper.printIntArray(atomRings);
    	
    	IAtomContainer mol = (IAtomContainer)atom.getProperty("ParentMoleculeData");
    	List ca = mol.getConnectedAtomsList(atom);
    	int rbonds = 0;
    	for (int i = 0; i < ca.size(); i++)
    	{
    		int atrings[] = (int[])((IAtom)ca.get(i)).getProperty("RingData2");
    		if (atrings == null)
    			continue;
    		//System.out.print("neigbour ("+i+")atom rings: ");
        	//SmartsHelper.printIntArray(atrings);
    		if (commonRingBond(atomRings, atrings))
    			rbonds++;	
    	}
    	
    	//Value -1 is interpreted as "at least one " - the default param value
    	if (param == -1)
    	{	
    		if (rbonds > 0)
    			return(true);
    		else
    			return(false);
    	}	
    	
    	return(param == rbonds);
    }
    
    boolean commonRingBond(int atomRingData1[], int atomRingData2[])
    {    	
    	for (int i = 0; i < atomRingData1.length; i++)
    		for (int k = 0; k < atomRingData2.length; i++)
    		{	
    			if (atomRingData1[i] == atomRingData1[k])
    				return(true);
    		}
    	
    	return(false);
    }
    
    
    public String toString() 
    {
    	StringBuffer sb = new StringBuffer();
    	sb.append("[");
    	for (int i=0; i < tokens.size(); i++)
    		sb.append(tokenToString(tokens.get(i)));
    	sb.append("]");
    	return sb.toString();
    }
}
