package ambit2.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;


public class SymbolQueryAtomAromaticityNotSpecified extends SMARTSAtom {
	
	private static final long serialVersionUID = -18035318274561647L;

    public SymbolQueryAtomAromaticityNotSpecified() {
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

