package ambit2.smarts;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

public class DoubleNonAromaticBond extends SMARTSBond {

	private DoubleBondStereoInfo stereoInfo = null;
	
	public DoubleNonAromaticBond(IChemObjectBuilder builder) {
		super(builder);
	}

	public boolean matches(IBond bond) {
		if (bond.getOrder() == IBond.Order.DOUBLE)
			if (!bond.getFlag(CDKConstants.ISAROMATIC))
				return (true);

		return false;
	}

	public DoubleBondStereoInfo getStereoInfo() {
		return stereoInfo;
	}

	public void setStereoInfo(DoubleBondStereoInfo stereoInfo) {
		this.stereoInfo = stereoInfo;
	};

}
