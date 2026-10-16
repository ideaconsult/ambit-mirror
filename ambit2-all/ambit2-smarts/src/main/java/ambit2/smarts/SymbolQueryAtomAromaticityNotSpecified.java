package ambit2.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;


public class SymbolQueryAtomAromaticityNotSpecified extends QueryAtom {

	public SymbolQueryAtomAromaticityNotSpecified(IChemObjectBuilder builder) {
		super(builder);
	}

	public boolean matches(IAtom atom) {

		if (this.getSymbol().equals(atom.getSymbol()))
			return true;
		else
			return false;
	};

	public String toString() {
		return "SymbolQueryAtomAromaticityNotSpecified()";
	}
}
