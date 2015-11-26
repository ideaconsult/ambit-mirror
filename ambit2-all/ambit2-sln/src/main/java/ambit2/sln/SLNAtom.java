package ambit2.sln;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;

/**
 * Abstract sln atom.
 * 
 */

public class SLNAtom extends SMARTSAtom {
	static final long serialVersionUID = 5327582562834894L;

	public SLNAtom(IChemObjectBuilder builder) {
		super(builder);
	}

	int atomType = 0;
	String atomName = null;
	int atomID = -1;
	int numHAtom = 0;
	SLNAtomExpression atomExpression = null;

	public boolean matches(IAtom atom) {

		// 1. Matching the atom type
		boolean FlagMatchAtomType = false;

		if (atomType == 0) // any atom
			FlagMatchAtomType = true;
		else if (atomType < SLNConst.GlobDictOffseet) // atomic symbol
		{
			FlagMatchAtomType = SLNConst.elSymbols[atomType].equals(atom
					.getSymbol());
		} else if (atomType < SLNConst.LocalDictOffseet) // It is a global
															// dictionary
															// definition
		{
			// TODO
		} else {
			// It is a local dictionary definition
			// TODO
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

}
