package ambit2.smarts;


import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;

/**
 * This matcher any non-aromatic atom. This assumes that aromaticity in the
 * molecule has been perceived.
 *
 * This class implements deprecated CDK class
 * org.openscience.cdk.isomorphism.matchers.smarts.AliphaticAtom
 */

public class AliphaticQueryAtom extends QueryAtom {

     /**
     * Creates a new instance
     *
     */
    public AliphaticQueryAtom(IChemObjectBuilder builder) {
        super(builder);
        setFlag(IChemObject.ALIPHATIC, true);
    }

    
    @Override
    public boolean matches(IAtom atom) {
        return !atom.isAromatic();

    }

    
    @Override
    public String toString() {
        return "AliphaticQueryAtom()";
    }
}
