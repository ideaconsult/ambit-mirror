package ambit2.smarts;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryBond;

/**
 * This matches an aromatic bond.
 *
 * This class implements deprecated CDK class
 * org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond
 */

public class AmbitAromaticQueryBond extends QueryBond {

    /**
     * Creates a new instance.
     *
     */
    public AmbitAromaticQueryBond(IChemObjectBuilder builder) {
        super(builder);
        setIsAromatic(true);
    }

    /**
     * Creates a new instance
     *
     */
    public AmbitAromaticQueryBond(IQueryAtom atom1, IQueryAtom atom2, IBond.Order order, IChemObjectBuilder builder) {
        super(atom1, atom2, order, builder);
        setIsAromatic(true);
    }

    @Override
    public boolean matches(IBond bond) {
        return bond.isAromatic();
    }

    @Override
    public String toString() {
        return "AmbitAromaticQueryBond()";
    }
}
