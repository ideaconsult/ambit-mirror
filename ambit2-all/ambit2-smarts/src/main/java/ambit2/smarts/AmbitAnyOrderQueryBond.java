package ambit2.smarts;



import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryBond;

/**
 * This matches a bond of any order.
 *
 * This class implements deprecated CDK class
 * org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond
 */

public class AmbitAnyOrderQueryBond extends QueryBond {

    
    public AmbitAnyOrderQueryBond(IChemObjectBuilder builder) {
        super(builder);
    }

    /**
     * Creates a new instance.
     *
     * @param atom1
     * @param atom2
     */
    public AmbitAnyOrderQueryBond(IQueryAtom atom1, IQueryAtom atom2, IBond.Order order, IChemObjectBuilder builder) {
        super(atom1, atom2, order, builder);
    }

    @Override
    public boolean matches(IBond bond) {
        return bond != null; // any bond order is fine
    }

   
    @Override
    public String toString() {
        return "AmbitAnyOrderQueryBond()";
    }
}