package ambit2.smarts;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.QueryBond;



public class DoubleBondAromaticityNotSpecified extends QueryBond {

	private DoubleBondStereoInfo stereoInfo = null;
	
	public DoubleBondAromaticityNotSpecified(IChemObjectBuilder builder) {
		super(builder);
	}

	public boolean matches(IBond bond) {
		if (bond.getOrder() == IBond.Order.DOUBLE)
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
