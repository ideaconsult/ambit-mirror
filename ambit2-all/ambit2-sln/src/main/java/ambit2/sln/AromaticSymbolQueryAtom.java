package ambit2.sln;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;


public class AromaticSymbolQueryAtom extends org.openscience.cdk.PseudoAtom implements
IQueryAtom
{
    private static final long serialVersionUID = -74489792192649224L;

    public AromaticSymbolQueryAtom() 
    {	
    }
    
    public boolean matches(IAtom atom) {
        if (atom.getFlag(CDKConstants.ISAROMATIC) && this.getSymbol().equals(atom.getSymbol())) 
        {	        	
        	return true;
        }
        else
        {	
        	return false;
        }	
    };

    public String toString() {
		return "AromaticSymbolQueryAtom()";
    }
}
