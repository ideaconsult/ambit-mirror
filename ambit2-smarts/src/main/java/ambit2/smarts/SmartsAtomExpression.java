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

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class SmartsAtomExpression extends SMARTSAtom {
	public List<SmartsExpressionToken> tokens = new ArrayList<SmartsExpressionToken>();
	// Each recursive Smarts is represented with a string and a separate
	// QueryAtomContainer
	public List<String> recSmartsStrings = new ArrayList<String>();
	public List<IQueryAtomContainer> recSmartsContainers = new ArrayList<IQueryAtomContainer>();

	// This data must be filled from outside for each different target
	// in order to be used for mathching.
	// If this data is not filled (i.e. null pointer) the current token is
	// assumed to be 'TRUE'
	public List<List<IAtom>> recSmartsMatches = null;
	
	//This is used if tetrahedral chiral atom or 
	//extended tetrahedral chiral token is present 
	public List<IAtom> stereoLigands = null;
	
	public ExtendedChiralityInfo extChirInfo = null;

	//This is the expression without stereo information 
	//stereo tokens and preceding or next logical operations are removed
	//This is used to match the atom regardless of the stereo information
	//The stereo info match is performed later.
	public List<SmartsExpressionToken> stereoRemovedTokens = null;
	
	//The indices of the logical tokens that contain stereo information
	public int stereoTokenIndices[] = null;
	
	public boolean hasImplicitHStereoLigand = false;
	
	
	public SmartsAtomExpression(IChemObjectBuilder builder) {
		super(builder);
	}

	public boolean matches(IAtom atom) {
		
		if (stereoTokenIndices == null)
		{	
			SmartsLogicalExpression sle = new SmartsLogicalExpression();
			for (int i = 0; i < tokens.size(); i++) {
				SmartsExpressionToken tok = tokens.get(i);
				if (tok.type < SmartsConst.LO) {
					sle.addArgument(getArgument(tok, atom));
				} else
					sle.addLogOperation(tok.type - SmartsConst.LO);
			}
			return (sle.getValue());
		}
		
		//Handle the case when stereo tokens are present
		//Two possible case are checked: ChC_AntiClock and ChC_Clock 
		//If at least one of these case gives true then
		//it is a POSSIBLE MATCH
		//The "YES" result for the match is confirmed at the 
		//final stereo check within IsomorphismTester class
		
		//Case I
		int orientation = SmartsConst.ChC_AntiClock;
		SmartsLogicalExpression sle = new SmartsLogicalExpression();
		for (int i = 0; i < tokens.size(); i++) 
		{
			SmartsExpressionToken tok = tokens.get(i);
			if (tok.type < SmartsConst.LO) 
			{
				if (tok.type == SmartsConst.AP_Chiral )
					sle.addArgument(tok.param ==orientation);
				else
					sle.addArgument(getArgument(tok, atom));
			} 
			else
				sle.addLogOperation(tok.type - SmartsConst.LO);
		}
		
		boolean res = sle.getValue();
		if (res)
			return true;   
		
		//Case II
		orientation = SmartsConst.ChC_Clock;
		sle = new SmartsLogicalExpression();
		for (int i = 0; i < tokens.size(); i++) 
		{
			SmartsExpressionToken tok = tokens.get(i);
			if (tok.type < SmartsConst.LO) 
			{
				if (tok.type == SmartsConst.AP_Chiral )
					sle.addArgument(tok.param ==orientation);
				else
					sle.addArgument(getArgument(tok, atom));
			} 
			else
				sle.addLogOperation(tok.type - SmartsConst.LO);
		}

		res = sle.getValue();
		return res;   
		
		/*
		if (stereoTokenIndices.length > 3)
			//This case is very rare.
			//The smarts atom expression contains too many stereo tokens
			//It is assumed here that the matching result is true
			//and then the result will be checked on the final stereo check
			//within IsomorphismTester class
			return true;  
		else
		{
			
			//Handling up to 3 stereo tokens
			//all possible combinations of the boolean values
			//of the stereo tokens are checked together with the other
			//non stereo tokens.
			//If at least one of these combinations gives true then
			//it is a POSSIBLE MATCH
			//The "YES" result for the match is confirmed at the 
			//final stereo check within IsomorphismTester class
			
			int comb[][] = null;
			switch (stereoTokenIndices.length)
			{
			case 1: 
				comb = BinaryCombinations.c1;
				break;
			case 2: 
				comb = BinaryCombinations.c2;
				break;
			case 3: 
				comb = BinaryCombinations.c3;
				break;	
			}
			
			for (int combNum = 0; combNum < comb.length; combNum++)
			{
				SmartsLogicalExpression sle = new SmartsLogicalExpression();
				int curStereoToken = 0;
				for (int i = 0; i < tokens.size(); i++) 
				{
					SmartsExpressionToken tok = tokens.get(i);
					if (tok.type < SmartsConst.LO) 
					{
						if (tok.type == SmartsConst.AP_Chiral )
						{	
							sle.addArgument(comb[combNum][curStereoToken] != 0);
							curStereoToken++;
						}
						else
							sle.addArgument(getArgument(tok, atom));
					} 
					else
						sle.addLogOperation(tok.type - SmartsConst.LO);
				}
				
				boolean res = sle.getValue();
				if (res)
					return true; //found at least one combination of all possible stereo match results
								//that gives true for the entire expression  
			}
			return false;
		}
		
		*/

	};
	
	
	public boolean stereoMatch(IAtom atom, int targetStereo) 
	{
		SmartsLogicalExpression sle = new SmartsLogicalExpression();
		for (int i = 0; i < tokens.size(); i++) 
		{
			SmartsExpressionToken tok = tokens.get(i);
			if (tok.type < SmartsConst.LO) 
			{
				if (tok.type == SmartsConst.AP_Chiral )
					sle.addArgument(tok.param == targetStereo);
				else
					sle.addArgument(getArgument(tok, atom));
			} 
			else
				sle.addLogOperation(tok.type - SmartsConst.LO);
		}
		
		return sle.getValue();
	}
	
	

	public SmartsExpressionToken getLastToken() {
		return (tokens.get(tokens.size() - 1));
	}

	boolean getArgument(SmartsExpressionToken tok, IAtom atom) {
		switch (tok.type) {
		case SmartsConst.AP_ANY:
			return (true);

		case SmartsConst.AP_a:
			if (atom.getFlag(CDKConstants.ISAROMATIC)) {
				if (tok.param == 0)
					return (true);
				else if (SmartsConst.elSymbols[tok.param].equals(atom
						.getSymbol()))
					return (true);
				else
					return (false);
			} else
				return (false);

		case SmartsConst.AP_A:
			if (!atom.getFlag(CDKConstants.ISAROMATIC)) {
				if (tok.param == 0)
					return (true);
				else if (SmartsConst.elSymbols[tok.param].equals(atom
						.getSymbol()))
					return (true);
				else
					return (false);
			} else
				return (false);

		case SmartsConst.AP_D:
			if (tok.param == atom.getFormalNeighbourCount())
				return (true);
			else
				return (false);

		case SmartsConst.AP_v:
			if (tok.param == atom.getValency())
				return (true);
			else
				return (false);

		case SmartsConst.AP_X: {
			/*
			 * https://sourceforge.net/tracker/?func=detail&aid=3020065&group_id=
			 * 20024&atid=120024 Integer hci = atom.getHydrogenCount();
			 */

			Integer hci = atom.getImplicitHydrogenCount();
			int hc = 0;
			if (hci != null)
				hc = hci.intValue();

			if (tok.param == atom.getFormalNeighbourCount() + hc)
				return (true);
			else
				return (false);
		}
		case SmartsConst.AP_H: {
			/*
			 * https://sourceforge.net/tracker/?func=detail&aid=3020065&group_id=
			 * 20024&atid=120024 Integer hci = atom.getHydrogenCount();
			 */
			Integer hci = atom.getImplicitHydrogenCount();
			int totalH = 0;
			if (hci != null)
				totalH = hci.intValue();

			Integer explicitH = (Integer) atom
					.getProperty(CMLUtilities.ExplicitH);
			if (explicitH != null)
				totalH += explicitH.intValue();
			if (tok.param == totalH)
				return (true);
			else
				return (false);
		}
		case SmartsConst.AP_R:
			int atomRings[] = (int[]) atom.getProperty(CMLUtilities.RingData);
			return (match_R(atomRings, tok.param, atom));

		case SmartsConst.AP_r:
			int atomRings2[] = (int[]) atom.getProperty(CMLUtilities.RingData);
			return (match_r(atomRings2, tok.param, atom));

		case SmartsConst.AP_Mass:
			// When atom mass is unspecified false is returned
			if (atom.getMassNumber() == null)
				return (false);
			if (atom.getMassNumber() == 0)
				return (false);

			if (atom.getMassNumber() == tok.param)
				return (true);
			else
				return (false);

		case SmartsConst.AP_Charge:
			if (atom.getFormalCharge() == tok.param)
				return (true);
			else
				return (false);

		case SmartsConst.AP_AtNum:
			if (SmartsConst.elSymbols[tok.param].equals(atom.getSymbol()))
				return (true);
			else
				return (false);

		case SmartsConst.AP_Chiral:
			// Currently chirality is not taken into account
			// TODO - use stereo elements information in IAtomContainer
			return true;

			/*
			 * //The following code does not work. It will throw null pointer
			 * exception and the assumption is not OK //It is assumed that PLUS
			 * is R and MINUS is S if (tok.param == SmartsConst.ChC_R) { if
			 * (atom.getStereoParity() == CDKConstants.STEREO_ATOM_PARITY_MINUS)
			 * return (false); else if (atom.getStereoParity() ==
			 * CDKConstants.STEREO_ATOM_PARITY_PLUS) return (true); else
			 * return(true); //pariti is undefined } else if (tok.param ==
			 * SmartsConst.ChC_S) { if (atom.getStereoParity() ==
			 * CDKConstants.STEREO_ATOM_PARITY_MINUS) return (true); else if
			 * (atom.getStereoParity() == CDKConstants.STEREO_ATOM_PARITY_PLUS)
			 * return (false); else return(true); //parity is undefined } else
			 * return(true); //undefined chirality
			 */

		case SmartsConst.AP_Recursive:
			// System.out.println("Match recursive token");
			if (recSmartsMatches == null)
				return (true);
			else {
				// System.out.println("recSmartsMatches.size()=" +
				// recSmartsMatches.size());
				List<IAtom> atomMaps = recSmartsMatches.get(tok.param);
				for (int i = 0; i < atomMaps.size(); i++)
					if (atomMaps.get(i) == atom)
						return (true);
				return (false);
			}
		case SmartsConst.AP_x:
			return (match_x(tok.param, atom));

		case SmartsConst.AP_iMOE:
			return (match_iMOE(tok.param, atom));

		case SmartsConst.AP_GMOE:
			return (match_GMOE(tok.param, atom));

		case SmartsConst.AP_XMOE:
			return (match_XMOE(atom));

		case SmartsConst.AP_NMOE:
			return (match_NMOE(atom));

		case SmartsConst.AP_vMOE:
			// System.out.println("vMOE");
			return (match_vMOE(tok.param, atom));

		case SmartsConst.AP_OB_Hybr:
			return (match_OB_Hybr(tok.param, atom));

		default:
			return (true);
		}
	}

	String tokenToString(SmartsExpressionToken tok) {
		if (tok.type >= SmartsConst.LO)
			return (Character.toString(SmartsConst.LogOperationChars[tok.type
					- SmartsConst.LO]));
		else {
			switch (tok.type) {
			case SmartsConst.AP_ANY:
				return ("*");
			case SmartsConst.AP_a:
				if (tok.param > 0)
					return (SmartsConst.elSymbols[tok.param].toLowerCase());
				else
					return ("a");
			case SmartsConst.AP_A:
				if (tok.param > 0)
					return (SmartsConst.elSymbols[tok.param]);
				else
					return ("A");

			case SmartsConst.AP_D:
			case SmartsConst.AP_H:
			case SmartsConst.AP_h:
			case SmartsConst.AP_R:
			case SmartsConst.AP_r:
			case SmartsConst.AP_v:
			case SmartsConst.AP_X:
			case SmartsConst.AP_x:
			case SmartsConst.AP_vMOE:
				String s = Character
						.toString(SmartsConst.AtomPrimChars[tok.type]);
				if (tok.param != 1)
					s += tok.param;
				return (s);

			case SmartsConst.AP_OB_Hybr:
				String sOBHybr = Character
						.toString(SmartsConst.AtomPrimChars[tok.type]);
				sOBHybr += tok.param;
				return (sOBHybr);

			case SmartsConst.AP_iMOE:
				return ("i");

			case SmartsConst.AP_GMOE:
				String sG = "G";
				sG += tok.param;
				return (sG);

			case SmartsConst.AP_XMOE:
				return ("#X");

			case SmartsConst.AP_NMOE:
				return ("#N");

			case SmartsConst.AP_Charge:
				String s1;
				if (tok.param > 0)
					s1 = "+";
				else
					s1 = "-";
				if (Math.abs(tok.param) != 1)
					s1 += Math.abs(tok.param);
				return (s1);

			case SmartsConst.AP_AtNum:
				return ("#" + tok.param);

			case SmartsConst.AP_Chiral:
				if (tok.param == SmartsConst.ChC_AntiClock)
					return ("@");
				else
					return ("@@");

			case SmartsConst.AP_Mass:
				return ("" + tok.param);

			case SmartsConst.AP_Recursive:
				if (recSmartsContainers.isEmpty())
					return ("$()");
				// return("$("+(String)recSmartsStrings.get(tok.param)+")");
				SmartsHelper sw = new SmartsHelper(
						SilentChemObjectBuilder.getInstance());
				return ("$("
						+ sw.toSmarts((IQueryAtomContainer) recSmartsContainers
								.get(tok.param)) + ")");
			}
		}
		return ("");
	}

	public boolean match_R(int atomRings[], int param, IAtom atom) {
		if (atomRings == null) {
			if (param == 0)
				return (true);
			else
				return (false);
		} else {
			if (param == -1) // This is a special value for default definition
								// "R" only without an integer
			{
				if (atomRings.length > 0)
					return (true);
				else
					return (false);
			} else {
				if (param == atomRings.length)
					return (true);
				else
					return (false);
			}
		}
	}

	public boolean match_r(int atomRings[], int param, IAtom atom) {
		if (atomRings == null) {
			if (param == 0)
				return (true);
			else
				return (false);
		} else {
			if (param < 3) // value 1 is possible here
			{
				if (atomRings.length > 0)
					return (true);
				else
					return (false);
			} else {
				for (int i = 0; i < atomRings.length; i++) {
					if (atomRings[i] == param)
						return (true);
				}
				return (false);
			}
		}
	}

	public boolean match_x(int param, IAtom atom) {
		int atomRings[] = (int[]) atom.getProperty(CMLUtilities.RingData2);
		if (atomRings == null)
			return (false);

		// System.out.print("target atom rings: ");
		// SmartsHelper.printIntArray(atomRings);

		IAtomContainer mol = (IAtomContainer) atom
				.getProperty("ParentMoleculeData");
		List ca = mol.getConnectedAtomsList(atom);
		int rbonds = 0;
		for (int i = 0; i < ca.size(); i++) {
			int atrings[] = (int[]) ((IAtom) ca.get(i))
					.getProperty(CMLUtilities.RingData2);
			if (atrings == null)
				continue;
			// System.out.print("neigbour ("+i+")atom rings: ");
			// SmartsHelper.printIntArray(atrings);
			if (commonRingBond(atomRings, atrings))
				rbonds++;
		}

		// Value -1 is interpreted as "at least one " - the default param value
		if (param == -1) {
			if (rbonds > 0)
				return (true);
			else
				return (false);
		}

		return (param == rbonds);
	}

	public boolean match_iMOE(int param, IAtom atom) {
		if (atom.getFlag(CDKConstants.ISAROMATIC))
			return (true);

		// Searching for a double or triple bond (participation in a Pi system)
		IAtomContainer mol = (IAtomContainer) atom
				.getProperty("ParentMoleculeData");
		List ca = mol.getConnectedAtomsList(atom);
		for (int i = 0; i < ca.size(); i++) {
			IBond b = mol.getBond(atom, (IAtom) ca.get(i));
			if ((b.getOrder() == IBond.Order.DOUBLE)
					|| (b.getOrder() == IBond.Order.TRIPLE))
				return (true);
		}

		return (false);
	}

	public boolean match_GMOE(int param, IAtom atom) {
		if (param == 4) {
			// any Group IV element [C,Si,Ge,Sn,Pb]
			if ((atom.getSymbol().equals("C"))
					|| (atom.getSymbol().equals("Si"))
					|| (atom.getSymbol().equals("Ge"))
					|| (atom.getSymbol().equals("Sn"))
					|| (atom.getSymbol().equals("Pb")))
				return (true);
			else
				return (false);
		}

		if (param == 6) {
			// any Group VI element [O,S,Se,Te,Po]
			if ((atom.getSymbol().equals("O"))
					|| (atom.getSymbol().equals("S"))
					|| (atom.getSymbol().equals("Ge"))
					|| (atom.getSymbol().equals("Te"))
					|| (atom.getSymbol().equals("Po")))
				return (true);
			else
				return (false);
		}

		if (param == 7) {
			// any Group VII element [F,Cl,Br,I,At]
			if ((atom.getSymbol().equals("F"))
					|| (atom.getSymbol().equals("Cl"))
					|| (atom.getSymbol().equals("Br"))
					|| (atom.getSymbol().equals("I"))
					|| (atom.getSymbol().equals("At")))
				return (true);
			else
				return (false);
		}

		// Other groups so far are nor defined
		return (false);
	}

	public boolean match_XMOE(IAtom atom) {
		// heavy non carbon atom
		if ((atom.getSymbol().equals("H")) || (atom.getSymbol().equals("C")))
			return (false);
		else
			return (true);
	}

	public boolean match_NMOE(IAtom atom) {
		// electronegative element (O, N, F, Cl, Br)
		if ((atom.getSymbol().equals("O")) || (atom.getSymbol().equals("N"))
				|| (atom.getSymbol().equals("F"))
				|| (atom.getSymbol().equals("Cl"))
				|| (atom.getSymbol().equals("Br")))
			return (true);

		return (false);
	}

	public boolean match_vMOE(int param, IAtom atom) {
		// Counting the number of to heavy atoms
		int nB = 0;
		IAtomContainer mol = (IAtomContainer) atom
				.getProperty("ParentMoleculeData");
		List ca = mol.getConnectedAtomsList(atom);
		for (int i = 0; i < ca.size(); i++) {
			IAtom a = (IAtom) ca.get(i);
			if (a.getSymbol().equals("H"))
				continue;
			else
				nB++;
		}
		// System.out.println("nB = " + nB);
		return (nB == param);
	}

	public boolean match_OB_Hybr(int param, IAtom atom) {
		if (atom.getFlag(CDKConstants.ISAROMATIC)) {
			if (param == 2) // sp2
				return (true);
			else
				return (false); // aromatic atoms are not of sp1 nor sp3
								// hybridization
		}

		// Searching for a double or triple bond (participation in a Pi system)
		IAtomContainer mol = (IAtomContainer) atom
				.getProperty("ParentMoleculeData");
		List ca = mol.getConnectedAtomsList(atom);
		int nDB = 0;
		int nTB = 0;
		for (int i = 0; i < ca.size(); i++) {
			IBond b = mol.getBond(atom, (IAtom) ca.get(i));
			if (b.getOrder() == IBond.Order.DOUBLE)
				nDB++;
			else if (b.getOrder() == IBond.Order.TRIPLE)
				nTB++;
		}

		if (param == 3) // sp3 - hybridization
		{
			if ((nDB == 0) && (nTB == 0))
				return (true);
		} else if (param == 2) // sp2 - hybridization
		{
			if ((nDB == 1) && (nTB == 0))
				return (true);
		} else // (param == 1) sp1 - hybridization
		{
			if ((nDB == 2) || (nTB == 1))
				return (true);
		}

		return (false);
	}

	boolean commonRingBond(int atomRingData1[], int atomRingData2[]) {
		for (int i = 0; i < atomRingData1.length; i++)
			for (int k = 0; k < atomRingData2.length; i++) {
				if (atomRingData1[i] == atomRingData1[k])
					return (true);
			}

		return (false);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");

		// Expression tokens
		for (int i = 0; i < tokens.size(); i++)
			sb.append(tokenToString(tokens.get(i)));
		// SMIRKS mapping
		Object prop = this.getProperty("SmirksMapIndex");
		if (prop != null)
			sb.append(":" + prop);

		sb.append("]");
		return sb.toString();
	}
	
	public String stereoRemovedToString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");

		// Expression tokens
		for (int i = 0; i < stereoRemovedTokens.size(); i++)
			sb.append(tokenToString(stereoRemovedTokens.get(i)));
		// SMIRKS mapping
		Object prop = this.getProperty("SmirksMapIndex");
		if (prop != null)
			sb.append(":" + prop);

		sb.append("]");
		return sb.toString();
	}
	
	/*
	public void getStereoTokenIndices()
	{
		List<Integer> stereoTokens = new ArrayList<Integer>();
		for (int i = 0; i < tokens.size(); i++)
		{
			SmartsExpressionToken token = tokens.get(i);
			if (token.type == SmartsConst.AP_Chiral)
				stereoTokens.add(i);
		}
		
		if (stereoTokens.isEmpty())
			stereoTokenIndices = null;
		else
		{
			stereoTokenIndices = new int[stereoTokens.size()];
			for (int i = 0; i < stereoTokens.size(); i++)
				stereoTokenIndices[i] = stereoTokens.get(i);
		}	
	}
	*/
	
	public void getStereoRemovedTokens()
	{
		stereoRemovedTokens = new ArrayList<SmartsExpressionToken>();
		List<Integer> stereoTokens = new ArrayList<Integer>();
		List<Integer> precedingLogicalNos = new ArrayList<Integer>();
		List<Boolean> removePreviousLogOperation = new ArrayList<Boolean>();
		List<Boolean> removeNextLogOperation = new ArrayList<Boolean>();
		
		for (int i = 0; i < tokens.size(); i++)
		{
			SmartsExpressionToken token = tokens.get(i);
			if (token.type == SmartsConst.AP_Chiral)
			{
				stereoTokens.add(i);
				int a[] = analyseStereoToken(i);
				precedingLogicalNos.add(a[0]);
				
				if (a[1] == 0)
					removePreviousLogOperation.add(false);
				else
					removePreviousLogOperation.add(true);
				
				if (a[2] == 0)
					removeNextLogOperation.add(false);
				else
					removeNextLogOperation.add(true);
			}
		}
		
		boolean useToken[] = new boolean[tokens.size()];
		for (int i = 0; i < tokens.size(); i++)
			useToken[i] = true;
		
		for (int i = 0; i < stereoTokens.size(); i++)
		{	
			
			int pos1 = stereoTokens.get(i) - precedingLogicalNos.get(i);
			if (removePreviousLogOperation.get(i))
				pos1--;
			int pos2 = stereoTokens.get(i);
			if (removeNextLogOperation.get(i))	
				pos2++;
			
			for (int pos = pos1; pos <=pos2; pos++)
				useToken[pos] = false;
		}
		
		for (int i = 0; i < tokens.size(); i++)
			if (useToken[i])
				stereoRemovedTokens.add(tokens.get(i));
	}
	
	int getPrecedingLogicalNos(int tokenNum)
	{
		int n = 0;
		for (int i = tokenNum-1; i >= 0; i--)
		{
			if (tokens.get(i).type == (SmartsConst.LO + SmartsConst.LO_NOT))
				n++;
			else
				break;
		}
		
		return n;
	}
	
	/**
	 * @return int[3]
	 * [0] is the number of preceding logical not operations
	 * [1] Flag for removing preceding logical operation
	 * [2] Flag for removing next logical operation
	 */
	int [] analyseStereoToken(int tokenNum){
		int a[] = new int [3];
		a[0] = getPrecedingLogicalNos(tokenNum);
		a[1] = 0; //initialized so that previous operation is not removed (preserved)
		a[2] = 0; //initialized so that next operation is not removed (preserved)
		int prevLogOp = SmartsConst.LO_UNDEFINED;
		int nextLogOp = SmartsConst.LO_UNDEFINED;
		
		int pos = tokenNum - a[0] - 1;
		if (pos >= 0)
			prevLogOp = tokens.get(pos).type - SmartsConst.LO;
		
		pos = tokenNum + 1;
		if (pos < tokens.size())
			nextLogOp = tokens.get(pos).type - SmartsConst.LO;
		
		//The logical operation before/after the stereo token is removed/preserved
		//so that the obtained smarts expression logic could be calculated 
		//regardless the stereo matching. 
		//The precedence of the logical operations is taken into account in order to 
		//decide which logical operations to preserve/remove.
		
		if (prevLogOp == SmartsConst.LO_UNDEFINED)
		{
			//This means that the stereo token is the at beginning of the smarts expression 
			//(negation before the stereo token is not counted)  
			
			//[@,D] --> [D]   (next operation is removed)
			//[@&D] --> [D]   (next operation is removed)
			//[@;D] --> [D]   (next operation is removed)
			//[@]   --> [ ]   (next operation is not removed /nothing to remove/)
			
			if (nextLogOp != SmartsConst.LO_UNDEFINED)
				a[2] = 1;
		}
		
		if (prevLogOp == SmartsConst.LO_AND)
		{
			//[X&@,D] --> [X,D]   (previous operation is removed)
			//[X&@&D] --> [X&D]   (previous operation is removed)
			//[X&@;D] --> [X;D]   (previous operation is removed)
			//[X&@]   --> [X ]    (previous operation is removed)
			a[1] = 1; 
		}
		
		if (prevLogOp == SmartsConst.LO_OR)
		{
			//[X,@,D] --> [X,D]   (previous operation is removed)
			//[X,@&D] --> [X,D]   (previous operation is preserved, next operation is removed)
			//[X,@;D] --> [X;D]   (previous operation is removed)
			//[X,@]   --> [X ]    (previous operation is removed)
			
			if (nextLogOp == SmartsConst.LO_AND)
				a[2] = 1;
			else
				a[1] = 1;
		}
		
		if (prevLogOp == SmartsConst.LO_ANDLO)
		{
			//[X;@,D] --> [X;D]   (next operation is removed)
			//[X;@&D] --> [X;D]   (next operation is removed)
			//[X;@;D] --> [X;D]   (next operation is removed)
			//[X;@]   --> [X ]    (previous operation is removed)
			if (nextLogOp == SmartsConst.LO_UNDEFINED)
				a[1] = 1;
			else
				a[2] = 1;
		}
		
		return a;
	}
}
