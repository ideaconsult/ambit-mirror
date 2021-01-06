package ambit2.sln;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;

import ambit2.sln.dictionary.AtomDictionaryObject;
import ambit2.sln.dictionary.ISLNDictionaryObject;
import ambit2.sln.dictionary.MacroAtomDictionaryObject;

/**
 * Abstract sln atom.
 * 
 */

public class SLNAtom extends SMARTSAtom {
	static final long serialVersionUID = 5327582562834894L;

	public SLNAtom(IChemObjectBuilder builder) {
		super(builder);
	}

	public int atomType = 0;
	public String atomName = null;
	public int atomID = -1;
	public int numHAtom = 0;
	public ISLNDictionaryObject dictObj = null;
	public SLNAtomExpression atomExpression = null;

	public boolean matches(IAtom atom) {

		// 1. Matching the atom type
		boolean FlagMatchAtomType = false;

		if (atomType == 0) // any atom
			FlagMatchAtomType = true;
		else if (atomType < SLNConst.GlobalDictOffseet) // atomic symbol
		{
			FlagMatchAtomType = SLNConst.elSymbols[atomType].equals(atom
					.getSymbol());
		} else if (atomType == SLNConst.GlobalDictOffseet
				|| atomType == SLNConst.LocalDictOffseet) 
		{
			// It is a global or local dictionary object
			FlagMatchAtomType = matchDictionaryObject(atom); 
		}

		if (!FlagMatchAtomType)
			return false;

		// 2. Matching the H atom count
		// TODO

		// 3. Matching the atom expression
		if (atomExpression != null)
			return atomExpression.matches(atom);

		return true;
	}

	String getHString() {
		switch (numHAtom) {
		case 0:
			return "";
		case 1:
			return "H";
		default:
			return "H" + numHAtom;
		}
	}

	public String toString() {
		if (atomExpression == null)
			return (atomName + getHString());
		else
			return (atomName + getHString() + atomExpression.toString());
	}
	
	
	boolean matchDictionaryObject(IAtom atom) 
	{
		switch (dictObj.getObjectType())
		{
		case ATOM:
			AtomDictionaryObject atomDO = (AtomDictionaryObject) dictObj;
			return atomDO.getSLNAtom().matches(atom);
		case MACRO_ATOM:
			//should not be matched here. SLNContaoner expansion is needed
			break;
		}
		
		return false;
	}

}
