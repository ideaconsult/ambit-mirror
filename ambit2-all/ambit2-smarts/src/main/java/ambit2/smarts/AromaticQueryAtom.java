package ambit2.smarts;


import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;

/**
* This matcher any aromatic atom. This assumes that aromaticity in the molecule
* has been perceived.
*
* This class implements deprecated CDK class
* org.openscience.cdk.isomorphism.matchers.smarts.AromaticAtom
*/

public class AromaticQueryAtom extends QueryAtom {

    
    /**
     * Creates a new instance
     *
     */
    public AromaticQueryAtom(IChemObjectBuilder builder) {
        super(builder);
        setIsAromatic(true);
    }

    
    @Override
    public boolean matches(IAtom atom) {
        return atom.isAromatic();
    }

    
    @Override
    public String toString() {
        return "AromaticQueryAtom()";
    }
}
