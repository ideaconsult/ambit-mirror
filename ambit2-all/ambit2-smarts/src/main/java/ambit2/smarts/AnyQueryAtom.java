package ambit2.smarts;


import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;

/**
 * This matches any Atom including explicit hydrogens.
 *
 * This class implements deprecated CDK class
 * org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom
 */

public class AnyQueryAtom extends QueryAtom {

    
    /**
     * Creates a new instance
     */
    public AnyQueryAtom(IChemObjectBuilder builder) {
        super(builder);
    }

    @Override
    public boolean matches(IAtom atom) {
        return true;
    }

    
    @Override
    public String toString() {
        return "AnyQueryAtom()";
    }
}
