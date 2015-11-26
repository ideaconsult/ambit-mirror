package ambit2.smarts;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class SingleNonAromaticBond extends SMARTSBond {

	public SingleNonAromaticBond(IChemObjectBuilder builder) {
		super(builder);
	}

	public boolean matches(IBond bond) {
		if (bond.getOrder() == IBond.Order.SINGLE)
			if (!bond.getFlag(CDKConstants.ISAROMATIC))
				return (true);

		return false;
	};

}
