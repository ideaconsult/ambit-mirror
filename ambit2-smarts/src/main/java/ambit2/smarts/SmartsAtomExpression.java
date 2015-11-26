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

	public SmartsAtomExpression(IChemObjectBuilder builder) {
		super(builder);
	}

	public boolean matches(IAtom atom) {
		SmartsLogicalExpression sle = new SmartsLogicalExpression();
		for (int i = 0; i < tokens.size(); i++) {
			SmartsExpressionToken tok = tokens.get(i);
			if (tok.type < SmartsConst.LO) {
				sle.addArgument(getArgument(tok, atom));
			} else
				sle.addLogOperation(tok.type - SmartsConst.LO);
		}
		return (sle.getValue());

	};

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
}
