package ambit2.smarts;


import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.QueryBond;

/**
 * This matches a bond with a certain bond order.
 *
 * Daylight spec indicates that if match a single bond
 * using '-', it should be an aliphatic single bond
 *
 * This class implements deprecated CDK class
 * org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond
 */

public class AmbitOrderQueryBond extends QueryBond {


    /**
     * Creates a new instance
     *
     * @param order the order of bond
     */
    public AmbitOrderQueryBond(IBond.Order order, IChemObjectBuilder builder) {
        super(builder);
        this.setOrder(order);
    }

    
    @Override
    public boolean matches(IBond bond) {
        if (bond.isAromatic() ^ isAromatic()) return false;

        // we check for both bonds being aromatic - but the query will
        // never come in as aromatic (since there is a separate aromatic
        // query bond). But no harm in checking
        return bond.isAromatic() || bond.getOrder() == getOrder();
    }

    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("OrderQueryBond(");
        s.append(this.hashCode() + ", ");
        s.append("#O:" + getOrder());
        s.append(')');
        return s.toString();
    }
}
