package ambit2.smarts;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

public class TripleBondAromaticityNotSpecified extends SMARTSBond {

	public TripleBondAromaticityNotSpecified(IChemObjectBuilder builder) {
		super(builder);
	}

	public boolean matches(IBond bond) {
		if (bond.getOrder() == IBond.Order.TRIPLE)
			return (true);

		return false;
	};

}
